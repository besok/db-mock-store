package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Boris Zhguchev on 23/02/2019
 */
public class ReflectionUtilsTest {

  @Test(expected = CastCollectionException.class)
  public void getTypeFromCollectionTest() throws NoSuchFieldException {
	class T{
	  String s;
	  List<String> strs;
	  List raws;
	}
	Field s = T.class.getDeclaredField("s");
	Field strs = T.class.getDeclaredField("strs");

	Assert.assertEquals(ReflectionUtils.typeFromField(s),Optional.empty());
	Assert.assertEquals(ReflectionUtils.typeFromField(strs),Optional.of(String.class));

	Field raws = T.class.getDeclaredField("raws");
	Assert.assertEquals(ReflectionUtils.typeFromField(raws),Optional.empty());
  }

  @Test
  public void isCollectionTest() throws NoSuchFieldException {
	class TestCollection{
	  private TestCollection self;
	  private List<Object> list;
	  private Set<Object> set;
	  private Object[] array;
	}
	Field self = TestCollection.class.getDeclaredField("self");
	Field list = TestCollection.class.getDeclaredField("list");
	Field set = TestCollection.class.getDeclaredField("set");
	Field array = TestCollection.class.getDeclaredField("array");

	assertFalse(ReflectionUtils.isCollection(self.getType()));
	assertTrue(ReflectionUtils.isCollection(list.getType()));
	assertTrue(ReflectionUtils.isCollection(set.getType()));
	assertFalse(ReflectionUtils.isCollection(array.getType()));
  }



}