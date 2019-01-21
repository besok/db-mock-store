package ru.besok.db.mock.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.besok.db.mock.store.data.common.City;
import ru.besok.db.mock.store.data.common.CityRepo;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonDataTest {

  @Autowired
  private CityRepo cityRepo;


  @Test
  public void cityRepo() {
	City city = new City();
	city.setId(0);
	city.setCode(10);
	city.setName("Moscow");
	City save = cityRepo.save(city);
  }
}
