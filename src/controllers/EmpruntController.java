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
import java.util.Comparator;
import java.util.stream.Collectors;

public class EmpruntController {
    private EmpruntDAO empruntModel;
    private static final int MAX_RENEWALS = 1;
    private UserDAO userDAO;
    private LivreDAO livreDAO;
    private EmpruntView empruntView;
    private List<Livre> livres;
     // Initialisation de la liste
    public EmpruntController(EmpruntView empruntView, String csvFileEmprunts, String csvFileLivres, String csvFileUsers) {
        this.empruntModel = new EmpruntDAO(csvFileEmprunts);
        this.livreDAO = new LivreDAO(csvFileLivres);
        this.userDAO = new UserDAO(csvFileUsers);
        this.empruntView = empruntView;
        ajouterEcouteurs();
        chargerEmprunts("Tous");
        
    }
    public List<Emprunt> getEmprunts() {
        return empruntModel.listerEmprunts(); // Retourne la liste des emprunts
    }
    public boolean hasActiveEmpruntsForBook(int livreId) {
        return empruntModel.listerEmprunts().stream()
                .anyMatch(emprunt -> emprunt.getLivreId() == livreId && !emprunt.isRendu());
    }

    private void ajouterEcouteurs() {
        empruntView.getRetournerButton().addActionListener(e -> {
            try {
                int empruntId = getSelectedEmpruntId();
                retournerLivre(empruntId);
                chargerEmprunts("Tous"); 
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
        List<Emprunt> emprunts = empruntModel.listerEmprunts();  // Récupérer directement tous les emprunts depuis le DAO
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
        List<Emprunt> emprunts = empruntModel.listerEmprunts(); // Récupérer tous les emprunts depuis le DAO
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
        // Logique pour retourner un livre
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId); // Récupérer l'emprunt par son ID
        if (emprunt != null && !emprunt.isRendu()) {
            // Marquer l'emprunt comme retourné
            empruntModel.retournerLivre(empruntId); // Appel à la méthode qui met à jour l'état de l'emprunt
            // Mettre à jour le livre pour marquer qu'un exemplaire a été retourné
            Livre livre = livreDAO.rechercherParID(emprunt.getLivreId());
            if (livre != null) {
                livre.retourner(); // Appeler la méthode retourner sur le livre
                livreDAO.updateLivre(livre); // Mettre à jour l'état du livre dans le DAO
            } else {
                throw new IllegalStateException("Livre non trouvé.");
            }
        } else {
            throw new IllegalStateException("Cet emprunt a déjà été retourné.");
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
        if (emprunt.getDateRetourPrevue().isBefore(LocalDate.now())) {
            emprunt.setPenalite(emprunt.getPenalite() + 5);  // Assuming 5 is the penalty fee for overdue books
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
        // Vérifiez si le livre est disponible avant d'ajouter l'emprunt
        Livre livre = livreDAO.rechercherParID(emprunt.getLivreId());
        if (livre != null && livre.isDisponible()) {
            empruntModel.ajouterEmprunt(emprunt); // Ajouter l'emprunt au DAO
            livre.emprunter(); // Mettre à jour le livre
            livreDAO.updateLivre(livre); // Mettre à jour le livre dans le DAO
        } else {
            throw new IllegalStateException("Le livre n'est pas disponible pour emprunt.");
        }
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
        return empruntModel.listerEmprunts().stream()
            .anyMatch(emprunt -> emprunt.getUserId().equals(userId) && !emprunt.isRendu());
    }

    
    public boolean isUserActive(String userId) {
        // Vérifie si l'utilisateur est actif
        User user = userDAO.getUserById(userId);
        return user != null && user.isActive(); // Assurez-vous que User a la méthode isActive
    }

  
    public boolean hasActiveEmpruntForUser(String userId, int livreId) {
        return empruntModel.listerEmprunts().stream()
                .anyMatch(emprunt -> emprunt.getUserId().equals(userId)
                        && emprunt.getLivreId() == livreId
                        && !emprunt.isRendu());
    }



}