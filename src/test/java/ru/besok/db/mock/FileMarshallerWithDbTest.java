package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.besok.db.mock.data.common.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.besok.db.mock.JpaAnnotationScanner.scan;
import static ru.besok.db.mock.JpaEntityStore.buildRelations;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FileMarshallerWithDbTest {


  @Autowired
  private CustomerRepo customerRepo;
  @Autowired
  private OrderRepo orderRepo;
  @Autowired
  private CityRepo cityRepo;

  @Before
  public void setUp() throws Exception {

	City city = new City();
	city.setId(1);
	city.setCode(100);

	cityRepo.save(city);

	Customer customer = new Customer();
	customer.setName("name");
	customer.setLogin("login");

	customer.setCity(city);
	customer.setMail("mail");

	customerRepo.save(customer);



	Order order = new Order();
	order.setId(1);
	order.setAmount(10);
	order.setComment("comment");
	order.setCustomer(customer);
	order.setOrderTime(LocalDateTime.of(2019,1,1,1,1));

  	orderRepo.save(order);
  }

  @Test
  public void putToStoreTest() {
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileMarshaller fileMarshaller = new FileMarshaller(store);
	Order order = orderRepo.findAll().get(0);

	fileMarshaller.putToStore(order);

	Map<String, FileMarshaller.Value> valueStore = fileMarshaller.valueStore;
	FileMarshaller.Value value = valueStore.get(store.byClass(Order.class).get().dbHeader());
	Set<String> records = value.records;
	for (String record : records) {
	  Assert.assertEquals("\"2\";\"comment\";\"10\";\"2019-01-01T01:01\";\"1\";\"\"",record);
	}
  }
}