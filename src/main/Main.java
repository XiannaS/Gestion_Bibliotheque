package main;

import javax.swing.SwingUtilities;

import vue.BibliothequeApp;
import vue.LoginController;
import vue.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
            new LoginController(loginView);
        });
    }
}