package ru.besok.db.mock;

import org.reflections.Store;

import java.util.*;

import static ru.besok.db.mock.JpaDependency.Type.*;
import static ru.besok.db.mock.ReflectionUtils.*;
import static ru.besok.db.mock.UnmarshalUtils.findByIdx;
import static ru.besok.db.mock.UnmarshalUtils.split;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
class InnerStore extends Store {

  private Map<String, Record> recordMap;
  private JpaEntityStore entityStore;
  private StringMapper mapper;

  InnerStore(JpaEntityStore store, StringMapper stringMapper) {
	this.entityStore = store;
	this.mapper = stringMapper;
	this.recordMap = new HashMap<>();
  }

  static InnerStore buildObjects(InnerStore store) {
	Collection<Record> records = store.getRecordMap().values();
	StringMapper m = store.mapper;
	for (Record record : records) {
	  JpaEntity entity = record.entity;
	  for (String[] rawRecord : record.records) {
		Object object = entity.newInstance();
		Record.Pair p = record.addPair(rawRecord, object);

		// set id
		JpaId jpaId = entity.getId();
		findByIdx(record.idx(jpaId.getColumn()), rawRecord)
		  .ifPresent(id -> {
			entity.setIdValue(object, m.generate(jpaId.getField().getType(), id));
			p.id = id;
		  });
		// set plains
		entity.getColumns().forEach(c ->
		  findByIdx(record.idx(c.getName()), rawRecord)
			.ifPresent(val -> setValue(object, c.getField(), m.generate(c.getField().getType(), val))));
	  }
	}
	return store;
  }

  // TODO: 28.02.2019 Refactor cyclic hell
  static InnerStore buildObjectRelations(InnerStore store) {
	for (Record record : store.getRecordMap().values()) {
	  for (Record.Pair pair : record.pairs) {
		manyToOneDependencies(store, record, record.entity, pair);
		oneToOneDependencies(store, record, record.entity, pair);
	  }
	}
	return store;
  }

  private static void oneToOneDependencies(InnerStore store, Record record, JpaEntity entity, Record.Pair pair) {
	entity
	  .getDependenciesByType(O2O)
	  .forEach(d -> {
		if (d.getMappedBy().isEmpty()) {
		  boolean joinById = Objects.equals(d.getColumn(), d.getEntity().getId().getColumn());
		  if (joinById) {
		  } else {
			mapByField(store, record, entity, pair, d);
		  }
		} else {

		}
	  });
  }

  private static void mapByField(InnerStore store, Record record, JpaEntity entity, Record.Pair pair, JpaDependency d) {
	String column = d.getColumn();
	String mappedName = d.getField().getName();
	d.getEntity().getDependenciesByType(O2O).stream()
	  .filter(nD -> nD.getMappedBy().equals(mappedName)).findAny()
	  .ifPresent(nD -> findByIdx(record.idx(column), pair.record)
		.ifPresent(idStr -> store.findDepById(idStr, d.getEntity())
		  .ifPresent(dObject -> {
			entity.setDependencyValue(pair.object, d.getColumn(), d.getEntity(), dObject);
			d.getEntity().setDependencyValue(dObject, nD.getColumn(), nD.getEntity(), pair.object);
		  })));
  }

  private static void manyToOneDependencies(InnerStore store, Record record, JpaEntity entity, Record.Pair pair) {
	entity
	  .getDependenciesByType(M2O)
	  .forEach(d -> findByIdx(record.idx(d.getColumn()), pair.record)
		.ifPresent(dV ->
		  store.findDepById(dV, d.getEntity())
			.ifPresent(dO -> {
			  Object object = pair.object;
			  entity.setDependencyValue(object, d.getColumn(), d.getEntity(), dO);
			  d.getEntity().setDependencyValueToCol(dO, d.getField().getName(), object);
			})));
  }

  Optional<Object> findDepById(String id, JpaEntity entity) {
	for (Record record : recordMap.values()) {
	  if (record.entity.equals(entity)) {
		return record.byId(id);
	  }
	}
	return Optional.empty();
  }

  Record initRecord(String key, List<String> records) {
	Record record = recordMap.get(key);
	if (Objects.isNull(record)) {
	  record = new Record(key);
	  JpaEntity jpaEntity =
		entityStore.bySchemaTable(key)
		  .orElseThrow(() -> new UnmarshallerException("can not find entity with schema.table : " + key));
	  record.setEntity(jpaEntity);
	  recordMap.put(key, record);
	}
	if (records.isEmpty()) {
	  throw new UnmarshallerException(" raw records must not be empty for " + key);
	}
	record.setColumns(split(records.get(0)));
	for (int i = 1; i < records.size(); i++) {
	  record.add(split(records.get(i)));
	}
	return record;
  }


  Map<String, Record> getRecordMap() {
	return recordMap;
  }


}
