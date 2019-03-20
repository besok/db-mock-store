package ru.besok.db.mock;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * utils for reflection
 * Created by Boris Zhguchev on 23/02/2019
 */
class ReflectionUtils {

  static Optional<Class<?>> typeFromField(Field f) {
	if (!isCollection(f.getType())) {
	  return Optional.of(f.getType());
	}

	Type gt = f.getGenericType();
	if (gt instanceof ParameterizedType) {
	  ParameterizedType parType = (ParameterizedType) gt;
	  Class<?> type = (Class<?>) parType.getActualTypeArguments()[0];
	  return Optional.ofNullable(type);
	}
	throw new CastCollectionException(" got an error while getting generic type from collection from field["
	  + fieldName(f) + "]. " + "Probably the collection has a raw type");
  }

  static boolean isCollection(Class<?> objClass) {
	return Collection.class.isAssignableFrom(objClass);
  }

  public static Object getValue(Object entity, Field field) {
	field.setAccessible(true);
	try {
	  return field.get(entity);
	} catch (IllegalAccessException e) {
	  throw new IllegalStateDbMockException(" error to get from field " + fieldName(field), e);
	}
  }

  public static Object getValue(Object entity, String field) {
	try {
	  return getValue(entity, entity.getClass().getDeclaredField(field));
	} catch (Throwable e) {
	  throw new IllegalStateDbMockException(" error to get from field " + field, e);
	}
  }

  public static Object setValue(Object entity, Field field, Object value) {
	field.setAccessible(true);
	try {
	  field.set(entity, value);
	  return value;
	} catch (IllegalAccessException e) {
	  throw new IllegalStateDbMockException("error to get from field " + fieldName(field), e);
	}
  }

  static String fieldName(Field field) {
	return field.getDeclaringClass().getSimpleName() + "#" + field.getName();
  }

  static  public Object getEnumByVal(Class<?> clazz, Object value) {
	  Object[] constants = clazz.getEnumConstants();
	  for (int i = 0; i < constants.length; i++) {
		  if (constants[i].toString().equals(value.toString())) {
			  return i;
		  }
	  }
	  return "";
  }
  static Object newCollection(Field field) {
	Class<?> type = field.getType();
	try {
	  if (!type.isInterface()) {
		return type.newInstance();
	  }
	  switch (type.getSimpleName()) {
		case "Set":
		  return HashSet.class.newInstance();
		case "List":
		  return ArrayList.class.newInstance();
		default:
		  return null;
	  }
	} catch (IllegalAccessException | InstantiationException e) {
	  throw new IllegalStateDbMockException(" impossible init new collection from " + fieldName(field));
	}

  }
}
