package ru.besok.db.mock.data.special;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 28/02/2019
 */
@Data
@Entity
@Table(schema = "test",name = "inner_report")
public class InnerReport {

	@Id
	private int id;
	private String code;

	@OneToOne(mappedBy = "")
	private Report report;



}
