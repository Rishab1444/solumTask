package com.example.demo.Response;

import lombok.Data;

@Data
public class Response1 {
    private String message;
    private String status;

    public Response1(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
