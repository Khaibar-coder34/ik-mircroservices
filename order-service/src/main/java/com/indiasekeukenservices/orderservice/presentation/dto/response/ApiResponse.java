package com.indiasekeukenservices.orderservice.presentation.dto.response;

public class ApiResponse {
    private String message;

    // Constructor
    public ApiResponse(String message) {
        this.message = message;
    }

    // Getters en setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
