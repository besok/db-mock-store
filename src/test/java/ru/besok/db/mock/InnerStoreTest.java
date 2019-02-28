package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;

import static ru.besok.db.mock.InnerStore.*;
import static ru.besok.db.mock.UnmarshalUtils.split;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public class InnerStoreTest {
  @Test
  public void parseStringTest() {


	String[] val1 = split("\"a\";\"b\";\"\"");
	String[] val2 = split("\"a\";\"b;\";\"c\"\"d\"");

	Assert.assertArrayEquals(new String[]{"a","b",""},val1);
	Assert.assertArrayEquals(new String[]{"a","b;","c\"\"d"},val2);
  }
}