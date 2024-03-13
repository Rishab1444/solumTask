package com.example.demo.Controller;


import com.example.demo.Response.Response1;
import com.example.demo.Service.CsvService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Request.RequestPath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/csv")
public class CsvController {

    private CsvService csvService;
    @Autowired
   public CsvController (CsvService csvService){
        this.csvService =csvService;
    }
    @PostMapping("/process")
    public ResponseEntity<Response1> processCsvFile(@RequestBody RequestPath filepath){
        System.out.println(new Date());
        String sourceFilePath = filepath.getFilePath();
        Path path = Paths.get(sourceFilePath.trim());
        CompletableFuture<Response1> responseFuture = csvService.copyAndProcessCsvFile(path)
                .thenCompose(destinationPath -> {
                    // The actual processing of the CSV file
                    return CompletableFuture.runAsync(() -> csvService.processCsvFile(destinationPath))
                            .thenApply(aVoid -> {
                                // Constructing success response

                                Response1 response1 = new Response1("CsvFile Processed", "Success");
                                return response1;
                            });
                })
                .exceptionally(ex -> {
                    // Handling exceptions and constructing error response
                    System.out.println("Error during CSV processing: " + ex.getMessage());
                    return new Response1("Error during CSV processing: " + ex.getMessage(), "Failed");
                });

        try {
            // Block and get the result to ensure processing is complete before returning the response.
            // Note: Blocking should be minimized or avoided if possible. Consider using async responses.
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Custom-header","value");
            Response1 response1 = responseFuture.get(); // This blocks the thread, wait for completion
            System.out.println(new Date());
            return ResponseEntity.ok(response1);
        } catch (InterruptedException  | ExecutionException e) {
            e.printStackTrace();
            Response1 response1 = new Response1("Internal Server Error","Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
        }
    }
}
