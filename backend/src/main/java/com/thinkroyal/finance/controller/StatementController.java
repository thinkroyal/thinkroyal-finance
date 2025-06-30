package com.thinkroyal.finance.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.exceptions.CsvValidationException;
import com.thinkroyal.finance.dto.TransactionDto;
import com.thinkroyal.finance.service.StatementService;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<TransactionDto>> uploadStatement(
            @RequestParam("file") MultipartFile file)
            throws CsvValidationException, IllegalStateException, IOException, InterruptedException {
        return ResponseEntity.ok(statementService.parseStatement(file));
    }
}
