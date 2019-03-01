package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;
import ru.besok.db.mock.data.common.Customer;
import ru.besok.db.mock.data.common.Order;
import ru.besok.db.tests.AbstractJpaFileMock;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Boris Zhguchev on 01/03/2019
 */
public class QueryableStoreTest extends AbstractJpaFileMock {


  public QueryableStoreTest() {
	super("ru.besok.db.mock.data.common");
  }

  @Test
  public void allTest() {
	QueryableStore store = store("test_dir");
	List<Order> orders = store.all(Order.class);
	Assert.assertEquals(orders.size(),1);
	Optional<Customer> customerOptional = store.any(Customer.class);
	Assert.assertTrue(customerOptional.isPresent());
  }

  @Test
  public void byIdTest() {
	QueryableStore store = store("test_dir");
	Optional<Order> orderOpt = store.byId(Order.class,1);
	Assert.assertTrue(orderOpt.isPresent());
	Optional<Order> orderOptWrong = store.byId(Order.class,10);
	Assert.assertFalse(orderOptWrong.isPresent());
  }

  @Test
  public void byFieldTest() {
	QueryableStore store = store("test_dir");
	Optional<Order> orderOpt = store.anyByField(Order.class,"customer.name","name");
	Assert.assertTrue(orderOpt.isPresent());
	Optional<Order> orderOptWrong = store.anyByField(Order.class,"customer.name","nameWrong");
	Assert.assertFalse(orderOptWrong.isPresent());

  }
}