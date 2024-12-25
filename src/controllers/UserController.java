package controllers;

import model.User;
import model.UserDAO;

import java.util.List;

public class UserController {
    private UserDAO userDAO;

    public UserController(String filePath) {
        this.userDAO = new UserDAO(filePath);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    public void addUser (User user) {
        // Vérifiez si l'utilisateur existe déjà par ID
        if (userExists(user.getId())) {
            throw new IllegalArgumentException("Un utilisateur avec cet ID existe déjà.");
        }
        
        // Vérifiez si l'email existe déjà
        if (userExists(user.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }

        userDAO.addUser (user);
    }

    public void updateUser (User user) {
        userDAO.updateUser (user);
    }

    public void deleteUser (String id) {
        userDAO.deleteUser (id);
    }

    public boolean userExists(String id) {
        return getAllUsers().stream().anyMatch(user -> user.getId().equals(id));
    }

    public boolean userExistsByEmail(String email) {
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
    
}