package ru.besok.db.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import ru.besok.db.mock.data.common.City;
import ru.besok.db.mock.data.common.CityRepo;
import ru.besok.db.mock.data.special.InnerReport;
import ru.besok.db.mock.data.special.InnerReportRepo;
import ru.besok.db.mock.data.special.Report;
import ru.besok.db.mock.data.special.ReportRepo;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonDataTest {

  @Autowired
  private InnerReportRepo innerReportRepo;
  @Autowired
  private ReportRepo reportRepo;


  @Test
  @Rollback(false)
  public void common() {


  }
}
