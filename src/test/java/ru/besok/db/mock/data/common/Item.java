package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

@Data
@Table(schema = "test", name = "item")
@Entity
public class Item {
  @Id
  @GeneratedValue
  private int id;

  private String name;
  private String dsc;
  private long code;
  private long amount;

  @ManyToMany(mappedBy = "items")
  private List<Order> orders;

  @ManyToOne
  @JoinColumn(name = "item_type_id")
  private ItemType itemType;


}
