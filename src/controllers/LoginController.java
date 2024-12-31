package controllers;

import model.User;
import model.UserDAO;
import vue.LoginView;
import vue.BibliothequeApp;

import javax.swing.*;

public class LoginController {
    private LoginView loginView;
    private UserDAO userDAO;

    public LoginController(LoginView loginView, UserDAO userDAO) {
        this.loginView = loginView;
        this.userDAO = userDAO;

        loginView.setLoginButtonListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(loginView, "Connexion réussie!");
            // Crée et affiche la fenêtre principale de l'application
            new BibliothequeApp(); // Ouvre la fenêtre principale
            // Optionnel : fermez la fenêtre de connexion
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(loginView);
            topFrame.dispose();
        } else {
            JOptionPane.showMessageDialog(loginView, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
