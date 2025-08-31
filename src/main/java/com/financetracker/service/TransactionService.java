package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
//import java.util.stream.Collectors;
import java.util.Map;
//import java.util.List;
//import java.util.HashMap;


@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public Transaction addTransaction(String description, double amount, String type, 
                                     String category, User user) {
        Transaction transaction = new Transaction(description, amount, type, category, new Date(), user);
        return transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this transaction");
        }
        
        transactionRepository.delete(transaction);
    }
    
    public List<Transaction> getAllTransactions(User user) {
        return transactionRepository.findByUser(user);
    }
    
    public List<Transaction> getTransactionsByType(User user, String type) {
        return transactionRepository.findByUserAndType(user, type);
    }
    
    public List<Transaction> getTransactionsForLastDays(User user, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startDate = calendar.getTime();
        
        return transactionRepository.findByUserAndDateBetween(user, startDate, new Date());
    }
    
    public double getTotalIncome(User user) {
        Double total = transactionRepository.getTotalIncome(user);
        return total != null ? total : 0;
    }
    
    public double getTotalExpense(User user) {
        Double total = transactionRepository.getTotalExpense(user);
        return total != null ? total : 0;
    }
    
    public double getSavings(User user) {
        return getTotalIncome(user) - getTotalExpense(user);
    }
    public Map<String, Double> getMonthlySummary(String username) {
        List<Transaction> transactions = transactionRepository.findByUserUsername(username);
    
        return transactions.stream()
            .collect(Collectors.groupingBy(
                t -> String.format("%1$tY-%1$tm", t.getDate()),  // YYYY-MM format
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }
    
    

}
