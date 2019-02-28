package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;
import ru.besok.db.mock.data.common.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static ru.besok.db.mock.JpaAnnotationScanner.*;
import static ru.besok.db.mock.JpaEntityStore.*;

/**
 * Created by Boris Zhguchev on 26/02/2019
 */
public class FileMarshallerTest {

  @Test
  public void prepareTest() {
	FileMarshaller.Value value = new FileMarshaller.Value("classHeader");
	value.columnHeader = "columnHeader";
	value.add("1;record");
	value.add("2;record");

	String expectedString = "_@@@_classHeader\ncolumnHeader\n1;record\n2;record\n";
	Assert.assertEquals(expectedString, value.prepare());
  }
  @Test
  public void prepareExClassTest() {
	FileMarshaller.Value value = new FileMarshaller.Value("classHeader");
	value.columnHeader = "columnHeader";
	value.add("1;record");
	value.add("2;record");

	String expectedString = "columnHeader\n1;record\n2;record\n";
	Assert.assertEquals(expectedString, value.prepareExClass());
  }

  @Test
  public void toFileTest() {
	FileMarshaller m = new FileMarshaller(new JpaEntityStore());
	String originalString = "_@@@_classHeader\ncolumnHeader\n1;record\n2;record\n";
	Path p = Paths.get("src/test/resources/test");

	m.toFile(p, originalString);

	try (Stream<String> pathStream = Files.lines(p)) {
	  String resultString = pathStream.collect(Collectors.joining("\n","","\n"));
	  Assert.assertEquals(resultString,originalString);
	} catch (IOException e) {
	  e.printStackTrace();
	  Assert.fail();
	}
  }

  @Test
  public void makeColHeaderTest() {
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	JpaEntity je = store.byClass(Manager.class).get();
	FileMarshaller fileMarshaller = new FileMarshaller(store);
	String header = fileMarshaller.makeColHeader(je);
	Assert.assertEquals("\"id\";\"name\";\"mail\";\"inner_manager_id\"",header);
  }

  @Test
  public void putToStoreTest() {
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileMarshaller fileMarshaller = new FileMarshaller(store);

	Order order = new Order();
	order.setId(1);
	order.setAmount(10);
	order.setComment("comment");

	Customer customer = new Customer();
	customer.setId(1);
	customer.setName("name");
	customer.setLogin("login");
	City city = new City();city.setId(1);city.setCode(100);
	customer.setCity(city);
	customer.setMail("mail");
	order.setCustomer(customer);
	order.setOrderTime(LocalDateTime.now());

	fileMarshaller.putToStore(order);
	Map<String, FileMarshaller.Value> valueStore = fileMarshaller.valueStore;
	FileMarshaller.Value value = valueStore.get(store.byClass(Order.class).get().dbHeader());
	Set<String> records = value.records;
	for (String record : records) {
	  Assert.assertEquals("\"2\";\"comment\";\"10\";\"2019-01-01T01:01\";\"1\";\"\"",record);
	}
  }

  @Test
  public void marshalTest() throws IOException {
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileMarshaller fileMarshaller = new FileMarshaller(store);

	Order order = new Order();
	order.setId(1);
	order.setAmount(10);
	order.setComment("comment");
	Customer customer = new Customer();
	customer.setId(1);
	customer.setName("name");
	customer.setLogin("login");
	City city = new City();city.setId(1);city.setCode(100);
	customer.setCity(city);
	customer.setMail("mail");
	order.setCustomer(customer);
	order.setOrderTime(LocalDateTime.of(2019,1,1,1,1));


	List<Order> orders = Arrays.asList(order);
	Path pathDir = Paths.get("src/test/resources/test_dir");
	Path pathFile = Paths.get("src/test/resources/test_common");
	fileMarshaller.marshal(pathDir,orders);
	fileMarshaller.marshal(pathFile,orders);


	List<String> res = Files.readAllLines(pathDir.resolve("test.inner_order"));

	Assert.assertEquals("\"id\";\"comment\";\"amount\";\"order_time\";\"customer_id\";\"payment_id\"",res.get(0));
	Assert.assertEquals("\"1\";\"comment\";\"10\";\"2019-01-01T01:01\";\"1\";\"\"",res.get(1));


  }  @Test
  public void marshalOneToOneTest() throws IOException {
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileMarshaller fileMarshaller = new FileMarshaller(store);
	Payment payment = new Payment();
	payment.setCode("code");
	payment.setDsc("dsc");
	payment.setId(1);
	PaymentInfo paymentInfo = new PaymentInfo();
	paymentInfo.setCode("code");
	paymentInfo.setId(2);
	paymentInfo.setPayment(payment);
	payment.setPaymentInfo(paymentInfo);

	fileMarshaller.marshal(Paths.get("src/test/resources/test_one_to_one"), singletonList(paymentInfo));

  }
}