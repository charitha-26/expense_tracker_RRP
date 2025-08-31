package com.financetracker.controller;

import com.financetracker.model.SupportTicket;
import com.financetracker.model.User;
import com.financetracker.service.SupportTicketService;
import com.financetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/support")
public class SupportController {
    
    @Autowired
    private SupportTicketService supportTicketService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/ticket")
    @ResponseBody
    public Map<String, Object> createTicket(@RequestParam String title,
                                           @RequestParam String description,
                                           @RequestParam String priority,
                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Not logged in");
                return response;
            }
            
            User user = userService.getUserById(userId);
            SupportTicket ticket = supportTicketService.createTicket(title, description, priority, user);
            
            response.put("success", true);
            response.put("ticket", ticket);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    @GetMapping("/tickets")
    @ResponseBody
    public Map<String, Object> getUserTickets(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Not logged in");
                return response;
            }
            
            User user = userService.getUserById(userId);
            List<SupportTicket> tickets = supportTicketService.getUserTickets(user);
            
            response.put("success", true);
            response.put("tickets", tickets);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}
