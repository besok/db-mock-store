package ru.besok.db.mock.store.data.common;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Boris Zhguchev on 21/01/2019
 */

public interface PaymentRepo extends JpaRepository<Payment,Integer> { }
