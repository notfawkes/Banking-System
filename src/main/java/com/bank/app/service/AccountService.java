package com.bank.app.service;

import com.bank.app.dto.AccountDto;
import com.bank.app.entity.Account;
import com.bank.app.entity.User;
import com.bank.app.repository.AccountRepository;
import com.bank.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AccountDto createAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);

        Account saved = accountRepository.save(account);
        return mapToDto(saved);
    }

    public List<AccountDto> getUserAccounts(UUID userId) {
        return accountRepository.findByUser_Id(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountById(UUID accountId, UUID userId) {
        Account account = accountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found or access denied"));
        return mapToDto(account);
    }

    @Transactional
    public AccountDto deposit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        return mapToDto(accountRepository.save(account));
    }

    @Transactional
    public AccountDto withdraw(UUID accountId, UUID userId, BigDecimal amount) {
        Account account = accountRepository.findByIdAndUser_Id(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found or access denied"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return mapToDto(accountRepository.save(account));
    }

    private AccountDto mapToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setUserId(account.getUser().getId());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }
}
