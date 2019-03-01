package ru.besok.db.mock.data.special;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 28/02/2019
 */
@Data
@Entity
@Table(schema = "test", name = "report")
public class Report {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String code;


}
