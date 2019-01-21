package ru.besok.db.mock.store.data.common;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

@Data
@Table(schema = "test", name = "manager")
@Entity
public class Manager {
  @Id
  @GeneratedValue
  private int id;
  private String name;
  private String mail;

  @ManyToOne
  @JoinColumn(name = "inner_manager_id")
  private Manager innerManager;

  @OneToMany(mappedBy = "innerManager")
  private List<Manager> underManagers;

  @OneToMany(mappedBy = "manager")
  private List<Customer> customers;
}
