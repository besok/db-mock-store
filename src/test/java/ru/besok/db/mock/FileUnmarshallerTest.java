package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;
import ru.besok.db.mock.data.common.Customer;
import ru.besok.db.mock.data.common.Order;
import ru.besok.db.mock.data.common.Payment;
import ru.besok.db.mock.data.common.PaymentInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static ru.besok.db.mock.JpaAnnotationScanner.scan;
import static ru.besok.db.mock.JpaEntityStore.buildRelations;


/**
 * Created by Boris Zhguchev on 27/02/2019
 */
public class FileUnmarshallerTest {

  @Test
  public void initStoreDirTest() {
	Path p = Paths.get("src/test/resources/test_dir");
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileUnmarshaller fileUnmarshaller = new FileUnmarshaller(store);
	fileUnmarshaller.unmarshal(p);
	InnerStore innerStore = fileUnmarshaller.getInnerStore();
	Map<String, Record> recordMap = innerStore.getRecordMap();
	Assert.assertEquals(recordMap.size(),3);
	Assert.assertTrue(recordMap.containsKey("test.city"));
	Assert.assertTrue(recordMap.containsKey("test.customer"));
	Assert.assertTrue(recordMap.containsKey("test.inner_order"));

	Record orderRecord = recordMap.get("test.inner_order");
	String[] columns = orderRecord.columns;
	String[] records = orderRecord.records.iterator().next();
	JpaEntity orderEntity = store.byClass(Order.class).get();
	Assert.assertEquals(orderRecord.entity,orderEntity);
	Assert.assertArrayEquals(new String[]{"id","comment","amount","order_time","customer_id","payment_id"},columns);
	Assert.assertArrayEquals(new String[]{"1","comment","10","2019-01-01T01:01","1",""},records);
  }
  @Test
  public void initStoreFileTest() {
	Path p = Paths.get("src/test/resources/test_common");
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileUnmarshaller fileUnmarshaller = new FileUnmarshaller(store);
	fileUnmarshaller.unmarshal(p);
	InnerStore innerStore = fileUnmarshaller.getInnerStore();
	Map<String, Record> recordMap = innerStore.getRecordMap();
	Assert.assertEquals(recordMap.size(),3);
	Assert.assertTrue(recordMap.containsKey("test.city"));
	Assert.assertTrue(recordMap.containsKey("test.customer"));
	Assert.assertTrue(recordMap.containsKey("test.inner_order"));

	Record orderRecord = recordMap.get("test.inner_order");
	String[] columns = orderRecord.columns;
	String[] records = orderRecord.records.iterator().next();
	JpaEntity orderEntity = store.byClass(Order.class).get();
	Assert.assertEquals(orderRecord.entity,orderEntity);
	Assert.assertArrayEquals(new String[]{"id","comment","amount","order_time","customer_id","payment_id"},columns);
	Assert.assertArrayEquals(new String[]{"1","comment","10","2019-01-01T01:01","1",""},records);
  }
  @Test(expected = UnmarshallerException.class)
  public void initStoreFailureTest() {
	Path p = Paths.get("src/test/resources/test_failure");
	FileUnmarshaller fileUnmarshaller = new FileUnmarshaller(new JpaEntityStore());
	fileUnmarshaller.unmarshal(p);
  }

  @Test
  public void manyToOneDependenciesTest() {
	Path p = Paths.get("src/test/resources/test_dir");
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileUnmarshaller fileUnmarshaller = new FileUnmarshaller(store);
	fileUnmarshaller.unmarshal(p);
	InnerStore innerStore = fileUnmarshaller.getInnerStore();
	Map<String, Record> recordMap = innerStore.getRecordMap();
	Record orderRecord = recordMap.get("test.inner_order");
	Order order = (Order) orderRecord.pairs.iterator().next().object;
	Customer customer = order.getCustomer();
	Assert.assertNotNull(customer);
	Order orderFromCustomer = customer.getOrders().iterator().next();
	Assert.assertEquals(orderFromCustomer,order);
  }

  @Test
  public void oneToOneTest() {
	Path p = Paths.get("src/test/resources/test_one_to_one");
	JpaEntityStore store = buildRelations(scan("ru.besok.db.mock.data.common"));
	FileUnmarshaller fileUnmarshaller = new FileUnmarshaller(store);
	fileUnmarshaller.unmarshal(p);
	InnerStore innerStore = fileUnmarshaller.getInnerStore();
	Map<String, Record> recordMap = innerStore.getRecordMap();
	Record payment_info = recordMap.get("payment_info");
	Record test_payment_cust = recordMap.get("test.payment_cust");
	PaymentInfo paymentInfo = (PaymentInfo) payment_info.pairs.iterator().next().object;
	Payment payment = (Payment) test_payment_cust.pairs.iterator().next().object;
	Assert.assertEquals(payment,paymentInfo.getPayment());
	Assert.assertEquals(payment.getPaymentInfo(),paymentInfo);
  }
}