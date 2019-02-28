package ru.besok.db.mock.data.common;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Boris Zhguchev on 28/02/2019
 */
@Data
@Entity
@Table(schema = "test",name = "report")
public class Report {

	@Id
	@GeneratedValue
	private int id;
	private String code;




}
