package vue;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DashboardView extends JPanel {
    private static final long serialVersionUID = 1L;

    // Panneau pour les statistiques
    private JPanel statsPanel;

    // Panneau pour les graphiques
    private JPanel chartPanel;

    // Bouton pour rafraîchir les données
    private JButton refreshButton;

    // Menu déroulant pour les filtres (par exemple, choisir un mois pour afficher les statistiques)
    private JComboBox<String> filterComboBox;

    // Filtres avancés
    private JComboBox<String> genreComboBox;
    private JComboBox<String> periodComboBox;
    private JComboBox<String> userComboBox;

    public DashboardView() {
        setLayout(new BorderLayout(15, 15)); // Espacement entre les composants
        setBackground(new Color(0xEAEAEA)); // Couleur de fond du Dashboard

        // Initialisation du panneau des statistiques
        statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));  // GridLayout avec un espacement
        statsPanel.setBackground(new Color(0xEAEAEA)); // Fond clair pour plus de contraste
        add(createCard(statsPanel, "Statistiques"), BorderLayout.NORTH);

        // Initialisation du panneau des graphiques
        chartPanel = new JPanel(new GridLayout(1, 2, 10, 10));  // Deux graphiques côte à côte
        chartPanel.setBackground(new Color(0xEAEAEA)); // Fond clair pour plus de contraste
        add(createCard(chartPanel, "Graphiques"), BorderLayout.CENTER);

        // Panneau inférieur pour les contrôles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(new Color(0xEAEAEA)); // Fond clair

        // Menu déroulant pour filtrer les statistiques (ex. filtrer par mois)
        filterComboBox = new JComboBox<>(new String[]{"Tous", "Janvier", "Février", "Mars", "Avril"});
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        filterComboBox.setPreferredSize(new Dimension(150, 30));
        filterComboBox.setBackground(Color.WHITE);
        controlPanel.add(filterComboBox);

        // Filtres avancés
        genreComboBox = new JComboBox<>(new String[]{"Tous les genres", "Roman", "Science-fiction", "Biographie"});
        genreComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        genreComboBox.setPreferredSize(new Dimension(150, 30));
        genreComboBox.setBackground(Color.WHITE);
        controlPanel.add(genreComboBox);

        periodComboBox = new JComboBox<>(new String[]{"Tout le temps", "Cette année", "Ce mois"});
        periodComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        periodComboBox.setPreferredSize(new Dimension(150, 30));
        periodComboBox.setBackground(Color.WHITE);
        controlPanel.add(periodComboBox);

        userComboBox = new JComboBox<>(new String[]{"Tous les utilisateurs", "Utilisateur 1", "Utilisateur 2"});
        userComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        userComboBox.setPreferredSize(new Dimension(150, 30));
        userComboBox.setBackground(Color.WHITE);
        controlPanel.add(userComboBox);

        // Bouton pour rafraîchir les graphiques
        refreshButton = new JButton("Rafraîchir");
        refreshButton.setBackground(new Color(0x4CAF50));  // Vert moderne
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // Méthode pour créer une carte avec un panneau intérieur et un titre
    private JPanel createCard(JPanel innerPanel, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xCCCCCC), 1, true), title));
        card.add(innerPanel, BorderLayout.CENTER);
        return card;
    }

    // Mise à jour des statistiques affichées
    public void updateStats(String totalBooks, String activeUsers, String totalLoans) {
        statsPanel.removeAll();
        statsPanel.add(createStatPanel("Total de Livres", totalBooks));
        statsPanel.add(createStatPanel("Utilisateurs Actifs", activeUsers));
        statsPanel.add(createStatPanel("Emprunts Actifs", totalLoans));
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    // Méthode pour générer un panneau de statistiques (détail d'un chiffre)
    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xFFFFFF));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    // Définir le graphique à afficher
    public void setChart(JComponent chart) {
        chartPanel.removeAll();
        chartPanel.add(chart);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // Retourne le bouton de rafraîchissement pour le controller
    public JButton getRefreshButton() {
        return refreshButton;
    }

    // Retourne le menu de filtrage pour les statistiques
    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }

    // Retourne le menu de filtrage pour les genres
    public JComboBox<String> getGenreComboBox() {
        return genreComboBox;
    }

    // Retourne le menu de filtrage pour les périodes
    public JComboBox<String> getPeriodComboBox() {
        return periodComboBox;
    }

    // Retourne le menu de filtrage pour les utilisateurs
    public JComboBox<String> getUserComboBox() {
        return userComboBox;
    }

    // Méthode pour créer un graphique en barres
    public JComponent createBarChart(List<String> bookTitles, List<Integer> borrowCounts) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < bookTitles.size(); i++) {
            dataset.addValue(borrowCounts.get(i), "Emprunts", bookTitles.get(i));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Emprunts par Livre",
                "Livre",
                "Nombre d'Emprunts",
                dataset
        );

        return new ChartPanel(barChart);
    }

    // Méthode pour créer un graphique linéaire (par exemple, Emprunts au fil du temps)
    public JComponent createLineChart(List<Integer> months, List<Integer> borrowCounts) {
        XYSeries series = new XYSeries("Emprunts Mensuels");
        for (int i = 0; i < months.size(); i++) {
            series.add(months.get(i), borrowCounts.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Emprunts Mensuels",
                "Mois",
                "Emprunts",
                dataset
        );

        return new ChartPanel(lineChart);
    }

    // Méthode pour créer un graphique circulaire (pourcentage des livres empruntés)
    public JComponent createPieChart(List<String> categories, List<Integer> values) {
        PieDataset dataset = createPieDataset(categories, values);
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Distribution des Emprunts",
                dataset,
                true,
                true,
                false
        );

        return new ChartPanel(pieChart);
    }

    // Créer un dataset pour le graphique circulaire
    private PieDataset createPieDataset(List<String> categories, List<Integer> values) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < categories.size(); i++) {
            dataset.setValue(categories.get(i), values.get(i));
        }
        return dataset;
    }
}
