package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;
import ru.besok.db.mock.data.common.*;


import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.Assert.*;
import static ru.besok.db.mock.JpaUtils.*;
import static ru.besok.db.mock.JpaDependency.Property.*;
import static ru.besok.db.mock.JpaDependency.Type.M2O;
import static ru.besok.db.mock.JpaDependency.Type.O2M;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaUtilsTest {

  @Test
  public void initEntityWithHeaderTest() {
	assertEquals(initEntityWithHeader(City.class).dbHeader(),"test.city");
	assertEquals(initEntityWithHeader(Payment.class).dbHeader(),"test.payment_cust");
	assertEquals(initEntityWithHeader(PaymentInfo.class).dbHeader(),"payment_info");
  }

  @Test
  public void camelToSnakeTest() {
	assertEquals("camel",JpaUtils.camelToSnake("camel"));
	assertEquals("camel_to_snake",JpaUtils.camelToSnake("camelToSnake"));
	assertEquals("camel_to_snake",JpaUtils.camelToSnake("camel_to_snake"));
	assertEquals("camel__to__snake",JpaUtils.camelToSnake("camel_To_Snake"));
  }

  @Test
  public void findIdInFieldTest() throws NoSuchFieldException {
	Class<City> cityClass = City.class;
	Optional<JpaId> idOpt = findIdInField(cityClass.getDeclaredField("id"));
	Optional<JpaId> codeOpt = findIdInField(cityClass.getDeclaredField("code"));
	Assert.assertTrue(idOpt.isPresent());
	Assert.assertFalse(idOpt.get().isGenerated());
	assertEquals(idOpt.get().getColumn(),"id");
	Assert.assertFalse(codeOpt.isPresent());
  }

  @Test
  public void processManyToOneTest() throws NoSuchFieldException {
	JpaEntity j = initEntityWithHeader(Customer.class);
	Field fieldCity = Customer.class.getDeclaredField("city");
	processManyToOne(j, fieldCity);

	Optional<JpaDependency> depOpt = j.findDep(fieldCity);
	Assert.assertTrue(depOpt.isPresent());

	JpaDependency d = depOpt.get();

	assertEquals(d.getField(),fieldCity);
	assertEquals(d.getColumn(),"city_id");
	assertEquals(d.getParent(),j);
	assertEquals(d.getMappedBy(),"");
	Assert.assertEquals(d.getType(),M2O);
	Assert.assertTrue(d.property(OPTIONAL));
	Assert.assertFalse(d.property(JOIN_PRIMARY_KEYS));
	Assert.assertFalse(d.property(ALWAYS_NEW));


  }
  @Test
  public void processOneToManyTest() throws NoSuchFieldException {
	JpaEntity j = initEntityWithHeader(Manager.class);
	Field list = Manager.class.getDeclaredField("underManagers");
	processOneToMany(j, list);

	Optional<JpaDependency> depOpt = j.findDep(list);
	Assert.assertTrue(depOpt.isPresent());

	JpaDependency d = depOpt.get();

	assertEquals(d.getField(),list);
	assertEquals(d.getColumn(),"underManagers");
	assertEquals(d.getParent(),j);
	assertEquals(d.getMappedBy(),"innerManager");
	Assert.assertEquals(d.getType(),O2M);
	Assert.assertTrue(d.property(OPTIONAL));
	Assert.assertFalse(d.property(JOIN_PRIMARY_KEYS));
	Assert.assertFalse(d.property(ALWAYS_NEW));


  }
  @Test
  public void processPlainTest() throws NoSuchFieldException {
	JpaEntity j = initEntityWithHeader(City.class);
	Field code = City.class.getDeclaredField("code");
	Field title = City.class.getDeclaredField("name");
	processPlain(j, code);
	processPlain(j, title);

	Optional<JpaColumn> cOpt = j.findCol(code);
	Optional<JpaColumn> t1Opt = j.findCol(title);
	Assert.assertTrue(cOpt.isPresent());
	Assert.assertTrue(t1Opt.isPresent());

	JpaColumn c = cOpt.get();
	JpaColumn t = t1Opt.get();

	assertEquals(c.getField(),code);
	assertEquals(c.getName(),"code");
	assertEquals(t.getName(),"title");
  }

  @Test
  public void concatTest() {
	String concat = JpaUtils.concat(";", "aaa", "bbb", "ccc", "ddd");
	Assert.assertEquals("aaa;bbb;ccc;ddd",concat);
  }
}