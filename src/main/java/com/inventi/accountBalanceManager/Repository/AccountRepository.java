package com.inventi.accountBalanceManager.Repository;

import com.inventi.accountBalanceManager.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * from accounts a where operation_date BETWEEN :startDate AND :endDate",nativeQuery = true)
    List<Account>findByDate(@Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);
}
