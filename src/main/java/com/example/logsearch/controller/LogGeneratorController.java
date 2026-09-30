package com.example.logsearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api")
public class LogGeneratorController {

    private static final Logger logger = LoggerFactory.getLogger(LogGeneratorController.class);
    private final Random random = new Random();

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        long responseTime = random.nextInt(50);
        logger.info("Health check completed",
                kv("service", "HealthService"),
                kv("endpoint", "/api/health"),
                kv("httpStatus", 200),
                kv("responseTime", responseTime),
                kv("requestId", UUID.randomUUID().toString()));
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/simulate-error")
    public ResponseEntity<String> simulateError() {
        long responseTime = random.nextInt(200) + 100;
        logger.error("Database connection failed",
                kv("service", "DatabaseService"),
                kv("endpoint", "/api/simulate-error"),
                kv("httpStatus", 500),
                kv("responseTime", responseTime),
                kv("requestId", UUID.randomUUID().toString()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database Error Simulated");
    }

    @GetMapping("/simulate-warning")
    public ResponseEntity<String> simulateWarning() {
        long responseTime = random.nextInt(1000) + 500;
        logger.warn("High response time detected",
                kv("service", "PaymentService"),
                kv("endpoint", "/api/simulate-warning"),
                kv("httpStatus", 200),
                kv("responseTime", responseTime),
                kv("requestId", UUID.randomUUID().toString()));
        return ResponseEntity.ok("Warning Log Simulated (Slow Request)");
    }
    
    @GetMapping("/simulate-auth-failure")
    public ResponseEntity<String> simulateAuthFailure() {
        long responseTime = random.nextInt(50) + 10;
        logger.warn("Authentication failed - Invalid credentials",
                kv("service", "AuthService"),
                kv("endpoint", "/api/simulate-auth-failure"),
                kv("httpStatus", 401),
                kv("responseTime", responseTime),
                kv("requestId", UUID.randomUUID().toString()));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/simulate-traffic")
    public ResponseEntity<String> simulateTraffic(@RequestParam(defaultValue = "10") int count) {
        for (int i = 0; i < count; i++) {
            int chance = random.nextInt(100);
            String traceId = UUID.randomUUID().toString();
            if (chance < 70) {
                logger.info("User request processed successfully",
                        kv("service", "UserService"),
                        kv("endpoint", "/api/users"),
                        kv("httpStatus", 200),
                        kv("responseTime", random.nextInt(100)),
                        kv("requestId", traceId));
            } else if (chance < 90) {
                logger.warn("Invalid input received",
                        kv("service", "ProductService"),
                        kv("endpoint", "/api/products"),
                        kv("httpStatus", 400),
                        kv("responseTime", random.nextInt(50)),
                        kv("requestId", traceId));
            } else {
                logger.error("Internal Server Error while fetching orders",
                        kv("service", "OrderService"),
                        kv("endpoint", "/api/orders"),
                        kv("httpStatus", 500),
                        kv("responseTime", random.nextInt(300)),
                        kv("requestId", traceId));
            }
        }
        return ResponseEntity.ok("Simulated " + count + " log entries.");
    }
}
