package ru.besok.db.mock.store.data.common;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

@Data
@Table(schema = "test", name = "item_type")
@Entity
public class ItemType {
  @Id
  @GeneratedValue
  private int id;
  @Column(name = "code_item_type")
  private String code;
  private String dsc;
}
