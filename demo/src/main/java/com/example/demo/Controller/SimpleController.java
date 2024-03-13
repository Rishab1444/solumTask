package com.example.demo.Controller;

import com.example.demo.Request.RequestMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SimpleController {

    private AtomicInteger postMethodCallCounter = new AtomicInteger(0);

    @GetMapping("/greeting")
    public  String greeting() {
        return "Hello World";
    }

    @PostMapping("/echo")
    public ResponseEntity<String> echo(@RequestBody RequestMessage message) {
       int count = postMethodCallCounter.incrementAndGet();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Custom-header","value");
        String responseString = "Recived :" + message + " " + count + "times";
        return  ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(responseString);
    }
}
