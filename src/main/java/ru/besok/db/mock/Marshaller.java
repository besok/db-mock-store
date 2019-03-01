package ru.besok.db.mock;

import java.util.Collection;

/**
 * Major interface for marshalling entity to some moveable store
 * @param <T> destination type (Path,File,Url and etc)
 * Created by Boris Zhguchev on 26/02/2019
 */
public interface Marshaller<T> {

  /**
   *
   * @param destination destination for savinf
   * @param objects objects for saving
   * @param <E> type for objects
   * @return result for saving
   */
 <E> boolean marshal(T destination,Collection<E> objects);

}
