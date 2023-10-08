package br.ufsc.inf.lapesd.csv2rdf.controller;

import br.ufsc.inf.lapesd.csv2rdf.CsvReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
public class ConversorController {

        @Autowired
        private CsvReader csvReader;

        @PostMapping("/converte")
        public ResponseEntity<Resource> converteCsv(@RequestParam("csv-file") MultipartFile csvFile,
                        @RequestParam("mapping-file") MultipartFile mappingFile,
                        @RequestParam("ontology-file") MultipartFile ontologyFile) throws IOException {

                if (csvFile.isEmpty()) {
                        throw new IOException("Arquivo Inexistente");
                }

                File tempFileCsv = File.createTempFile("temp", ".csv");
                csvFile.transferTo(tempFileCsv);

                File tempFileMapping = File.createTempFile("temp", ".jsonld");
                mappingFile.transferTo(tempFileMapping);

                Path tempFileOntology = Files.createTempFile("temp", ".owl");
                Files.copy(ontologyFile.getInputStream(), tempFileOntology, StandardCopyOption.REPLACE_EXISTING);

                InputStreamResource input = csvReader.process(tempFileCsv, tempFileMapping, tempFileOntology);

                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(input);
        }
}
