package com.bank.app.repository;

import com.bank.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByFromAccount_User_IdOrToAccount_User_IdOrderByTimestampDesc(UUID fromUserId, UUID toUserId);
}
