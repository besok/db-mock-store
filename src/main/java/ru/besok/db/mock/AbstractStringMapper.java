package ru.besok.db.mock;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 28/02/2019
 */
public abstract class AbstractStringMapper implements StringMapper {
  @Override
  public StringFucntion<UUID> uuid() {
	return UUID::fromString;
  }

  @Override
  public StringFucntion<String> string() {
	return e -> e;
  }

  @Override
  public StringFucntion<BigDecimal> bigDecimal() {
	return BigDecimal::new;
  }

  @Override
  public StringFucntion<Integer> integerVal() {
	return Integer::valueOf;
  }

  @Override
  public StringFucntion<Double> doubleVal() {
	return Double::valueOf;
  }

  @Override
  public StringFucntion<Date> date() {
	return e -> new SimpleDateFormat().parse(e);
  }

  @Override
  public StringFucntion<LocalDateTime> localDateTime() {
	return LocalDateTime::parse;
  }

  @Override
  public StringFucntion<LocalDate> localDate() {
	return LocalDate::parse;
  }

  @Override
  public StringFucntion<Timestamp> timestamp() {
	return Timestamp::valueOf;
  }

  @Override
  public StringFucntion<Character> character() {
	return e -> e.toCharArray()[0];
  }

  @Override
  public StringFucntion<byte[]> bytes() {
	return String::getBytes;
  }

  @Override
  public StringFucntion<Boolean> booleanVal() {
	return Boolean::new;
  }

  @Override
  public StringFucntion<Long> longVal() {
	return Long::valueOf;
  }

  @Override
  public StringFucntion<Short> shortVal() {
	return Short::valueOf;
  }

  @Override
  public StringFucntion<? extends Enum<?>> enumVal() {
	return null;
  }
}
