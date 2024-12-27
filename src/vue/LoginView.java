package vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginView(BibliothequeApp app) {
        setTitle("Connexion");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Créer les composants
        JLabel usernameLabel = new JLabel("Nom d'utilisateur :");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Se connecter");
        JButton guestButton = new JButton("Accès Visiteur");

        // Ajouter des écouteurs d'événements
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique de connexion ici
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Vérifiez les informations d'identification
                if (authenticate(username, password)) {
                    app.setVisible(true); // Afficher l'application principale
                    dispose(); // Fermer la fenêtre de connexion
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, "Nom d'utilisateur ou mot de passe incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        guestButton.addActionListener(e -> {
            // Ouvrir la vue des livres pour les visiteurs
            new VisitorView(app).setVisible(true);
            dispose(); // Fermer la fenêtre de connexion
        });

        // Ajouter les composants à la fenêtre
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(loginButton, gbc);
        gbc.gridx = 1;
        add(guestButton, gbc);
    }

    private boolean authenticate(String username, String password) {
        // Remplacez cette logique par votre propre logique d'authentification
        // Par exemple, vérifiez les informations d'identification dans un fichier ou une base de données
        return "admin".equals(username) && "password".equals(password); // Exemple simple
    }
}