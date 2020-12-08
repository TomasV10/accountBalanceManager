package com.inventi.accountBalanceManager.api.controllers;

import com.inventi.accountBalanceManager.dto.BalanceDto;
import com.inventi.accountBalanceManager.message.ResponseMessage;
import com.inventi.accountBalanceManager.services.CSVService;
import com.inventi.accountBalanceManager.validation.BankAccountNumberValidationUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;

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
                return ResponseEntity.status(OK).body(createResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(EXPECTATION_FAILED).body(createResponseMessage(message));
            }
        }
        message = "Please upload a csv file!";
        return ResponseEntity.status(BAD_REQUEST).body(createResponseMessage(message));
    }


    @GetMapping("/export")
    public ResponseEntity<Resource> getFile(@RequestParam(name = "dateFrom", required = false)
                                            @DateTimeFormat(iso = DATE) LocalDate dateFrom,
                                            @RequestParam(name = "dateTo", required = false)
                                            @DateTimeFormat(iso = DATE) LocalDate dateTo) {
        String filename = "bankstatements.csv";
        InputStreamResource file = new InputStreamResource(csvService.loadDataFromDBToCSVFile(dateFrom, dateTo));
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/balance")
    public BalanceDto calculateAccountBalance(@RequestParam(name = "accNumber") String accNumber,
                                              @RequestParam(name = "dateFrom", required = false)
                                              @DateTimeFormat(iso = DATE) LocalDate dateFrom,
                                              @RequestParam(name = "dateTo", required = false)
                                              @DateTimeFormat(iso = DATE) LocalDate dateTo)
                                                                                    throws IllegalAccessException {
        BankAccountNumberValidationUtils.validateAccNumber(accNumber);
        return csvService.calculateAccountBalance(accNumber, dateFrom, dateTo);
    }

    private ResponseMessage createResponseMessage(String message) {
        return new ResponseMessage(message);
    }


}
