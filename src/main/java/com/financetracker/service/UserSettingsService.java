package com.financetracker.service;

import com.financetracker.model.User;
import com.financetracker.model.UserSettings;
import com.financetracker.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsService {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    public UserSettings getUserSettings(User user) {
        return userSettingsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Settings not found for user"));
    }
    
    public UserSettings updateUserSettings(User user, String theme, String currency, 
                                          String language, boolean emailNotifications) {
        UserSettings settings = getUserSettings(user);
        
        settings.setTheme(theme);
        settings.setCurrency(currency);
        settings.setLanguage(language);
        settings.setEmailNotifications(emailNotifications);
        
        return userSettingsRepository.save(settings);
    }
}
