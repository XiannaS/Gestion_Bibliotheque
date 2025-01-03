package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;
import model.LivreDAO;
import model.UserDAO;
import model.EmpruntDAO;

import javax.swing.*;
import javax.swing.UIManager.*;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

import java.awt.*;
import java.awt.event.ActionEvent;

public class BibliothequeApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    private JButton toggleThemeButton;
    private JButton profileButton;
    private JButton notificationButton;
    private JLabel welcomeLabel;
    private boolean isDarkMode = false; // Commencer avec le mode clair

    // Constructeur de la classe BibliothequeApp
    public BibliothequeApp() {
        setTitle("Gestion de Bibliothèque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation des DAOs
        LivreDAO livreDAO = new LivreDAO("src/resources/books.csv");
        UserDAO userDAO = new UserDAO("src/resources/users.csv");
        EmpruntDAO empruntDAO = new EmpruntDAO("src/resources/emprunt.csv");

        // Initialisation des vues
        LivreView livreView = new LivreView();  // Création sans contrôleur
        EmpruntView empruntView = new EmpruntView();

        // Initialisation des contrôleurs
        EmpruntController empruntController = new EmpruntController(empruntView, "src/resources/emprunt.csv", "src/resources/books.csv", "src/resources/users.csv");
        LivreController livreController = new LivreController(livreView, livreDAO);  // Création du contrôleur avec la vue et le DAO

        // Injecter livreController dans livreView après sa création
        livreView.setLivreController(livreController); 

        // Créer le panneau d'onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Livres", livreView);
        tabbedPane.addTab("Emprunts", empruntView);

        // Ajouter le panneau d'onglets à la fenêtre principale
        add(tabbedPane, BorderLayout.CENTER);

        // Créer un JPanel pour l'entête avec des icônes
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Alignement à droite pour les icônes

        // Icône de notification
        ImageIcon notificationIcon = new ImageIcon(new ImageIcon("src/resources/notification.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        notificationButton = new JButton(notificationIcon);
        notificationButton.setToolTipText("Notifications");
        headerPanel.add(notificationButton);

        // Icône de profil
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("src/resources/profile.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        profileButton = new JButton(profileIcon);
        profileButton.setToolTipText("Profil");
        profileButton.addActionListener(this::onProfileClicked);
        headerPanel.add(profileButton);

        // Icône de bascule de thème
        ImageIcon toggleThemeIcon = new ImageIcon(new ImageIcon("src/resources/mode.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        toggleThemeButton = new JButton(toggleThemeIcon);
        toggleThemeButton.setToolTipText("Basculer le thème");
        toggleThemeButton.addActionListener(this::toggleTheme);
        headerPanel.add(toggleThemeButton);

        // Label de bienvenue avec le nom de l'utilisateur
        welcomeLabel = new JLabel("Bienvenue, Nom Utilisateur !");
        headerPanel.add(welcomeLabel);

        // Ajouter l'entête dans la fenêtre principale
        add(headerPanel, BorderLayout.NORTH); // Ajouter l'entête en haut
    }

    private void onProfileClicked(ActionEvent event) {
        JOptionPane.showMessageDialog(this, "Profil de l'utilisateur sélectionné.", "Profil", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleTheme(ActionEvent event) {
        if (isDarkMode) {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf()); // Mode clair
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        } else {
            try {
                UIManager.setLookAndFeel(new FlatDraculaIJTheme()); // Mode sombre
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
        SwingUtilities.updateComponentTreeUI(this); // Rafraîchir l'interface
        isDarkMode = !isDarkMode; // Basculer le mode
    }

    public static void main(String[] args) {
        // Appliquer FlatLaf par défaut
        try {
            UIManager.setLookAndFeel(new FlatLightLaf()); // Thème clair par défaut
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Lancer l'application
        SwingUtilities.invokeLater(() -> {
            new BibliothequeApp().setVisible(true);
        });
    }
}