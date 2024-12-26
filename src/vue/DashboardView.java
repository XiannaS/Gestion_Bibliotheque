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
import org.jfree.data.general.DefaultPieDataset;
import java.util.List; 
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView extends JPanel {
    private static final long serialVersionUID = 1L;
    private LivreController livreController;
    private UserController userController;
    private EmpruntController empruntController;

    public DashboardView(LivreController livreController, UserController userController, EmpruntController empruntController) {
        this.livreController = livreController;
        this.userController = userController;
        this.empruntController = empruntController;

        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        // Panneau supérieur avec le label de bienvenue
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        JLabel welcomeLabel = new JLabel("Bienvenue dans le Tableau de Bord", JLabel.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Panneau principal pour les statistiques et graphiques
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(Color.DARK_GRAY);

        // Panneau des statistiques
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.DARK_GRAY);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel statsLabel = new JLabel("Statistiques");
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statsPanel.add(statsLabel);

        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setBackground(Color.LIGHT_GRAY);
        statsArea.setForeground(Color.BLACK);
        statsPanel.add(new JScrollPane(statsArea));
        mainPanel.add(statsPanel);

        // Panneau des graphiques
        JPanel chartPanel = new JPanel(new GridLayout(2, 1));
        chartPanel.setBackground(Color.DARK_GRAY);
        JFreeChart barChart = createBooksChart();
        ChartPanel barChartPanel = new ChartPanel(barChart);
        chartPanel.add(barChartPanel);

        JFreeChart pieChart = createUsersPieChart();
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        chartPanel.add(pieChartPanel);
        mainPanel.add(chartPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Afficher les statistiques textuelles
        afficherStatistiques(statsArea);

        // Panneau inférieur pour les listes récentes
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel recentBooksPanel = createRecentBooksPanel();
        JPanel recentUsersPanel = createRecentUsersPanel();

        bottomPanel.add(recentBooksPanel);
        bottomPanel.add(recentUsersPanel);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    private void afficherRappels(JTextArea statsArea) {
        StringBuilder rappels = new StringBuilder();
        rappels.append("=== Rappels ===\n");

        // Date actuelle
        LocalDate today = LocalDate.now();

        // Récupérer tous les emprunts
        List<Emprunt> emprunts = empruntController.listerEmprunts();

        // Vérifier les emprunts en retard et ceux dont la date de retour est proche
        for (Emprunt emprunt : emprunts) {
            if (!emprunt.isRendu()) {
                LocalDate dateRetourPrevue = emprunt.getDateRetourPrevue();
                if (dateRetourPrevue.isBefore(today)) {
                    rappels.append("Emprunt en retard : Livre ID ").append(emprunt.getLivreId())
                            .append(", Utilisateur ID ").append(emprunt.getUserId())
                            .append(", Date de retour prévue : ").append(dateRetourPrevue).append("\n");
                } else if (dateRetourPrevue.isEqual(today.plusDays(3)) || dateRetourPrevue.isBefore(today.plusDays(3))) {
                    rappels.append("Rappel : Livre ID ").append(emprunt.getLivreId())
                            .append(", Utilisateur ID ").append(emprunt.getUserId())
                            .append(", Date de retour prévue : ").append(dateRetourPrevue).append("\n");
                }
            }
        }

        statsArea.append(rappels.toString());
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
                    Livre livre = empruntController.getEntityById(String.valueOf(e.getKey()), "Livre");
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

    private JFreeChart createUsersPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Calcul des utilisateurs les plus actifs
        Map<String, Long> empruntsParUser   = empruntController.listerEmprunts().stream()
                .collect(Collectors.groupingBy(emprunt -> String.valueOf(emprunt.getUserId()), Collectors.counting()));

        // Ajouter les données au dataset
        empruntsParUser .forEach((userId, count) -> {
            User user = empruntController.getEntityById(userId, "User"); // Assurez-vous qu'il n'y a pas d'espace
            if (user != null) {
                dataset.setValue(user.getNom() + " " + user.getPrenom(), count);
            } else {
                System.out.println("Utilisateur non trouvé pour l'ID : " + userId); // Journaliser si l'utilisateur n'est pas trouvé
            }
        });

        // Créer le graphique
        return ChartFactory.createPieChart(
                "Répartition des utilisateurs actifs",
                dataset,
                true, true, false);
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
                    User user = empruntController.getEntityById(String.valueOf(e.getKey()), "User");
                    if (user != null) {
                        stats.append(" - ").append(user.getNom()).append(" ").append(user.getPrenom()).append("\n");
                    }
                });

        statsArea.setText(stats.toString());

        // Afficher les rappels
        afficherRappels(statsArea);
    }

    private JPanel createRecentBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Livres Récemment Ajoutés");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        StringBuilder recentBooks = new StringBuilder();
        livreController.getAllLivres().stream()
                .limit(5)
                .forEach(livre -> recentBooks.append(livre.getTitre()).append(" par ").append(livre.getAuteur()).append("\n"));

        textArea.setText(recentBooks.toString());

        return panel;
    }

    private JPanel createRecentUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Utilisateurs Récemment Ajoutés");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(); // Déclarez textArea ici
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        StringBuilder recentUsers = new StringBuilder();
        userController.getAllUsers().stream()
                .limit(5)
                .forEach(user -> recentUsers.append(user.getNom()).append(" ").append(user.getPrenom()).append("\n"));

        textArea.setText(recentUsers.toString());

        return panel;
    }
}
