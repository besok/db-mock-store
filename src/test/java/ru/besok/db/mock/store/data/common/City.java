package ru.besok.db.mock.store.data.common;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

@Data
@Table(schema = "test",name = "city")
@Entity
public class City {
  @Id
  private int id;
  private int code;
  @Column(name = "title")
  private String name;
}
