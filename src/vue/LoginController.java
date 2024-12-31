package vue;

import vue.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView loginView;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.loginView.addLoginListener(new LoginButtonListener());
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            // Logique de connexion (exemple simple)
            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(loginView, "Connexion réussie !");
                // Ici, vous pouvez rediriger vers la vue principale ou effectuer d'autres actions
                // Par exemple : new MainLibraryView().setVisible(true);
                loginView.dispose(); // Ferme la fenêtre de connexion
            } else {
                JOptionPane.showMessageDialog(loginView, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean authenticate(String username, String password) {
            // Remplacez cette logique par votre propre méthode d'authentification
            // Par exemple, vérifiez les informations d'identification dans une base de données
            return "admin".equals(username) && "password".equals(password); // Exemple simple
        }
    }

   
}