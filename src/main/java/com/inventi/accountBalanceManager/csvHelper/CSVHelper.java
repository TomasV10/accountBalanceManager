package com.inventi.accountBalanceManager.csvHelper;

import com.inventi.accountBalanceManager.Entities.Account;
import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CSVHelper {
    public static String type = "text/csv";
    static String[] headers = {
            "accountNumber", "operationDate", "beneficiary", "comment", "amount", "currency"
    };

    public static boolean hasCSVFormat(MultipartFile file){
        return type.equals(file.getContentType());
    }

    public static List<Account>csvToAccounts(InputStream inputStream){
        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
            List<CSVRecord> csvRecords = csvParser.getRecords();
            return getAllAccountsFromCSV(csvRecords);

        }catch (IOException e){
            throw new RuntimeException("fail to parse CSV file " + e.getMessage());
        }
    }

    public static void accountsToCSV(List<Account>accounts){
        final String SAMPLE_CSV_FILE = "./bankStatements.csv";

        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT .withHeader(headers))) {
            printingGivenValues(accounts, csvPrinter);
            csvPrinter.flush();
        }catch (IOException e){
            throw new RuntimeException("fail to import data to CSV file " + e.getMessage());
        }
    }

    private static void printingGivenValues(List<Account> accounts, CSVPrinter csvPrinter) throws IOException {
        for(Account account : accounts){
            List<String>dataDB = Arrays.asList(
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

    private static List<Account> getAllAccountsFromCSV(List<CSVRecord> csvRecords) {
        return csvRecords.stream()
                .map(csvRecord -> new Account(csvRecord.get("accountNumber"),
                        LocalDate.parse(csvRecord.get("operationDate")),
                        csvRecord.get("beneficiary"), csvRecord.get("comment"),
                        new BigDecimal(csvRecord.get("amount")), csvRecord.get("currency")))
                .collect(Collectors.toList());
    }
}
