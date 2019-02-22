package ru.besok.db.mock.store;

import org.junit.Assert;
import org.junit.Test;
import ru.besok.db.mock.store.data.common.City;
import ru.besok.db.mock.store.data.common.Payment;
import ru.besok.db.mock.store.data.common.PaymentInfo;
import ru.besok.db.mock.store.meta.jpa.JpaEntity;

import java.util.Set;

import static org.junit.Assert.*;
import static ru.besok.db.mock.store.JpaAnnotationScanner.*;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
public class JpaAnnotationScannerTest {

  @Test
  public void initEntityTest() {
	assertEquals(initEntity(City.class).dbHeader(),"test.city");
	assertEquals(initEntity(Payment.class).dbHeader(),"test.payment_cust");
	assertEquals(initEntity(PaymentInfo.class).dbHeader(),"payment_info");
  }
}