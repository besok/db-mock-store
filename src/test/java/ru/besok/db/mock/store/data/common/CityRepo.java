package ru.besok.db.mock.store.data.common;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

public interface CityRepo extends JpaRepository<City,Integer> { }
