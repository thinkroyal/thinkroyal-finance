package com.thinkroyal.finance.utility;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.thinkroyal.finance.dto.TransactionDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;


public class StatementParser {
    
    public static List<TransactionDto> parseCsv(MultipartFile file) throws CsvValidationException {
    List<TransactionDto> transactions = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
         CSVReader csvReader = new CSVReader(reader)) {

        String[] line;
        csvReader.readNext();

        while ((line = csvReader.readNext()) != null) {
            TransactionDto dto = new TransactionDto();
            dto.setDate(line[0]);
            dto.setDescription(line[1]);
            dto.setCategory(line[2]);
            dto.setAmount(line[3]);
            transactions.add(dto);
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to read CSV", e);
    }
    return transactions;
}


}
