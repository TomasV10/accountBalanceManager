package com.inventi.accountBalanceManager;

import com.inventi.accountBalanceManager.repository.AccountRepository;
import com.inventi.accountBalanceManager.services.CSVService;
import org.junit.jupiter.api.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class CSVServiceTest {

    @InjectMocks
    CSVService csvService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void shouldReturnTrueWhenFileFormatCSV() {
        String TYPE = "text/csv";
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getContentType())
                .thenReturn(TYPE);
        boolean csvFormat = csvService.hasCSVFormat(multipartFile);
        assertTrue(csvFormat);
    }

    @Test
    public void shouldReturnFalseWhenFileFormatCSV() {
        String OTHER_TYPE = "other/type";
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getContentType())
                .thenReturn(OTHER_TYPE);
        boolean csvFormat = csvService.hasCSVFormat(multipartFile);
        assertFalse(csvFormat);
    }
}
