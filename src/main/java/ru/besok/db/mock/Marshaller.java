package ru.besok.db.mock;

import java.util.Collection;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public interface Marshaller<T> {

 <E> boolean marshal(T destination,Collection<E> objects);

}
