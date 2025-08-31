package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndType(User user, String type);
    
    List<Transaction> findByUserUsername(String username);

    @Query("SELECT t FROM Transaction t WHERE t.user = ?1 AND t.date BETWEEN ?2 AND ?3")
    List<Transaction> findByUserAndDateBetween(User user, Date startDate, Date endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = ?1 AND t.type = 'income'")
    Double getTotalIncome(User user);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = ?1 AND t.type = 'expense'")
    Double getTotalExpense(User user);
}
