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
    /**
	 * 
	 */
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
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Tableau de Bord", dashboardView);
        }

        // Onglet pour gérer les livres
        if (livreController != null) {
        	// Créer une instance de EmpruntView
        	EmpruntView empruntView = new EmpruntView(empruntController);

        	// Créer une instance de LivreView en passant empruntView
        	LivreView livreView = new LivreView(livreController, empruntController, userController, empruntView);
            tabbedPane.addTab("Livres", livreView);
        }

        // Onglet pour gérer les utilisateurs
        if (userController != null) {
            UserView userView = new UserView(userController);
            tabbedPane.addTab("Utilisateurs", userView);
        }

        // Onglet pour gérer les emprunts
        if (empruntController != null) {
        	// Créer une instance de EmpruntView
        	EmpruntView empruntView = new EmpruntView(empruntController);

        	new LivreView(livreController, empruntController, userController, empruntView);
            tabbedPane.addTab("Emprunts", empruntView);
        }

        // Créer un JPanel pour le header avec des icônes
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));  // Alignement à droite pour les icônes

        // Icône de notification
        ImageIcon notificationIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/notification.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        notificationButton = new JButton(notificationIcon);
        notificationButton.setToolTipText("Notifications");
        notificationButton.setPreferredSize(new Dimension(30, 30)); // Ajustez la taille du bouton si nécessaire
        headerPanel.add(notificationButton);

        // Icône de profil
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/profile.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        profileButton = new JButton(profileIcon);
        profileButton.setToolTipText("Profil");
        profileButton.setPreferredSize(new Dimension(30, 30)); // Ajust ez la taille du bouton si nécessaire
        profileButton.addActionListener(this::onProfileClicked);
        headerPanel.add(profileButton);

        // Icône de bascule de thème
        ImageIcon toggleThemeIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/mode.png")
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

    public boolean isDarkMode() {
        return isDarkMode;
    }

    private void toggleTheme(ActionEvent e) {
        try {
            // Si le thème est actuellement sombre, basculer vers le thème clair
            if (isDarkMode) {
                if (!(UIManager.getLookAndFeel() instanceof FlatLightLaf)) {
                    // Changer le Look and Feel vers FlatLightLaf
                    UIManager.setLookAndFeel(new FlatLightLaf());
                }
            } else {
                if (!(UIManager.getLookAndFeel() instanceof FlatDraculaIJTheme)) {
                    // Changer le Look and Feel vers FlatDraculaIJTheme
                    UIManager.setLookAndFeel(new FlatDraculaIJTheme());
                }
            }

            // Actualiser l'UI en appelant updateComponentTreeUI après le changement de Look and Feel
            SwingUtilities.updateComponentTreeUI(this); // Cela va forcer une mise à jour visuelle

            // Changer le mode pour le prochain appel
            isDarkMode = !isDarkMode;
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }


    private void onProfileClicked(ActionEvent e) {
        // Afficher un dialogue de confirmation pour savoir si l'utilisateur veut se déconnecter
        int choice = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment quitter ?", "Confirmation de déconnexion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        // Si l'utilisateur clique sur "Oui"
        if (choice == JOptionPane.YES_OPTION) {
            // Vous pouvez fermer la fenêtre actuelle et ouvrir la fenêtre de connexion.
            this.dispose(); // Ferme la fenêtre principale (BibliothequeApp)

            // Créer et afficher la fenêtre de connexion (LoginView)
            LoginView loginView = new LoginView(null);
            loginView.setVisible(true);
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
// main pour login
//public static void main(String[] args) {
  //  SwingUtilities.invokeLater(() -> {
    //    try {
      //      UIManager.setLookAndFeel(new FlatLightLaf()); // Appliquer le thème par défaut

            // Créer et afficher la fenêtre de connexion
        //    LoginView loginView = new LoginView(new BibliothequeApp());
          //  loginView.setVisible(true);
        //} catch (UnsupportedLookAndFeelException e) {
//    e.printStackTrace();
            //  }
// });
//}