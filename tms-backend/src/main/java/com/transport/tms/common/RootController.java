package com.transport.tms.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "system", "TransFlow Transport Management System (TMS) - Backend API",
                "version", "1.0.0",
                "documentation", "http://localhost:8080/swagger-ui/index.html",
                "apiDocs", "http://localhost:8080/v3/api-docs",
                "frontendApp", "http://localhost:3000",
                "message", "Spring Boot REST API is running successfully. Access Frontend at http://localhost:3000 or Swagger API docs at http://localhost:8080/swagger-ui/index.html"
        ));
    }
}
