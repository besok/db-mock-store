package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */
@Data
@Table(schema = "test",name = "customer")
@Entity
public class Customer {
  @Id
  @GeneratedValue
  private long id;

  private String name;
  private String login;
  private String mail;
  private String phone;

  @OneToMany(mappedBy = "customer")
  private List<Order> orders;

  @ManyToOne
  @JoinColumn(name = "city_id")
  private City city;

  @ManyToOne
  @JoinColumn(name = "manager_id")
  private Manager manager;



}
