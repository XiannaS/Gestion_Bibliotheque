package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;

import javax.swing.*;
import com.formdev.flatlaf.FlatDarkLaf;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme; // Importer le thème Dracula

public class BibliothequeApp extends JFrame {
    private JTabbedPane tabbedPane;
    private JButton toggleThemeButton;
    private boolean isDracula = false; // Suivi du thème actuel
    private boolean isDarkMode = true;
    public BibliothequeApp() {
        setTitle("Gestion de Bibliothèque");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Initialisation des contrôleurs avec gestion des exceptions
        LivreController livreController = null;
        UserController userController = null;
        EmpruntController empruntController = null;

        try {
            livreController = new LivreController("C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des livres : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        try {
            userController = new UserController("C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des utilisateurs : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        try {
            empruntController = new EmpruntController(
                "C:/Eclipse/gestionbibli/src/main/resources/ressources/emprunt.csv",
                "C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv",
                "C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des emprunts : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Onglet pour le tableau de bord
        if (livreController != null && userController != null && empruntController != null) {
            DashboardView dashboardView = new DashboardView(livreController, userController, empruntController);
            tabbedPane.addTab("Tableau de Bord", dashboardView);
        }

        // Onglet pour gérer les livres
        if (livreController != null) {
            LivreView livreView = new LivreView(livreController);
            tabbedPane.addTab("Livres", livreView);
        }

        // Onglet pour gérer les utilisateurs
        if (userController != null) {
            UserView userView = new UserView(userController);
            tabbedPane.addTab("Utilisateurs", userView);
        }

        // Onglet pour gérer les emprunts
        if (empruntController != null) {
            EmpruntView empruntView = new EmpruntView(empruntController);
            tabbedPane.addTab("Emprunts", empruntView);
        }

        // Ajouter un bouton pour basculer entre les thèmes
        toggleThemeButton = new JButton("Basculer le thème");
        toggleThemeButton.addActionListener(e -> toggleTheme());  // Action pour basculer entre les thèmes
        add(toggleThemeButton, "South"); // Placer le bouton en bas de la fenêtre

        add(tabbedPane);
    }
    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleTheme() {
        try {
            // Si le thème est déjà activé, on ne fait rien
            if (isDarkMode) {
                if (!(UIManager.getLookAndFeel() instanceof FlatLightLaf)) {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    SwingUtilities.updateComponentTreeUI(this);
                }
            } else {
                if (!(UIManager.getLookAndFeel() instanceof FlatDraculaIJTheme)) {
                    UIManager.setLookAndFeel(new FlatDraculaIJTheme());
                    SwingUtilities.updateComponentTreeUI(this);
                }
            }
            isDarkMode = !isDarkMode;
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void applyDraculaTheme() {
        try {
            // Si le thème actuel n'est pas déjà le Dracula, l'appliquer
            if (!(UIManager.getLookAndFeel() instanceof FlatDraculaIJTheme)) {
                UIManager.setLookAndFeel(new FlatDraculaIJTheme());
                isDarkMode = true;
                SwingUtilities.updateComponentTreeUI(this);
            }
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Appliquer le thème par défaut (par exemple, FlatLightLaf ou autre)
                UIManager.setLookAndFeel(new FlatLightLaf()); // Par défaut, ici on met un thème clair

                // Créer et afficher l'application
                BibliothequeApp app = new BibliothequeApp();
                app.setVisible(true);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }
}
