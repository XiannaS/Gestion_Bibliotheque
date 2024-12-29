package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel logoLabel;

    public LoginView() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panneau principal avec un arrière-plan sombre
        JPanel background = new JPanel();
        background.setBackground(Color.DARK_GRAY); // Couleur de fond sombre
        setContentPane(background);
        background.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Message de bienvenue et logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Bienvenue à la Bibliothèque", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Redimensionner le logo
        ImageIcon logoIcon = new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/logo.png");
        Image scaledLogoImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogoImage), JLabel.CENTER);
        headerPanel.add(logoLabel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        background.add(headerPanel, gbc);

        // Panneau de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/email.png"));
        usernameLabel.setText("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField(15);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JLabel passwordLabel = new JLabel(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/password.png"));
        passwordLabel.setText("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(15);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 10);
        background.add(formPanel, gbc);

        // Bouton de connexion avec animation
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginButton.setFocusPainted(false);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(60, 179, 113));
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(60, 179, 113)));

        // Animation pour l'effet de survol
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(40, 120, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(60, 179, 113));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 10);
        background.add(loginButton, gbc);
    }

    // Getters pour accéder aux champs et au bouton
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public static void main(String[] args) {
 SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}