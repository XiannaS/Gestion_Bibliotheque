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
        if (userExists(user.getId())) {
            throw new IllegalArgumentException("Un utilisateur avec cet ID existe déjà.");
        }
        userDAO.addUser (user);
    }

 

    public void updateUser (User user) {
        userDAO.updateUser (user);
    }

    public void deleteUser (String id) {
        userDAO.deleteUser (id);
    }

    public boolean userExists(String email) {
        return getAllUsers().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}