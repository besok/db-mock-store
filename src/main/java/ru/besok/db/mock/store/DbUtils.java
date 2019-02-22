package ru.besok.db.mock.store;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class DbUtils {

  public static String camelToSnake(String field) {
	Queue<Character> q = new ArrayDeque<>();
	int i = 0;
	for (char c : field.toCharArray()) {
	  if (Character.isUpperCase(c)) {
		if (i != 0) {
		  q.add('_');
		}
		q.add(Character.toLowerCase(c));
	  } else {
		q.add(c);
	  }
	  i++;
	}
	StringBuilder sb = new StringBuilder();
	for (Character ch : q) {
	  sb.append(ch);
	}
	return sb.toString();
  }
  public  static boolean isCollection(Field field) {
	return Collection.class.isAssignableFrom(field.getType());
  }


}
