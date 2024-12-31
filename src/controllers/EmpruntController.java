package controllers;

import model.Emprunt;
import model.EmpruntDAO;
import model.Livre;
import model.LivreDAO;
import model.User;
import model.UserDAO;
import vue.EmpruntView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class EmpruntController {
    private EmpruntDAO empruntModel;
    private static final int MAX_RENEWALS = 1;
    private UserDAO userDAO;
    private LivreDAO livreDAO;
    private EmpruntView empruntView;
    private List<Livre> livres;
    private List<Emprunt> emprunts; 
    public EmpruntController(EmpruntView empruntView, String csvFileEmprunts, String csvFileLivres, String csvFileUsers) {
        this.empruntModel = new EmpruntDAO(csvFileEmprunts);
        this.livreDAO = new LivreDAO(csvFileLivres);
        this.userDAO = new UserDAO(csvFileUsers);
        this.empruntView = empruntView;
        ajouterEcouteurs();
        chargerEmprunts("Tous");
        
    }
    public boolean hasActiveEmpruntsForBook(int livreId) {
        return empruntModel.listerEmprunts().stream()
                .anyMatch(emprunt -> emprunt.getLivreId() == livreId && !emprunt.isRendu()); // Utilisation de isRendu() au lieu de isReturned()
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
                boolean renouvellement = renouvelerEmprunt(empruntId);
                if (renouvellement) {
                    JOptionPane.showMessageDialog(empruntView, "Renouvellement réussi !");
                    chargerEmprunts("Tous"); // Recharge la liste des emprunts après renouvellement
                } else {
                    JOptionPane.showMessageDialog(empruntView, "Impossible de renouveler cet emprunt.");
                }
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
    // Méthode pour obtenir le titre du livre par ID
    public String getTitreLivreById(int livreId) {
        Livre livre = livreDAO.rechercherParID(livreId);
        return (livre != null) ? livre.getTitre() : "Livre non trouvé";
    }
    
    public void chargerEmprunts(String searchTerm, String searchType) {
        List<Emprunt> emprunts = empruntModel.listerEmprunts(); // Récupérer tous les emprunts
        try {
            switch (searchType) {
                case "ID Emprunt":
                    emprunts = emprunts.stream().filter(e -> e.getId() == Integer.parseInt(searchTerm)).collect(Collectors.toList());
                    break;
                case "ID Livre":
                    emprunts = emprunts.stream().filter(e -> e.getLivreId() == Integer.parseInt(searchTerm)).collect(Collectors.toList());
                    break;
                case "ID Utilisateur":
                    emprunts = emprunts.stream().filter(e -> e.getUserId().equals(searchTerm)).collect(Collectors.toList());
                    break;
                case "Date":
                    LocalDate date = LocalDate.parse(searchTerm);
                    emprunts = emprunts.stream().filter(e -> e.getDateEmprunt().isEqual(date)).collect(Collectors.toList());
                    break;
                default:
                    break; // Aucun filtrage, tout afficher
            }
        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(empruntView, "Erreur de format : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        empruntView.updateEmpruntsTable(emprunts, livreDAO);
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
        empruntView.updateEmpruntsTable(emprunts, livreDAO);
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

    public boolean renouvelerEmprunt(int empruntId) {
        System.out.println("Tentative de renouvellement pour l'emprunt ID: " + empruntId);

        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
        if (emprunt == null) {
            System.out.println("Emprunt non trouvé.");
            return false; // Emprunt non trouvé
        }
        if (emprunt.isRendu()) {
            System.out.println("Emprunt déjà rendu, ne peut pas être renouvelé.");
            return false; // Emprunt déjà rendu, ne peut pas être renouvelé
        }
        if (emprunt.getNombreRenouvellements() >= MAX_RENEWALS) {
            System.out.println("Emprunt atteint le nombre maximum de renouvellements.");
            return false; // Emprunt ne peut pas être renouvelé
        }

        // Logique pour renouveler l'emprunt
        System.out.println("Renouvellement de l'emprunt : nouvelle date de retour prévue : " + emprunt.getDateRetourPrevue().plusDays(14));
        emprunt.setDateRetourPrevue(emprunt.getDateRetourPrevue().plusDays(14)); // Exemple de renouvellement
        emprunt.incrementerNombreRenouvellements();
        empruntModel.updateEmprunt(emprunt); // Mettez à jour l'emprunt dans le modèle
        System.out.println("Renouvellement réussi.");
        return true; // Renouvellement réussi
    }



    public void supprimerEmprunt(int empruntId) {
        int confirmation = JOptionPane.showConfirmDialog(empruntView, "Êtes-vous sûr de vouloir supprimer cet emprunt ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            empruntModel.supprimerEmprunt(empruntId);
            chargerEmprunts("Tous"); // Recharge la liste des emprunts
            JOptionPane.showMessageDialog(empruntView, "Emprunt supprimé avec succès !");
        }
    }
    public void ajouterEmprunt(Emprunt emprunt) {
        // L'ID sera automatiquement généré dans EmpruntDAO lors de l'ajout de l'emprunt
        empruntModel.ajouterEmprunt(emprunt); // Cette méthode gère la génération de l'ID
        chargerEmprunts("Tous"); // Recharge la liste des emprunts après ajout
        JOptionPane.showMessageDialog(empruntView, "Emprunt ajouté avec succès !");
    }

    public String getTitreLivre(int livreId) {
    	for (Livre livre : livres) {
    		if (livre.getId() == livreId) {
    			return livre.getTitre();
    			} } 
    	return "Titre inconnu"; // Retourne une valeur par défaut si le livre n'est pas trouvé }
    }
    
 // Récupérer l'historique des emprunts d'un utilisateur
    public List<Emprunt> getHistoriqueEmprunts(String userId) {
        return empruntModel.listerEmprunts().stream()
                .filter(emprunt -> emprunt.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public int generateEmpruntId() {
        // Logique pour générer un nouvel ID unique pour un emprunt
        return empruntModel.listerEmprunts().size() + 1; // Exemple simple
    }
    
    public boolean hasActiveEmprunts(String userId) {
        // Check if the user has any active loans
        return empruntModel.listerEmprunts().stream()
            .anyMatch(emprunt -> emprunt.getUserId().equals(userId) && !emprunt.isRendu());
    }
    
    public boolean isUserActive(String userId) {
        // Vérifie si l'utilisateur est actif
        User user = userDAO.getUserById(userId);
        return user != null && user.isActive(); // Assurez-vous que User a la méthode isActive
    }

 
    public boolean hasActiveEmpruntForUser(String userId, int livreId) {
        // Utiliser directement l'appel à listerEmprunts pour éviter le problème de liste non initialisée
        return empruntModel.listerEmprunts().stream()
                .anyMatch(emprunt -> emprunt.getUserId().equals(userId)
                        && emprunt.getLivreId() == livreId
                        && !emprunt.isRendu());
    }


}