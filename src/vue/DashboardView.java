package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;
import model.Emprunt;
import model.Livre;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView extends JPanel {
    private LivreController livreController;
    private UserController userController;
    private EmpruntController empruntController;

    public DashboardView(LivreController livreController, UserController userController, EmpruntController empruntController) {
        this.livreController = livreController;
        this.userController = userController;
        this.empruntController = empruntController;

        setLayout(new BorderLayout());

        // Créer un panneau pour afficher les statistiques
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statsArea);
        add(scrollPane, BorderLayout.CENTER);

        // Afficher les statistiques
        afficherStatistiques(statsArea);
    }

    private void afficherStatistiques(JTextArea statsArea) {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Tableau de Bord ===\n");
        stats.append("Total des livres : ").append(livreController.getAllLivres().size()).append("\n");
        stats.append("Total des utilisateurs : ").append(userController.getAllUsers().size()).append("\n");
        stats.append("Total des emprunts : ").append(empruntController.listerEmprunts().size()).append("\n");
        stats.append("Livres disponibles : ").append(livreController.getAllLivres().stream().filter(Livre::isDisponible).count()).append("\n");
        stats.append("Livres non disponibles : ").append(livreController.getAllLivres().stream().filter(livre -> !livre.isDisponible()).count()).append("\n");

        // Livres les plus empruntés
        stats.append("Livres les plus empruntés :\n");
        empruntController.listerEmprunts().stream()
                .collect(Collectors.groupingBy(Emprunt::getLivreId, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> {
                    Livre livre = empruntController.getLivreById(e.getKey());
                    if (livre != null) {
                        stats.append(" - ").append(livre.getTitre()).append("\n");
                    }
                });

        // Utilisateurs les plus actifs
        stats.append("Utilisateurs les plus actifs :\n");
        empruntController.listerEmprunts().stream()
                .collect(Collectors.groupingBy(Emprunt::getUserId, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> {
                    User user = empruntController.getUserById(e.getKey());
                    if (user != null) {
                        stats.append(" - ").append(user.getNom()).append(" ").append(user.getPrenom()).append("\n");
                    }
                });

        statsArea.setText(stats.toString());
    }
}