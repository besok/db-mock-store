package ru.besok.db.mock.data.common;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

public interface CustomerRepo extends JpaRepository<Customer,Long> { }
