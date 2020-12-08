package com.inventi.accountBalanceManager.repository;

import com.inventi.accountBalanceManager.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * FROM accounts WHERE (:startDate IS NULL OR operation_date >= :startDate) AND" +
            " (:endDate IS NULL OR operation_date < :endDate)",
            nativeQuery = true)
    List<Account> findByDate(@Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);
}
