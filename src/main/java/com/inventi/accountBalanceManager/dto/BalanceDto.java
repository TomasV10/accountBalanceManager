package com.inventi.accountBalanceManager.dto;

import java.math.BigDecimal;

public class BalanceDto {
    private BigDecimal balance;

    public BalanceDto() {
    }

    public BalanceDto(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BalanceDto{" +
                "balance=" + balance +
                '}';
    }
}
