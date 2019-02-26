package ru.besok.db.mock;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public interface Unmarshaller<T,S extends Store> {

  S unmarshal(T source);

}
