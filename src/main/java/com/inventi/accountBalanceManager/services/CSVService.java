package com.inventi.accountBalanceManager.services;

import com.inventi.accountBalanceManager.Entities.Account;
import com.inventi.accountBalanceManager.Repository.AccountRepository;
import com.inventi.accountBalanceManager.csvHelper.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class CSVService {
    @Autowired
    AccountRepository accountRepository;

    public void save(MultipartFile file){
        try {
            List<Account> accounts = CSVHelper.csvToAccounts(file.getInputStream());
            accountRepository.saveAll(accounts);
        }catch (IOException e){
            throw new RuntimeException("fail to store csv data " + e.getMessage());
        }
    }

    public void load(LocalDate dateFrom, LocalDate dateTo) {
        CSVHelper.accountsToCSV(getAllAccountsForCSV(dateFrom, dateTo));
    }

    private List<Account>getAllAccountsForCSV(LocalDate dateFrom, LocalDate dateTo){
        if(dateFrom != null && dateTo != null){
            return accountRepository.findByDate(dateFrom, dateTo);

        }return getAllAccounts();
    }


    public List<Account>getAllAccounts(){
        return accountRepository.findAll();
    }
}
