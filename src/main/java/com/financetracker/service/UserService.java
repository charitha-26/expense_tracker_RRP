package com.financetracker.service;

import com.financetracker.model.User;
import com.financetracker.model.UserSettings;
import com.financetracker.repository.UserRepository;
import com.financetracker.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    public User registerUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, password, email);
        user = userRepository.save(user);
        
        // Create default settings for the user
        UserSettings settings = new UserSettings("dark", "INR", "en", false, user);
        userSettingsRepository.save(settings);
        
        return user;
    }
    
    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        
        throw new RuntimeException("Invalid username or password");
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
