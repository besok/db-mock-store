package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

@Data
@Table(schema = "test",name = "city")
@Entity
public class City implements Serializable {

  private static final long serialVersionUID = 1L;


  @Id
  private int id;
  private int code;
  @Column(name = "title")
  private String name;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private Type enumType;

  public OrdinalType getOrdinalType() {
    return ordinalType;
  }

  public void setOrdinalType(OrdinalType ordinalType) {
    this.ordinalType = ordinalType;
  }

  @Column(name = "type_ordinal")
  @Enumerated(EnumType.ORDINAL)
  private OrdinalType ordinalType;

  public Type getEnumType() {
    return enumType;
  }

  public void setEnumType(Type enumType) {
    this.enumType = enumType;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
