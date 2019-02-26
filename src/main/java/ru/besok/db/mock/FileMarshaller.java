package ru.besok.db.mock;

import org.hibernate.Hibernate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;
import static java.util.stream.Collectors.*;
import static ru.besok.db.mock.JpaDependency.Type.*;
import static ru.besok.db.mock.JpaUtils.*;
import static ru.besok.db.mock.JpaUtils.DELIM;
import static ru.besok.db.mock.ReflectionUtils.*;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public class FileMarshaller implements Marshaller<Path> {

  protected Map<String, Value> valueStore;
  private Logger logger = Logger.getLogger(FileMarshaller.class.getName());
  private JpaEntityStore metaStore;

  public FileMarshaller(JpaEntityStore store) {
	this.metaStore = store;
	this.valueStore = new HashMap<>();
  }


  @Override
  public <T> boolean marshal(Path destination, Collection<T> objects) {
	if (objects.isEmpty()) {
	  throw new IllegalStateDbMockException(" object collection for marshalling must not be empty");
	}

	for (Object object : objects) {
	  boolean res = putToStore(object);
	}


	if (Files.isDirectory(destination)) {
	  valueStore.values()
		.forEach(v -> {
		  Path newFile = destination.resolve(v.getClassHeader());
		  String records = v.prepareExClass();
		  toFile(newFile, records);
		});
	} else {
	  String records = valueStore.values().stream().map(Value::prepare).collect(joining());
	  toFile(destination, records);
	}

	return false;
  }

  protected boolean putToStore(Object object) {
	Object unproxyObject = unproxy(object);
	JpaEntity je = metaStore.byClass(unproxyObject.getClass())
	  .orElseThrow(
		() -> new JpaAnnotationScanException(" class " + unproxyObject.getClass()
		  + " not found in the meta metaStore."));

	String classHeader = je.dbHeader();
	Value value = getOrInitValue(classHeader);

	value.setColumnHeader(makeColHeader(je));
	value.add(makeRecord(unproxyObject, je));

	logger.info(" put object " + object.getClass() + " to store ");
	return true;
  }

  protected String makeRecord(Object object, JpaEntity je) {
	String idValue = quotesWrap(je.getIdValue(object).toString());

	String columns =
	  je.getColumns().stream()
		.map(c -> getValue(object, c.getField()))
		.map(o -> Objects.isNull(o) ? "" : o.toString())
		.map(JpaUtils::quotesWrap)
		.collect(joining(DELIM));
	String depManyOne =
	  je.getDependenciesByType(M2O).stream()
		.map(d -> putToStoreThenGetId(object, d))
		.map(JpaUtils::quotesWrap).collect(joining(DELIM));
	String depOneOne =
	  je.getDependenciesByType(O2O).stream()
		.map(d -> putToStoreThenGetId(object, d))
		.map(JpaUtils::quotesWrap).collect(joining(DELIM));

	return concat(DELIM, idValue, columns, depManyOne, depOneOne);

  }

  private String putToStoreThenGetId(Object object, JpaDependency d) {
	Object depValue = unproxy(getValue(object, d.getField()));
	if (Objects.isNull(depValue))
	  return "";
	putToStore(depValue);
	Object depIdVal = d.getEntity().getIdValue(depValue);
	return depIdVal.toString();
  }


  protected String makeColHeader(JpaEntity je) {

	String columnId = quotesWrap(je.getId().getColumn());

	String columns =
	  je.getColumns().stream()
		.map(JpaColumn::getName)
		.map(JpaUtils::quotesWrap)
		.collect(joining(DELIM));

	String depManyOne = je.getDependenciesByType(M2O).stream()
	  .map(JpaDependency::getColumn)
	  .map(JpaUtils::quotesWrap)
	  .collect(joining(DELIM));

	String depOneOne = je.getDependenciesByType(O2O).stream()
	  .map(JpaDependency::getColumn)
	  .map(JpaUtils::quotesWrap)
	  .collect(joining(DELIM));

	return concat(DELIM, columnId, columns, depManyOne, depOneOne);
  }

  protected Value getOrInitValue(String classHeader) {

	Value oldVal = valueStore.get(classHeader);

	if (Objects.isNull(oldVal)) {
	  Value value = new Value(classHeader);
	  valueStore.put(classHeader, value);
	  return value;
	}

	return oldVal;
  }


  protected Path toFile(Path dst, String object) {
	try {
	  return Files.write(dst, object.getBytes(), CREATE, WRITE);
	} catch (IOException e) {
	  throw new IllegalStateDbMockException("can't marshal to file[" + dst.getFileName() + "]", e);
	}
  }

  private Object unproxy(Object object) {
	return Hibernate.unproxy(object);
  }

  static class Value {
	final String delimiter = "_@@@_";
	String classHeader;
	String columnHeader;
	Set<String> records;

	Value(String classHeader) {
	  this.classHeader = classHeader;
	  this.records = new HashSet<>();
	}

	Value add(String record) {
	  records.add(record);
	  return this;
	}

	public String getClassHeader() {
	  return classHeader;
	}

	void setClassHeader(String classHeader) {
	  this.classHeader = classHeader;
	}

	void setColumnHeader(String columnHeader) {
	  this.columnHeader = columnHeader;
	}

	String prepare() {
	  StringBuilder sb = new StringBuilder();
	  sb.append(delimiter).append(classHeader).append("\n")
		.append(columnHeader).append("\n");

	  for (String record : records) {
		sb.append(record).append("\n");
	  }
	  return sb.toString();
	}

	String prepareExClass() {
	  StringBuilder sb = new StringBuilder();
	  sb.append(columnHeader).append("\n");

	  for (String record : records) {
		sb.append(record).append("\n");
	  }
	  return sb.toString();
	}


  }

}
