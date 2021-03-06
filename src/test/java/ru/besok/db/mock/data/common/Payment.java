package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
@Data
@Table(schema = "test",name = "payment_cust")
@Entity
public class Payment {
  @Id @GeneratedValue
  private int id;
  private String code;
  private String dsc;

  @OneToOne(mappedBy = "payment")
  private PaymentInfo paymentInfo;
}
