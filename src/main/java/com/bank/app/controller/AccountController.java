package com.bank.app.controller;

import com.bank.app.dto.AccountDto;
import com.bank.app.security.CustomUserDetails;
import com.bank.app.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountDto account = accountService.createAccount(userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AccountDto> accounts = accountService.getUserAccounts(userDetails.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        AccountDto account = accountService.getAccountById(id, userDetails.getId());
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable UUID id, @jakarta.validation.Valid @RequestBody com.bank.app.dto.AmountRequest request) {
        AccountDto account = accountService.deposit(id, request.getAmount());
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountDto> withdraw(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails userDetails, @jakarta.validation.Valid @RequestBody com.bank.app.dto.AmountRequest request) {
        AccountDto account = accountService.withdraw(id, userDetails.getId(), request.getAmount());
        return ResponseEntity.ok(account);
    }
}
