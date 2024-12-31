package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;
import controllers.DashboardController;
import model.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

public class BibliothequeApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    private JButton toggleThemeButton;
    private JButton profileButton;
    private JButton notificationButton;
    private JLabel welcomeLabel;
    private boolean isDarkMode = true;

    public BibliothequeApp() {
        setTitle("Gestion de Bibliothèque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation des vues
        LivreView livreView = new LivreView();
        EmpruntView empruntView = new EmpruntView();
        UserView userView = new UserView();
        DashboardView dashboardView = new DashboardView();

        // Chargement des fichiers CSV
        String empruntFilePath = getClass().getClassLoader().getResource("data/emprunts.csv").toURI().getPath();
        String booksFilePath = getClass().getClassLoader().getResource("data/books.csv").toURI().getPath();
        String usersFilePath = getClass().getClassLoader().getResource("data/users.csv").toURI().getPath();

        // Initialisation des contrôleurs
        EmpruntController empruntController = null;
        LivreController livreController = null;
        UserController userController = null;
        UserDAO userDAO = new UserDAO(usersFilePath);

        try {
            empruntController = new EmpruntController(empruntView, empruntFilePath, booksFilePath, usersFilePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des emprunts : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        try {
            livreController = new LivreController(livreView, booksFilePath, empruntController);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        try {
            userController = new UserController(userView, userDAO);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Création du panneau d'onglets
        tabbedPane = new JTabbedPane();

        if (livreController != null) tabbedPane.addTab("Livres", livreView);
        if (userController != null) tabbedPane.addTab("Utilisateurs", userView);
        if (empruntController != null) tabbedPane.addTab("Emprunts", empruntView);

        // Ajouter le dashboard
        if (dashboardView != null) {
            DashboardController dashboardController = new DashboardController(dashboardView, empruntController, livreController, userController);
            tabbedPane.addTab("Dashboard", dashboardView);
        }

        // Création du header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        notificationButton = new JButton(loadIcon("notification.png"));
        profileButton = new JButton(loadIcon("profile.png"));
        toggleThemeButton = new JButton(loadIcon("mode.png"));

        toggleThemeButton.addActionListener(this::toggleTheme);

        welcomeLabel = new JLabel("Bienvenue, Nom Utilisateur !");
        headerPanel.add(notificationButton);
        headerPanel.add(profileButton);
        headerPanel.add(toggleThemeButton);
        headerPanel.add(welcomeLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Méthode pour charger les icônes en toute sécurité
    private ImageIcon loadIcon(String resourceName) {
        try {
            return new ImageIcon(
                new ImageIcon(getClass().getClassLoader().getResource(resourceName))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)
            );
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Icone introuvable : " + resourceName, "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Bascule entre le mode sombre et le mode clair
     *
     * @param event L'événement de clic
     */
    private void toggleTheme(ActionEvent event) {
        if (isDarkMode) {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        } else {
            try {
                UIManager.setLookAndFeel(new FlatDraculaIJTheme());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
        // Mettre à jour l'interface graphique
        SwingUtilities.updateComponentTreeUI(this);
        isDarkMode = !isDarkMode;
    }
}
