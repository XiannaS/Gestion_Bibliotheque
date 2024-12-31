package controllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import vue.DashboardView;
import model.Emprunt;
import model.Livre;
import model.User;

public class DashboardController {
    private DashboardView dashboardView;
    private EmpruntController empruntController;
    private LivreController livreController;
    private UserController userController;

    public DashboardController(DashboardView dashboardView, EmpruntController empruntController, LivreController livreController, UserController userController) {
        this.dashboardView = dashboardView;
        this.empruntController = empruntController;
        this.livreController = livreController;
        this.userController = userController;

        // Ajouter des écouteurs pour les boutons de rafraîchissement et les filtres
        dashboardView.getRefreshButton().addActionListener(e -> refreshDashboard());
        dashboardView.getFilterComboBox().addActionListener(e -> filterDashboardData());
        dashboardView.getGenreComboBox().addActionListener(e -> filterDashboardData());
        dashboardView.getPeriodComboBox().addActionListener(e -> filterDashboardData());
        dashboardView.getUserComboBox().addActionListener(e -> filterDashboardData());

        // Initialiser le tableau de bord
        refreshDashboard();
    } // <-- Assurez-vous qu'il y a bien une accolade fermante à la fin


    // Rafraîchir les données du tableau de bord
 
    

    // Rafraîchir les données du tableau de bord
    private void refreshDashboard() {
        // Récupérer les statistiques
        String totalBooks = String.valueOf(livreController.getAllLivres().size());
        String activeUsers = String.valueOf(userController.getNombreUtilisateursActifs());
        String totalLoans = String.valueOf(empruntController.getEmprunts().size());

        // Mettre à jour la vue avec les statistiques
        dashboardView.updateStats(totalBooks, activeUsers, totalLoans);

        // Appliquer les filtres
        filterDashboardData();
    }
    
    	private void filterDashboardData() {
    	    String selectedFilter = (String) dashboardView.getFilterComboBox().getSelectedItem();
    	    String selectedGenre = (String) dashboardView.getGenreComboBox().getSelectedItem();
    	    String selectedPeriod = (String) dashboardView.getPeriodComboBox().getSelectedItem();
    	    String selectedUser = (String) dashboardView.getUserComboBox().getSelectedItem();

    	    List<Emprunt> filteredEmprunts = empruntController.getEmprunts();

    	    // Filtrer par période
    	    if (!selectedPeriod.equals("Tout le temps")) {
    	        filteredEmprunts = filteredEmprunts.stream()
    	                .filter(e -> filterByPeriod(e, selectedPeriod))
    	                .collect(Collectors.toList());
    	    }

    	    // Filtrer par genre de livre
    	    if (!selectedGenre.equals("Tous les genres")) {
    	        filteredEmprunts = filteredEmprunts.stream()
    	                .filter(e -> {
    	                    Livre livre = livreController.getLivreById(e.getLivreId());
    	                    return livre != null && livre.getGenre().equals(selectedGenre);
    	                })
    	                .collect(Collectors.toList());
    	    }

    	    // Filtrer par utilisateur
    	    if (!selectedUser.equals("Tous les utilisateurs")) {
    	        filteredEmprunts = filteredEmprunts.stream()
    	                .filter(e -> e.getUserId().equals(selectedUser))
    	                .collect(Collectors.toList());
    	    }

    	    // Mise à jour des graphiques avec les emprunts filtrés
    	    updateCharts(filteredEmprunts);
    	}

    	private void updateCharts(List<Emprunt> emprunts) {
    	    // Exemple de données dynamiques pour les graphiques
    	    List<String> bookTitles = emprunts.stream()
    	        .map(e -> {
    	            Livre livre = livreController.getLivreById(e.getLivreId());
    	            return livre != null ? livre.getTitre() : "Livre inconnu"; // Gestion de l'ID inconnu
    	        })
    	        .collect(Collectors.toList());

    	    List<Integer> borrowCounts = emprunts.stream()
    	        .collect(Collectors.groupingBy(Emprunt::getLivreId, Collectors.summingInt(e -> 1)))
    	        .values().stream().collect(Collectors.toList());

    	    // Vérifiez ici les tailles de ces deux listes
    	    System.out.println("bookTitles size: " + bookTitles.size());
    	    System.out.println("borrowCounts size: " + borrowCounts.size());

    	    // Assurez-vous qu'elles ont la même taille avant de créer les graphiques
    	    if (bookTitles.size() == borrowCounts.size()) {
    	        List<Integer> months = emprunts.stream()
    	            .map(e -> e.getDateEmprunt().getMonthValue())
    	            .distinct()
    	            .collect(Collectors.toList());

    	        List<Integer> borrowMonthlyCounts = months.stream()
    	            .map(month -> (int) emprunts.stream()
    	                    .filter(e -> e.getDateEmprunt().getMonthValue() == month)
    	                    .count())
    	            .collect(Collectors.toList());

    	        // Créer et afficher les graphiques
    	        JComponent barChart = dashboardView.createBarChart(bookTitles, borrowCounts);
    	        JComponent lineChart = dashboardView.createLineChart(months, borrowMonthlyCounts);

    	        // Ajouter les graphiques dans le tableau de bord
    	        dashboardView.setChart(barChart);
    	        dashboardView.setChart(lineChart);
    	    } else {
    	        System.out.println("Les tailles des listes ne correspondent pas !");
    	    }
    	}



    // Méthode pour filtrer les emprunts par période (ex. cette année, ce mois)
    private boolean filterByPeriod(Emprunt emprunt, String period) {
        LocalDate now = LocalDate.now();
        switch (period) {
            case "Cette année":
                return emprunt.getDateEmprunt().getYear() == now.getYear();
            case "Ce mois":
                return emprunt.getDateEmprunt().getYear() == now.getYear() &&
                       emprunt.getDateEmprunt().getMonthValue() == now.getMonthValue();
            default:
                return true;
        }
    }
}
