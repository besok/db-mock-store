package ru.besok.db.mock.store;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class DbUtilsTest {


  @Test
  public void isCollectionTest() throws NoSuchFieldException {
	Field self = TestCollection.class.getDeclaredField("self");
	Field list = TestCollection.class.getDeclaredField("list");
	Field set = TestCollection.class.getDeclaredField("set");
	Field array = TestCollection.class.getDeclaredField("array");

	assertFalse(DbUtils.isCollection(self));
	assertTrue(DbUtils.isCollection(list));
	assertTrue(DbUtils.isCollection(set));
	assertFalse(DbUtils.isCollection(array));
  }

  @Test
  public void camelToSnakeTest() {
	assertEquals("camel",DbUtils.camelToSnake("camel"));
	assertEquals("camel_to_snake",DbUtils.camelToSnake("camelToSnake"));
	assertEquals("camel_to_snake",DbUtils.camelToSnake("camel_to_snake"));
	assertEquals("camel__to__snake",DbUtils.camelToSnake("camel_To_Snake"));
  }

  class TestCollection{
    private TestCollection self;
    private List<Object> list;
    private Set<Object> set;
    private Object[] array;
  }
}