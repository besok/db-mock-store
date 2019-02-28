package ru.besok.db.mock;

import java.util.Set;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public class QueryableStore implements Store {
  private Set<Record> records;

  public QueryableStore(Set<Record> records) {
	this.records = records;
  }
}
