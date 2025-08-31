package com.financetracker.controller;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.model.UserSettings;
import com.financetracker.service.TransactionService;
import com.financetracker.service.UserService;
import com.financetracker.service.UserSettingsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserSettingsService userSettingsService;
    
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        User user = userService.getUserById(userId);
        UserSettings settings = userSettingsService.getUserSettings(user);
        List<Transaction> transactions = transactionService.getAllTransactions(user);
        
        double totalIncome = transactionService.getTotalIncome(user);
        double totalExpense = transactionService.getTotalExpense(user);
        double savings = transactionService.getSavings(user);
        
        model.addAttribute("user", user);
        model.addAttribute("settings", settings);
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("savings", savings);
        
        return "index";
    }
}
