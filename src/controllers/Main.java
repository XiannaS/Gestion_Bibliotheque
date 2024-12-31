package controllers;

import controllers.LoginController;
import model.UserDAO;
import vue.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Créer la fenêtre de connexion
        JFrame loginFrame = new JFrame("Connexion");
        loginFrame.setSize(400, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Créer la vue de connexion
        LoginView loginView = new LoginView();

        // Créer le DAO pour les utilisateurs (remplacer avec le bon chemin de fichier)
        String usersFilePath = "chemin/vers/votre/fichier/users.csv";
        UserDAO userDAO = new UserDAO(usersFilePath);

        // Créer le contrôleur pour gérer la connexion
        LoginController loginController = new LoginController(loginView, userDAO);

        // Ajouter la vue de connexion au JFrame et afficher la fenêtre
        loginFrame.add(loginView);
        loginFrame.setVisible(true);
    }
}
