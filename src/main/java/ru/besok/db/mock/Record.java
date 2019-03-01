package ru.besok.db.mock;

import java.util.*;

/**
 * Record for unmarshalling
 * Created by Boris Zhguchev on 28/02/2019
 */
class Record {
  String key;
  String[] columns;
  Set<String[]> records;
  JpaEntity entity;

  List<Pair> pairs;

  Optional<Object> byId(String id){
	for (Pair pair : pairs) {
	  if (pair.id.equals(id)) {
		return Optional.of(pair.object);
	  }
	}
	return Optional.empty();
  }

  Record(String key) {
	this.key = key;
	this.records = new HashSet<>();
	this.pairs = new ArrayList<>();
  }

  int idx(String colName) {
	return UnmarshalUtils.idx(colName, columns);
  }

  void setEntity(JpaEntity entity) {
	this.entity = entity;
  }

  // TODO: 27.02.2019 check len is equal with columns
  void add(String[] record) {
	if (columns.length != record.length) {
	  throw new UnmarshallerException(" wrong record " + Arrays.toString(record));
	}
	records.add(record);
  }

  void setColumns(String[] columns) {
	this.columns = columns;
  }

  Pair addPair(String[] rawRecord, Object object) {
	Pair p = new Pair(object, rawRecord);
	pairs.add(p);
	return p;
  }

  static class Pair {
	Object object;
	String[] record;
	String id;

	public Pair(Object object, String[] record) {
	  this.object = object;
	  this.record = record;
	}
  }

}
