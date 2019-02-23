package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
@Data
@Entity
public class PaymentInfo {
  @Id @GeneratedValue
  private int id;
  private String code;
}
