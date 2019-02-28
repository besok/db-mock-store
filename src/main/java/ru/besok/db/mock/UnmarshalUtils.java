package ru.besok.db.mock;

import java.util.Optional;

import static ru.besok.db.mock.JpaUtils.DELIM;
import static ru.besok.db.mock.JpaUtils.quotesWrap;

/**
 * Created by Boris Zhguchev on 28/02/2019
 */
public class UnmarshalUtils {
  static String[] split(String val) {
	String[] splitedVal = val.split(quotesWrap(DELIM));
	int len = splitedVal.length;
	if (len > 0) {
	  String first = splitedVal[0];
	  if (first.indexOf("\"") == 0) {
		splitedVal[0] = first.substring(1);
	  }
	  String last = splitedVal[len - 1];
	  int lastLen = last.length();
	  if (last.lastIndexOf("\"") == lastLen - 1) {
		splitedVal[len - 1] = last.substring(0, lastLen - 1);
	  }
	}
	return splitedVal;
  }

  static int idx(String colName, String[] columns) {
	for (int i = 0; i < columns.length; i++) {
	  if (columns[i].equals(colName)) {
		return i;
	  }
	}
	return -1;
  }

  static Optional<String> findByIdx(int idx, String[] record) {
	if (idx < 0 || idx > record.length) {
	  return Optional.empty();
	}
	String r = record[idx];

	return Optional.ofNullable(r.isEmpty() ? null : r);
  }
}
