package vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginView(BibliothequeApp app) {
        setTitle("Connexion");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Créer le panneau principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Ajouter les composants
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nom d'utilisateur :"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mot de passe :"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Bouton de connexion
        loginButton = new JButton("Se connecter");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique de connexion ici
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                // Remplacez cette logique par votre propre méthode de vérification
                if (authenticate(username, password)) {
                    app.setVisible(true); // Afficher la fenêtre principale
                    dispose(); // Fermer la fenêtre de connexion
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        // Bouton d'annulation
        cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Fermer l'application
            }
        });
        gbc.gridx = 1;
        panel.add(cancelButton, gbc);

        // Ajouter le panneau à la fenêtre
        add(panel);
    }

    // Méthode d'authentification (à remplacer par votre logique)
    private boolean authenticate(String username, String password) {
        // Remplacez cette logique par votre propre méthode de vérification
        return "admin".equals(username) && "password".equals(password); // Exemple simple
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView(new BibliothequeApp());
            loginView.setVisible(true);
        });
    }
}