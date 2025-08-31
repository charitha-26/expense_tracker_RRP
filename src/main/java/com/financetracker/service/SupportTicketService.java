package com.financetracker.service;

import com.financetracker.model.SupportTicket;
import com.financetracker.model.User;
import com.financetracker.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SupportTicketService {
    
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    
    public SupportTicket createTicket(String title, String description, String priority, User user) {
        SupportTicket ticket = new SupportTicket(title, description, priority, new Date(), user);
        return supportTicketRepository.save(ticket);
    }
    
    public List<SupportTicket> getUserTickets(User user) {
        return supportTicketRepository.findByUser(user);
    }
}
