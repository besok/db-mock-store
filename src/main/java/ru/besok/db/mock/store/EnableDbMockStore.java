package ru.besok.db.mock.store;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
public @interface EnableDbMockStore {
  String value() default "";
}
