package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaAnnotationScannerTest {


  @Test
  public void scanTest() {
	JpaEntityStore store = JpaAnnotationScanner.scan("ru.besok.db.mock.store.data.common");
	Assert.assertEquals(store.getEntities().size(),8);
  }
}