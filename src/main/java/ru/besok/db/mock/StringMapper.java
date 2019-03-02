package ru.besok.db.mock;
// 2018.07.24 

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Basic interface supplier for generating values for plain fields.
 *
 * @author Boris Zhguchev
 */
public interface StringMapper {


  StringFucntion<UUID> uuid();

  StringFucntion<String> string();

  StringFucntion<BigDecimal> bigDecimal();

  StringFucntion<Integer> integerVal();

  StringFucntion<Double> doubleVal();

  StringFucntion<Date> date();

  StringFucntion<LocalDateTime> localDateTime();

  StringFucntion<LocalDate> localDate();

  StringFucntion<Timestamp> timestamp();

  StringFucntion<Character> character();

  StringFucntion<byte[]> bytes();

  StringFucntion<Boolean> booleanVal();

  StringFucntion<Long> longVal();

  StringFucntion<Short> shortVal();

  StringFucntion<? extends Enum<?>> enumVal();

  default Object generate(Class<?> clazz, String val) {
	try {
	  if (val.isEmpty()) {
		return null;
	  }

	  switch (clazz.getSimpleName()) {
		case "String":
		  return string().apply(val);
		case "UUID":
		  return uuid().apply(val);
		case "BigDecimal":
		  return bigDecimal().apply(val);
		case "Integer":
		case "int":
		  return integerVal().apply(val);
		case "Double":
		case "double":
		  return doubleVal().apply(val);
		case "Date":
		  return date().apply(val);
		case "short":
		case "Short":
		  return shortVal().apply(val);
		case "LocalDateTime":
		  return localDateTime().apply(val);
		case "LocalDate":
		  return localDate().apply(val);
		case "Timestamp":
		  return timestamp().apply(val);
		case "Character":
		case "char":
		  return character().apply(val);
		case "byte[]":
		  return bytes().apply(val);
		case "boolean":
		case "Boolean":
		  return booleanVal().apply(val);
		case "long":
		case "Long":
		  return longVal().apply(val);
	  }

	  if (!Objects.isNull(clazz.getSuperclass())
		&& clazz.getSuperclass().equals(Enum.class)) {
		return enumVal().apply(val);
	  }
	} catch (Throwable e) {
	  throw new StringMapExeption(" couldn't transform an input value =" + val + " to class=" + clazz.getName()
		+ ", for custom mapping override a method which processes '" + clazz.getSimpleName() + "' from ", e);
	}
	return null;
  }

  interface StringFucntion<V> {
	V apply(String s) throws Exception;
  }
}
