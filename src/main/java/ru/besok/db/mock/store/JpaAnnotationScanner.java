package ru.besok.db.mock.store;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import ru.besok.db.mock.store.meta.jpa.JpaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

import static ru.besok.db.mock.store.DbUtils.*;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaAnnotationScanner {
  public void scan(String entityPackage) {
	checkNull(entityPackage);

	Set<Class<?>> classes = scanPackage(entityPackage);

	for (Class<?> cl : classes) {
	  if (cl.isAnnotationPresent(Entity.class)) {
		JpaEntity jpaEntity = initEntity(cl);
	  }
	}

  }


   JpaEntity initEntity(Class<?> cl) {
	JpaEntity entity = new JpaEntity();
	String className = cl.getSimpleName();
	if (cl.isAnnotationPresent(Table.class)) {
	  Table tbl = cl.getAnnotation(Table.class);
	  entity.setHeader(className, tbl.name(), tbl.schema());
	} else {
	  entity.setHeader(className, camelToSnake(className), "");
	}

	return entity;
  }

  private void checkNull(String entityPackage) {
	if (Objects.isNull(entityPackage) || entityPackage.isEmpty()) {
	  throw new JpaAnnotationScanException("package for scan is null or empty");
	}
  }

  Set<Class<?>> scanPackage(String entityPackage) {
	return new Reflections(entityPackage, new SubTypesScanner(false)).getSubTypesOf(Object.class);
  }
}
