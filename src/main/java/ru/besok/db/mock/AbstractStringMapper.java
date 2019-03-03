package ru.besok.db.mock;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * abstract string mapper @see StringMapper
 *
 * @apiNote This class is preferable for implementation than StringMapper
 *
 * Created by Boris Zhguchev on 28/02/2019
 */
public abstract class AbstractStringMapper implements StringMapper {
  @Override
  public StringFunction<UUID> uuid() {
	return UUID::fromString;
  }

  @Override
  public StringFunction<String> string() {
	return e -> e;
  }

  @Override
  public StringFunction<BigDecimal> bigDecimal() {
	return BigDecimal::new;
  }

  @Override
  public StringFunction<Integer> integerVal() {
	return Integer::valueOf;
  }

  @Override
  public StringFunction<Double> doubleVal() {
	return Double::valueOf;
  }

  @Override
  public StringFunction<Date> date() {
	return e -> new SimpleDateFormat().parse(e);
  }

  @Override
  public StringFunction<LocalDateTime> localDateTime() {
	return LocalDateTime::parse;
  }

  @Override
  public StringFunction<LocalDate> localDate() {
	return LocalDate::parse;
  }

  @Override
  public StringFunction<Timestamp> timestamp() {
	return Timestamp::valueOf;
  }

  @Override
  public StringFunction<Character> character() {
	return e -> e.toCharArray()[0];
  }

  @Override
  public StringFunction<byte[]> bytes() {
	return String::getBytes;
  }

  @Override
  public StringFunction<Boolean> booleanVal() {
	return Boolean::new;
  }

  @Override
  public StringFunction<Long> longVal() {
	return Long::valueOf;
  }

  @Override
  public StringFunction<Short> shortVal() {
	return Short::valueOf;
  }

  @Override
  public StringFunction<? extends Enum<?>> enumVal() {
	return null;
  }
}
