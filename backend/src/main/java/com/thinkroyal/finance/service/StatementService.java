package com.thinkroyal.finance.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvValidationException;
import com.thinkroyal.finance.dto.TransactionDto;
import com.thinkroyal.finance.utility.StatementParser;

@Service
public class StatementService {
    
    public List<TransactionDto> parseStatement(MultipartFile file) throws CsvValidationException, IllegalStateException, IOException, InterruptedException {
    String contentType = file.getContentType();
    if (contentType == null) {
        throw new IllegalArgumentException("File type not recognized");
    }

    if (contentType.equals("text/csv") || file.getOriginalFilename().endsWith(".csv")) {
        return StatementParser.parseCsv(file);
    } else if (contentType.equals("application/pdf") || file.getOriginalFilename().endsWith(".pdf")) {
        return runPythonParser(file);
    } else {
        throw new IllegalArgumentException("Unsupported file format");
    }
    }

    private List<TransactionDto> runPythonParser(MultipartFile file) throws IllegalStateException, IOException, InterruptedException {
        // ProcessBuilder pb2 = new ProcessBuilder("which", "python3");
        // Process p = pb2.start();
        // BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        // String line2;
        // while ((line2 = r.readLine()) != null) {
        //     System.out.println("Python being used by Java: " + line2);
        // }
        // p.waitFor();

        Path tempFile = Files.createTempFile("statement-", ".pdf");
        file.transferTo(tempFile.toFile());

       String scriptAbsolutePath = Path.of("scripts", "parse_pdf.py").toAbsolutePath().toString();
        String pythonPath = Path.of(".venv", "bin", "python").toAbsolutePath().toString();
        ProcessBuilder pb = new ProcessBuilder(pythonPath, scriptAbsolutePath, tempFile.toAbsolutePath().toString());
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        int exitCode = process.waitFor();
        Files.delete(tempFile);

        if (exitCode != 0) {
            throw new RuntimeException("PDF parsing failed with output: " + output.toString());
        }

        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(output.toString(), TransactionDto[].class));
    }



}
