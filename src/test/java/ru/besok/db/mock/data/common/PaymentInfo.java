package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
@Data
@Entity
@Table(schema = "test",name = "payment_info")
public class PaymentInfo {
  @Id @GeneratedValue
  private int id;
  private String code;

  @OneToOne
  @JoinColumn(name = "payment_id")
  private Payment payment;

}
