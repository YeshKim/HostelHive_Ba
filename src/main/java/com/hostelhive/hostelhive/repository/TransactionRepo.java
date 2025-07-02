package com.hostelhive.hostelhive.repository;

import com.hostelhive.hostelhive.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    // No additional custom queries needed for now
}