package controllers;

import model.Emprunt;
import model.EmpruntDAO;
import model.LivreDAO;
import model.UserDAO;
import vue.EmpruntView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class EmpruntController {
    private EmpruntDAO empruntModel;
    private UserDAO userDAO;
    private LivreDAO livreDAO;
    private EmpruntView empruntView;

    public EmpruntController(EmpruntView empruntView, String csvFileEmprunts, String csvFileLivres, String csvFileUsers) {
        this.empruntModel = new EmpruntDAO(csvFileEmprunts);
        this.livreDAO = new LivreDAO(csvFileLivres);
        this.userDAO = new UserDAO(csvFileUsers);
        this.empruntView = empruntView;
        ajouterEcouteurs();
        chargerEmprunts("Tous");
    }

    private void ajouterEcouteurs() {
        empruntView.getRetournerButton().addActionListener(e -> {
            try {
                int empruntId = getSelectedEmpruntId();
                retournerLivre(empruntId);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(empruntView, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        empruntView.getRenouvelerButton().addActionListener(e -> {
            try {
                int empruntId = getSelectedEmpruntId();
                renouvelerEmprunt(empruntId);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(empruntView, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        empruntView.getSupprimerButton().addActionListener(e -> {
            try {
                int empruntId = getSelectedEmpruntId();
                supprimerEmprunt(empruntId);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(empruntView, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        empruntView.getSearchButton().addActionListener(e -> {
            String searchTerm = empruntView.getSearchField().getText();
            String searchType = (String) empruntView.getSearchTypeComboBox().getSelectedItem();
            chargerEmprunts(searchTerm, searchType);
        });

        empruntView.getTriComboBox().addActionListener(e -> {
            String selectedCriteria = (String) empruntView.getTriComboBox().getSelectedItem();
            chargerEmprunts(selectedCriteria);
        });
    }

    public void chargerEmprunts(String searchTerm, String searchType) {
        List<Emprunt> emprunts = empruntModel.listerEmprunts(); // Récupérer tous les emprunts
        // Filtrer les emprunts selon le type de recherche
        try {
            if (searchType.equals("ID Emprunt")) {
                emprunts = emprunts.stream().filter(e -> e.getId() == Integer.parseInt(searchTerm)).collect(Collectors.toList());
            } else if (searchType.equals("ID Livre")) {
                emprunts = emprunts.stream().filter(e -> e.getLivreId() == Integer.parseInt(searchTerm)).collect(Collectors.toList());
            } else if (searchType.equals("ID Utilisateur")) {
                emprunts = emprunts.stream().filter(e -> e.getUserId().equals(searchTerm)).collect(Collectors.toList());
            } else if (searchType.equals("Date")) {
                LocalDate date = LocalDate.parse(searchTerm);
                emprunts = emprunts.stream().filter(e -> e.getDateEmprunt().isEqual(date)).collect(Collectors.toList());
            }
        
        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(empruntView, "Erreur de format : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        empruntView.updateEmpruntsTable(emprunts);
    }

    public void chargerEmprunts(String criteria) {
        List<Emprunt> emprunts = empruntModel.listerEmprunts(); // Récupérer tous les emprunts
        // Logique de tri
        switch (criteria) {
            case "En cours":
                emprunts = emprunts.stream().filter(e -> !e.isRendu()).collect(Collectors.toList());
                break;
            case "Historique":
                emprunts = emprunts.stream().filter(Emprunt::isRendu).collect(Collectors.toList());
                break;
            case "Par pénalités":
                emprunts.sort(Comparator.comparingDouble(Emprunt::getPenalite).reversed());
                break;
            default:
                break; // "Tous" ne nécessite pas de filtrage
        }
        empruntView.updateEmpruntsTable(emprunts);
    }

    private int getSelectedEmpruntId() {
        int selectedRow = empruntView.getEmpruntTable().getSelectedRow();
        if (selectedRow == -1) {
            throw new IllegalStateException("Aucun emprunt sélectionné");
        }
        return (int) empruntView.getEmpruntTable().getValueAt(selectedRow, 0);
    }

    public void retournerLivre(int empruntId) {
        try {
            empruntModel.retournerLivre(empruntId);
            chargerEmprunts("Tous"); // Recharge la liste des emprunts
            JOptionPane.showMessageDialog(empruntView, "Livre retourné avec succès !");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(empruntView, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void renouvelerEmprunt(int empruntId) {
        try {
            empruntModel.renouvelerEmprunt(empruntId);
            chargerEmprunts("Tous"); // Recharge la liste des emprunts
            JOptionPane.showMessageDialog(empruntView, "Emprunt prolongé avec succès !");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(empruntView, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void supprimerEmprunt(int empruntId) {
        int confirmation = JOptionPane.showConfirmDialog(empruntView, "Êtes-vous sûr de vouloir supprimer cet emprunt ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            empruntModel.supprimerEmprunt(empruntId);
            chargerEmprunts("Tous"); // Recharge la liste des emprunts
            JOptionPane.showMessageDialog(empruntView, "Emprunt supprimé avec succès !");
        }
    }
}