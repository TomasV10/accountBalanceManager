package com.inventi.accountBalanceManager.services;

import com.inventi.accountBalanceManager.dto.BalanceDto;
import com.inventi.accountBalanceManager.entity.Account;
import com.inventi.accountBalanceManager.repository.AccountRepository;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class CSVService {
    private static final String[] HEADERS = {
            "accountNumber", "operationDate", "beneficiary", "comment", "amount", "currency"
    };
    private static final CSVFormat format = CSVFormat.DEFAULT
            .withHeader(HEADERS)
            .withQuoteMode(QuoteMode.MINIMAL);
    private static final String type = "text/csv";

    private final AccountRepository accountRepository;

    public CSVService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean hasCSVFormat(MultipartFile file) {

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

    public ByteArrayInputStream loadDataFromDBToCSVFile(LocalDate dateFrom, LocalDate dateTo) {
        return accountsToCSV(getAllAccountsInPeriod(dateFrom, dateTo));
    }

    public BalanceDto calculateAccountBalance(String accNumber, LocalDate dateFrom,
                                              LocalDate dateTo) {

        List<Account> allAcounts = getAllAccountsInPeriod(dateFrom, dateTo);
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(allAcounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(accNumber))
                .map(Account::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return balanceDto;
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

    private ByteArrayInputStream accountsToCSV(List<Account> accounts) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = createCSVPrinter(format, out)) {
            printingGivenValues(accounts, csvPrinter);
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file " + e.getMessage());
        }
    }

    private CSVPrinter createCSVPrinter(CSVFormat format, ByteArrayOutputStream out) throws IOException {
        return new CSVPrinter(new PrintWriter(out), format);
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
