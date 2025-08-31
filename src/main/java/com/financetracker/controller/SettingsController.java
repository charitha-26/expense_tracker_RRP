package com.financetracker.controller;

import com.financetracker.model.User;
import com.financetracker.model.UserSettings;
import com.financetracker.service.UserService;
import com.financetracker.service.UserSettingsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    
    @Autowired
    private UserSettingsService userSettingsService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @ResponseBody
    public Map<String, Object> getSettings(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Not logged in");
                return response;
            }
            
            User user = userService.getUserById(userId);
            UserSettings settings = userSettingsService.getUserSettings(user);
            
            response.put("success", true);
            response.put("settings", settings);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateSettings(@RequestParam String theme,
                                             @RequestParam String currency,
                                             @RequestParam String language,
                                             @RequestParam boolean emailNotifications,
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
            UserSettings settings = userSettingsService.updateUserSettings(user, theme, currency, language, emailNotifications);
            
            response.put("success", true);
            response.put("settings", settings);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}
