/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.UserDAO;
import com.mycompany.storeapp.model.dao.UserProfileDAO;
import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.model.entity.UserProfile;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class UserController {
    private final UserDAO userDAO;
    private final UserProfileDAO userProfileDAO;
    
    public UserController() {
        var dbConnection = new DatabaseConnection();
        this.userDAO = new UserDAO(dbConnection);
        this.userProfileDAO = new UserProfileDAO(dbConnection);
    }
    
    public boolean registerWithProfile(String name, String email, String password, 
                                     String fullName, LocalDate dateOfBirth, 
                                     String gender, String phone) {
        if (!isValidRegistrationData(name, email, password)) {
            return false;
        }
        
        if (!isValidProfileData(fullName, dateOfBirth, gender, phone)) {
            return false;
        }
        
        if (isEmailExists(email)) {
            return false;
        }
        
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(password); 
        user.setRole(5); 
        user.setIs_active(true);
        
        if (!userDAO.save(user)) {
            return false;
        }
        
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setFullName(fullName.trim());
        profile.setDateOfBirth(dateOfBirth);
        profile.setGender(gender.trim());
        profile.setPhone(phone.trim());
        
        return userProfileDAO.createProfile(profile);
    }
    
    // === ADMIN USER MANAGEMENT ===
    public boolean createUserWithProfile(User user, UserProfile profile) {
        if (user == null || !isValidUserData(user)) {
            return false;
        }
        
        if (isEmailExists(user.getEmail())) {
            return false;
        }
        
        if (!userDAO.save(user)) {
            return false;
        }
       
        if (profile != null) {
            if (!isValidProfileData(profile.getFullName(), profile.getDateOfBirth(), 
                                   profile.getGender(), profile.getPhone())) {
                return false;
            }
            
            profile.setUserId(user.getId());
            userProfileDAO.createProfile(profile);
        }
        
        return true;
    }
    
    public boolean updateUserWithProfile(User user, UserProfile profile) {
        if (user == null || user.getId() <= 0 || !isValidUserData(user)) {
            return false;
        }
        User existingUser = userDAO.getUserById(user.getId());
        if (existingUser == null) {
            return false;
        }
        if (isEmailExists(user.getEmail())) {
            User userWithSameEmail = getUserByEmail(user.getEmail());
            if (userWithSameEmail != null && userWithSameEmail.getId() != user.getId()) {
                return false;
            }
        }
        if (!userDAO.updateUser(user)) {
            return false;
        }
        
        if (profile != null) {
            if (!isValidProfileData(profile.getFullName(), profile.getDateOfBirth(), 
                                   profile.getGender(), profile.getPhone())) {
                return false;
            }
            
            UserProfile existingProfile = userProfileDAO.getProfileByUserId(user.getId());
            if (existingProfile != null) {
                profile.setProfileId(existingProfile.getProfileId());
                profile.setUserId(user.getId());
                userProfileDAO.updateProfile(profile);
            } else {
                profile.setUserId(user.getId());
                userProfileDAO.createProfile(profile);
            }
        }
        
        return true;
    }
    
    public User getUserWithProfile(int userId) {
        if (userId <= 0) {
            return null;
        }
        
        User user = userDAO.getUserById(userId);
        if (user != null) {
            UserProfile profile = userProfileDAO.getProfileByUserId(userId);
            user.setProfile(profile);
        }
        return user;
    }
    
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userDAO.findByEmail(email.trim().toLowerCase());
    }
    
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    public List<User> getUsersByRole(int roleId) {
        return userDAO.getUsersByRole(roleId);
    }
    
    public List<User> getActiveUsers() {
        return userDAO.getActiveUsers();
    }
    
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (userId <= 0 || oldPassword == null || newPassword == null || 
            oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        
        if (!isValidPassword(newPassword)) {
            return false;
        }
        
        return userDAO.updatePassword(userId, newPassword);
    }
    
    public boolean resetPassword(int userId, String newPassword) {
        if (userId <= 0 || newPassword == null || !isValidPassword(newPassword)) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        return userDAO.updatePassword(userId, newPassword);
    }
    
    public boolean toggleUserStatus(int userId, boolean isActive) {
        if (userId <= 0) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        return userDAO.updateUserStatus(userId, isActive);
    }
    
    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        UserProfile profile = userProfileDAO.getProfileByUserId(userId);
        if (profile != null) {
            userProfileDAO.deleteProfile(profile.getProfileId());
        }
        
        return userDAO.deleteUser(userId);
    }
    
    // === PROFILE MANAGEMENT ===
    
    public boolean createProfile(int userId, UserProfile profile) {
        if (userId <= 0 || profile == null) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        if (userProfileDAO.profileExistsForUser(userId)) {
            return false;
        }

        if (!isValidProfileData(profile.getFullName(), profile.getDateOfBirth(), 
                               profile.getGender(), profile.getPhone())) {
            return false;
        }
        
        profile.setUserId(userId);
        return userProfileDAO.createProfile(profile);
    }
    
    public boolean updateProfile(int userId, UserProfile profile) {
        if (userId <= 0 || profile == null) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        UserProfile existingProfile = userProfileDAO.getProfileByUserId(userId);
        if (existingProfile == null) {
            return false;
        }
        
        if (!isValidProfileData(profile.getFullName(), profile.getDateOfBirth(), 
                               profile.getGender(), profile.getPhone())) {
            return false;
        }
        
        profile.setProfileId(existingProfile.getProfileId());
        profile.setUserId(userId);
        
        return userProfileDAO.updateProfile(profile);
    }
    
    public UserProfile getProfileByUserId(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userProfileDAO.getProfileByUserId(userId);
    }
    
    // === UTILITY METHODS ===
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userDAO.userExists(email.trim().toLowerCase());
    }
    
    public boolean hasProfile(int userId) {
        return userProfileDAO.profileExistsForUser(userId);
    }
    
    // === VALIDATION METHODS ===
    private boolean isValidRegistrationData(String name, String email, String password) {
        return name != null && !name.trim().isEmpty() &&
               isValidEmail(email) &&
               isValidPassword(password);
    }
    
    private boolean isValidUserData(User user) {
        return user.getName() != null && !user.getName().trim().isEmpty() &&
               isValidEmail(user.getEmail()) &&
               isValidPassword(user.getPassword());
    }
    
    private boolean isValidProfileData(String fullName, LocalDate dateOfBirth, 
                                     String gender, String phone) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        
        if (dateOfBirth != null) {
            LocalDate now = LocalDate.now();
            LocalDate minAge = now.minusYears(120);
            LocalDate maxAge = now.minusYears(13);
            
            if (dateOfBirth.isBefore(minAge) || dateOfBirth.isAfter(maxAge)) {
                return false;
            }
        }
        
        if (gender != null && !gender.trim().isEmpty()) {
            String normalizedGender = gender.trim().toLowerCase();
            if (!normalizedGender.equals("male") && 
                !normalizedGender.equals("female") && 
                !normalizedGender.equals("other")) {
                return false;
            }
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!isValidPhoneNumber(phone.trim())) {
                return false;
            }
        }
        
        return true;
    }
    
   private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailPattern = "^[^@]+@[^@]+\\.[^@]+$";
        return Pattern.matches(emailPattern, email.trim());
    }
    
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        String phonePattern = "^[0-9]{10,11}$";
        return Pattern.matches(phonePattern, phone.trim());
    }
    
    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        return password.length() >= 6;
    }
}