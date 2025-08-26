package com.csms.controller;

import com.csms.model.Transaction;
import com.csms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findActiveTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Transaction>> getActiveTransactions() {
        List<Transaction> activeTransactions = transactionService.findActiveTransactions();
        return ResponseEntity.ok(activeTransactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Integer transactionId) {
        return transactionService.findByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}