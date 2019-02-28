package ru.besok.db.mock;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public class QueryableStore implements Store {
  private InnerStore store;

  public QueryableStore(InnerStore store) {
	this.store = store;
  }
}
