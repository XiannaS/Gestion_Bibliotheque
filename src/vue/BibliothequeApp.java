package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;

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

    // Constructeur de la classe BibliothequeApp
    public BibliothequeApp() {
        setTitle("Gestion de Bibliothèque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation des contrôleurs avec gestion des exceptions
        LivreController livreController = null;
        UserController userController = null;
        EmpruntController empruntController = null;
        EmpruntView empruntView = new EmpruntView();
        // Chargement des contrôleurs et gestion des exceptions pour chaque
        try {
            livreController = new LivreController("src/resources/books.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        try {
            userController = new UserController("src/resources/users.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        try {
            empruntController = new EmpruntController(empruntView,
                "src/resources/emprunt.csv",
                "src/resources/books.csv",
                "src/resources/users.csv"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des emprunts : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Création du panneau d'onglets
        tabbedPane = new JTabbedPane();

        // Vérification de l'initialisation des contrôleurs avant d'ajouter des vues
        if (livreController != null && userController != null && empruntController != null) {
            DashboardView dashboardView = new DashboardView(livreController, userController, empruntController);
            tabbedPane.addTab("Tableau de Bord", dashboardView);
        }

        if (livreController != null) {
            
            LivreView livreView = new LivreView(livreController, empruntController, userController, empruntView);
            tabbedPane.addTab("Livres", livreView);
        }

        if (userController != null) {
            UserView userView = new UserView(userController);
            tabbedPane.addTab("Utilisateurs", userView);
        }

        if (empruntController != null) {
            
            tabbedPane.addTab("Emprunts", empruntView);
        }

        // Créer un JPanel pour le header avec des icônes
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));  // Alignement à droite pour les icônes

        // Icône de notification
        ImageIcon notificationIcon = new ImageIcon(new ImageIcon("src/resources/notification.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        notificationButton = new JButton(notificationIcon);
        notificationButton.setToolTipText("Notifications");
        notificationButton.setPreferredSize(new Dimension(30, 30)); // Ajustez la taille du bouton si nécessaire
        headerPanel.add(notificationButton);

        // Icône de profil
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("src/resources/profile.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        profileButton = new JButton(profileIcon);
        profileButton.setToolTipText("Profil");
        profileButton.setPreferredSize(new Dimension(30, 30)); // Ajustez la taille du bouton si nécessaire
        profileButton.addActionListener(this::onProfileClicked);
        headerPanel.add(profileButton);

        // Icône de bascule de thème
        ImageIcon toggleThemeIcon = new ImageIcon(new ImageIcon("src/resources/mode.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        toggleThemeButton = new JButton(toggleThemeIcon);
        toggleThemeButton.setToolTipText("Basculer le thème");
        toggleThemeButton.setPreferredSize(new Dimension(30, 30)); // Ajustez la taille du bouton si nécessaire
        toggleThemeButton.addActionListener(this::toggleTheme);
        headerPanel.add(toggleThemeButton);

        // Label de bienvenue avec le nom de l'utilisateur
        welcomeLabel = new JLabel("Bienvenue, Nom Utilisateur !");
        headerPanel.add(welcomeLabel);

        // Ajouter le header et les onglets dans la fenêtre principale
        add(headerPanel, BorderLayout.NORTH); // Ajouter le header en haut
        add(tabbedPane, BorderLayout.CENTER);  // Ajouter les onglets au centre
    }

    /**
     * Action qui gère le clic sur le bouton de profil
     *
     * @param event L'événement de clic
     */
    private void onProfileClicked(ActionEvent event) {
        // Action à effectuer lors du clic sur le profil
        JOptionPane.showMessageDialog(this, "Profil de l'utilisateur sélectionné.", "Profil", JOptionPane.INFORMATION_MESSAGE);
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

    public static void main(String[] args) {
        // Assurez-vous de lancer l'UI sur le thread de l'Event Dispatching (EDT)
        SwingUtilities.invokeLater(() -> {
            new BibliothequeApp().setVisible(true);
        });
    }
}
