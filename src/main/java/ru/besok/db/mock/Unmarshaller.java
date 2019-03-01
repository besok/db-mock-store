package ru.besok.db.mock;

/**
 * Major interface for unmarshalling entity from some moveable store
 * @param <T> source type : file,url, path or etc
 * @param <S> store e.g @see {@link QueryableStore}
 * Created by Boris Zhguchev on 26/02/2019
 */
public interface Unmarshaller<T,S extends Store> {

  /**
   * unmarshaling process
   * @param source source for unmarshalling
   * @return store
   */
  S unmarshal(T source);

}
