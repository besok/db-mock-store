package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Boris Zhguchev on 23/02/2019
 */
public class JpaEntityTest {


  @Test
  public void byCompareDbTableTest() {
	JpaEntity jpaEntity = new JpaEntity();
	jpaEntity.setHeader("Name","Table","Schema");

	Assert.assertTrue(jpaEntity.compareByDbHeader("Schema","Table"));
	Assert.assertFalse(jpaEntity.compareByDbHeader("Schema","Table1"));
  }


  @Test
  public void setDepValTest() {


  }

  @Test
  public void isPlainTest() {
	JpaEntity e = new JpaEntity();
	Assert.assertTrue(e.isPlain());
	e.addDep(new JpaDependency(null,null,e,"", ""));
	Assert.assertFalse(e.isPlain());
  }
}