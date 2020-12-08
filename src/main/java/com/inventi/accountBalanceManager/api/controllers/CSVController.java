package com.inventi.accountBalanceManager.api.controllers;

import com.inventi.accountBalanceManager.entity.Account;
import com.inventi.accountBalanceManager.message.ResponseMessage;
import com.inventi.accountBalanceManager.services.CSVService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CSVController {

    private final CSVService csvService;

    public CSVController(CSVService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/import")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (csvService.hasCSVFormat(file)) {
            try {
                csvService.saveCSVDataToDB(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(createResponseMessange(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(createResponseMessange(message));
            }
        }
        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponseMessange(message));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            List<Account> accounts = csvService.getAllAccounts();
            if (accounts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export")
    public void getFile(@RequestParam(name = "dateFrom", required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                        @RequestParam(name = "dateTo", required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        csvService.loadDataFromDBToCSVFile(dateFrom, dateTo);
    }

    @GetMapping("/balance")
    public BigDecimal calculateAccountBalance(@RequestParam(name = "accNumber") String accNumber,
                                              @RequestParam(name = "dateFrom", required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                              @RequestParam(name = "dateTo", required = false)
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return csvService.calculateAccountBalance(accNumber, dateFrom, dateTo);
    }

    private ResponseMessage createResponseMessange(String message) {
        return new ResponseMessage(message);
    }
}
