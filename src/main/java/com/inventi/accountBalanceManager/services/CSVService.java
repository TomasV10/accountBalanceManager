package com.inventi.accountBalanceManager.services;

import com.inventi.accountBalanceManager.entity.Account;
import com.inventi.accountBalanceManager.repository.AccountRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class CSVService {
    private static final String BANKSTATEMENTS_CSV_FILE = "./bankStatements.csv";
    private static final String[] HEADERS = {
            "accountNumber", "operationDate", "beneficiary", "comment", "amount", "currency"
    };

    private final AccountRepository accountRepository;

    public CSVService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean hasCSVFormat(MultipartFile file) {
        String type = "text/csv";
        return type.equals(file.getContentType());
    }

    public void saveCSVDataToDB(MultipartFile file) {
        try {
            List<Account> accounts = csvToAccounts(file.getInputStream());
            accountRepository.saveAll(accounts);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data " + e.getMessage());
        }
    }

    public void loadDataFromDBToCSVFile(LocalDate dateFrom, LocalDate dateTo) {
        accountsToCSV(getAllAccountsInPeriod(dateFrom, dateTo));
    }

    public BigDecimal calculateAccountBalance(String accNumber, LocalDate dateFrom,
                                              LocalDate dateTo) {

        List<Account> allAcounts = getAllAccountsInPeriod(dateFrom, dateTo);
        return allAcounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accNumber))
                .map(Account::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    private List<Account> csvToAccounts(InputStream inputStream) {
        try (BufferedReader fileReader = new BufferedReader(createInputStreamReader(inputStream));
             CSVParser csvParser = createCSVParser(fileReader)) {
            List<CSVRecord> csvRecords = csvParser.getRecords();
            return getAllAccountsFromCSV(csvRecords);
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file " + e.getMessage());
        }
    }

    private CSVParser createCSVParser(BufferedReader fileReader) throws IOException {
        return new CSVParser(fileReader,
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim());
    }

    private InputStreamReader createInputStreamReader(InputStream inputStream) {
        return new InputStreamReader(inputStream, UTF_8);
    }

    private List<Account> getAllAccountsFromCSV(List<CSVRecord> csvRecords) {
        return csvRecords.stream()
                .map(csvRecord -> createAccount(csvRecord))
                .collect(Collectors.toList());
    }

    private Account createAccount(CSVRecord csvRecord) {
        return new Account(csvRecord.get("accountNumber"),
                LocalDate.parse(csvRecord.get("operationDate")),
                csvRecord.get("beneficiary"),
                csvRecord.get("comment"),
                new BigDecimal(csvRecord.get("amount")),
                csvRecord.get("currency"));
    }

    private void accountsToCSV(List<Account> accounts) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(BANKSTATEMENTS_CSV_FILE));
             CSVPrinter csvPrinter = createCSVPrinter(writer)) {
            printingGivenValues(accounts, csvPrinter);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file " + e.getMessage());
        }
    }

    private CSVPrinter createCSVPrinter(BufferedWriter writer) throws IOException {
        return new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(HEADERS));
    }

    private void printingGivenValues(List<Account> accounts, CSVPrinter csvPrinter) throws IOException {
        for (Account account : accounts) {
            List<String> dataDB = Arrays.asList(
                    account.getAccountNumber(),
                    String.valueOf(account.getDate()),
                    account.getBeneficiary(),
                    account.getComment(),
                    String.valueOf(account.getAmount()),
                    account.getCurrency()
            );
            csvPrinter.printRecord(dataDB);
        }
    }

    private List<Account> getAllAccountsInPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return accountRepository.findByDate(dateFrom, dateTo);
    }
}
