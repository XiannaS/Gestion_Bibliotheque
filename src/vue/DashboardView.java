package vue;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;
import model.Emprunt;
import model.Livre;
import model.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LivreController livreController;
    private UserController userController;
    private EmpruntController empruntController;

    public DashboardView(LivreController livreController, UserController userController, EmpruntController empruntController) {
        this.livreController = livreController;
        this.userController = userController;
        this.empruntController = empruntController;

        setLayout(new BorderLayout());

        // Créer un panneau pour afficher les statistiques
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 1));

        // Créer un graphique des livres les plus empruntés
        JFreeChart chart = createBooksChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        statsPanel.add(chartPanel);

        // Afficher les statistiques sous forme textuelle
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statsArea);
        statsPanel.add(scrollPane);

        add(statsPanel, BorderLayout.CENTER);

        // Afficher les statistiques textuelles
        afficherStatistiques(statsArea);
    }

    private JFreeChart createBooksChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Calcul des livres les plus empruntés
        Map<Integer, Long> empruntsParLivre = empruntController.listerEmprunts().stream()
                .collect(Collectors.groupingBy(Emprunt::getLivreId, Collectors.counting()));


        // Trier les livres par nombre d'emprunts
        empruntsParLivre.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .forEach(e -> {
                	Livre livre = empruntController.getLivreById(e.getKey().intValue());

                    if (livre != null) {
                        dataset.addValue(e.getValue(), "Emprunts", livre.getTitre());
                    }
                });

        // Créer le graphique
        return ChartFactory.createBarChart(
                "Top 5 des livres les plus empruntés",
                "Livre",
                "Nombre d'emprunts",
                dataset
        );
    }

    private void afficherStatistiques(JTextArea statsArea) {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Tableau de Bord ===\n");
        stats.append("Total des livres : ").append(livreController.getAllLivres().size()).append("\n");
        stats.append("Total des utilisateurs : ").append(userController.getAllUsers().size()).append("\n");
        stats.append("Total des emprunts : ").append(empruntController.listerEmprunts().size()).append("\n");
        stats.append("Livres disponibles : ").append(livreController.getAllLivres().stream().filter(Livre::isDisponible).count()).append("\n");
        stats.append("Livres non disponibles : ").append(livreController.getAllLivres().stream().filter(livre -> !livre.isDisponible()).count()).append("\n");

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
