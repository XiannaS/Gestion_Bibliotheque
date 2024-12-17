package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;

import javax.swing.*;

public class BibliothequeApp extends JFrame {
    private JTabbedPane tabbedPane;

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

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BibliothequeApp app = new BibliothequeApp();
            app.setVisible(true);
        });
    }
}