package com.bank.app.controller;

import com.bank.app.dto.TransactionDto;
import com.bank.app.dto.TransferRequest;
import com.bank.app.security.CustomUserDetails;
import com.bank.app.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDto> transfer(@Valid @RequestBody TransferRequest request,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        TransactionDto transaction = transactionService.transferMoney(userDetails.getId(), request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TransactionDto> history = transactionService.getTransactionHistory(userDetails.getId());
        return ResponseEntity.ok(history);
    }
}
