package ru.besok.db.mock.store.data.common;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

@Data
@Table(schema = "test", name = "inner_order")
@Entity
public class Order {

  @Id
  @GeneratedValue
  private int id;

  private String comment;
  private long amount;
  private LocalDateTime orderTime;

  @ManyToOne
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToMany
  @JoinTable(name = "items_orders",
	joinColumns = @JoinColumn(name = "item_id", referencedColumnName = "id"),
	inverseJoinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"))
  private List<Item> items;


}
