package controllers;

import model.Livre;
import model.LivreDAO;
import model.Emprunt;
import model.EmpruntDAO;
import vue.LivreView;

import javax.swing.*;

import exception.LivreException;

import java.time.LocalDate;

public class LivreController {
    private LivreView livreView;
    private LivreDAO livreDAO;
   
    private EmpruntController empruntController; // Référence à EmpruntController

    public LivreController(LivreView livreView, String livreFilePath, EmpruntController empruntController) {
        this.livreView = livreView;
        this.livreDAO = new LivreDAO(livreFilePath);
        
        this.empruntController = empruntController; // Initialisation du contrôleur d'emprunt

        // Charger et afficher les livres au démarrage
        loadAndDisplayBooks();

        // Ajouter des écouteurs d'événements
        livreView.getAjouterButton().addActionListener(e -> ajouterLivre());
        livreView.getModifierButton().addActionListener(e -> modifierLivre());
        livreView.getSupprimerButton().addActionListener(e -> supprimerLivre());
        livreView.getEmprunterButton().addActionListener(e -> emprunterLivre());
        livreView.getLivresTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Vérifiez si la sélection est terminée
                afficherDetailsLivre();
            }
        });
    }

    public void loadAndDisplayBooks() {
        // Charger les livres et les afficher dans la vue
        livreView.updateLivresTable(livreDAO.getAllLivres());
    }

    public void ajouterLivre() {
        // Logique pour ajouter un livre
        try {
            // Vérifier si le livre existe déjà par ISBN
            String isbn = livreView.getIsbn();
            
            boolean livreExistant = livreDAO.getAllLivres().stream()
                .anyMatch(livre -> livre.getIsbn().equals(isbn));
            
            if (livreExistant) {
                throw new LivreException("Un livre avec cet ISBN existe déjà.");
            }

            // Créer un nouvel objet Livre
            Livre livre = new Livre(
                generateId(), // ID généré
                livreView.getTitre(),
                livreView.getAuteur(),
                livreView.getGenre(),
                livreView.getAnneePublication(),
                "", // imageUrl, si non utilisé
                isbn,
                livreView.getDescription(),
                livreView.getEditeur(),
                livreView.getExemplaires()
            );

            // Ajouter le livre au DAO
            livreDAO.addLivre(livre);
            loadAndDisplayBooks(); // Recharger la liste des livres
            JOptionPane.showMessageDialog(livreView, "Livre ajouté avec succès !");
            
            // Vider le formulaire
            livreView.clearFields(); // Assurez-vous que cette méthode existe dans LivreView
        } catch (LivreException e) {
            JOptionPane.showMessageDialog(livreView, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(livreView, "Erreur : Veuillez entrer un nombre valide pour l'année de publication et le nombre d'exemplaires.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(livreView, "Erreur lors de l'ajout du livre : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

public void modifierLivre() {
    int selectedIndex = livreView.getLivresTable().getSelectedRow();
    
    if (selectedIndex != -1) {
        // Récupérer le livre sélectionné
        Livre livre = livreDAO.getAllLivres().get(selectedIndex);
        
        try {
            // Récupérer les nouvelles valeurs
            String nouveauTitre = livreView.getTitre();
            String nouvelAuteur = livreView.getAuteur();
            String nouveauGenre = livreView.getGenre();
            int anneePublication = livreView.getAnneePublication();
            String nouvelIsbn = livreView.getIsbn();
            String nouvelleDescription = livreView.getDescription();
            String nouvelEditeur = livreView.getEditeur();
            int nouveauxExemplaires = livreView.getExemplaires();

            // Vérifier si l'année de publication est valide
            if (anneePublication < 0) {
                throw new LivreException.InvalidYearException("L'année de publication ne peut pas être négative.");
            }

            // Vérifier si le livre existe déjà (en ignorant le livre actuel)
            boolean livreExistant = livreDAO.getAllLivres().stream()
                .anyMatch(l -> l.getTitre().equalsIgnoreCase(nouveauTitre) && l.getAuteur().equalsIgnoreCase(nouvelAuteur) && l.getId() != livre.getId());
            
            if (livreExistant) {
                throw new LivreException("Un livre avec ce titre et cet auteur existe déjà.");
            }

            // Mettre à jour les détails du livre
            livre.setTitre(nouveauTitre);
            livre.setAuteur(nouvelAuteur);
            livre.setGenre(nouveauGenre);
            livre.setAnneePublication(anneePublication);
            livre.setIsbn(nouvelIsbn);
            livre.setDescription(nouvelleDescription);
            livre.setEditeur(nouvelEditeur);
            livre.setTotalExemplaires(nouveauxExemplaires);

            // Mettre à jour le livre dans le DAO
            livreDAO.updateLivre(livre);
            // Vider le formulaire
            livreView.clearFields();
            loadAndDisplayBooks(); // Recharger la liste des livres
            JOptionPane.showMessageDialog(livreView, "Livre modifié avec succès !");
        } catch (LivreException.InvalidYearException e) {
            JOptionPane.showMessageDialog(livreView, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (LivreException e) {
            JOptionPane.showMessageDialog(livreView, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(livreView, "Erreur : Veuillez entrer un nombre valide pour l'année de publication et le nombre d'exemplaires.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(livreView, "Erreur lors de la modification du livre : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(livreView, "Veuillez sélectionner un livre à modifier.", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }
}
   public void supprimerLivre() {
        int selectedIndex = livreView.getLivresTable().getSelectedRow();
        
        if (selectedIndex != -1) {
            // Récupérer le livre sélectionné
            Livre livre = livreDAO.getAllLivres().get(selectedIndex);
            
            int confirmation = JOptionPane.showConfirmDialog(livreView, "Êtes-vous sûr de vouloir supprimer le livre : " + livre.getTitre() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                // Supprimer le livre du DAO
                livreDAO.deleteLivre(livre.getId()); // Assurez-vous que cette méthode existe dans LivreDAO
                loadAndDisplayBooks(); // Recharger la liste des livres
                JOptionPane.showMessageDialog(livreView, "Livre supprimé avec succès !");
            }
        } else {
            JOptionPane.showMessageDialog(livreView, "Veuillez sélectionner un livre à supprimer.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void emprunterLivre() {
        int selectedIndex = livreView.getLivresTable().getSelectedRow();
        
        if (selectedIndex != -1) {
            String userIdStr = JOptionPane.showInputDialog(livreView, "Veuillez entrer votre ID utilisateur :");
            
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    Livre livre = livreDAO.getAllLivres().get(selectedIndex);
                    
                    if (livre.isDisponible()) {
                        livre.emprunter();
                        livreDAO.updateLivre(livre);
                        
                        // Créer un nouvel emprunt
                        Emprunt emprunt = new Emprunt(empruntController.generateEmpruntId(), livre.getId(), String.valueOf(userId), LocalDate.now(), 
                                                       LocalDate.now().plusDays(14), null, false, 0);
                        // Utiliser la méthode d'EmpruntController pour ajouter l'emprunt
                        empruntController.ajouterEmprunt(emprunt);
                        
                        loadAndDisplayBooks();
                        JOptionPane.showMessageDialog(livreView, "Livre emprunté avec succès !");
                    } else {
                        JOptionPane.showMessageDialog(livreView, "Ce livre n'est pas disponible pour emprunt.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(livreView, "ID utilisateur invalide. Veuillez entrer un nombre.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(livreView, "Erreur lors de l'emprunt du livre : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(livreView, "Veuillez sélectionner un livre à emprunter.", "Avertissement", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void afficherDetailsLivre() {
        int selectedIndex = livreView.getLivresTable().getSelectedRow(); // Utilisez getSelectedRow()
        if (selectedIndex != -1) {
            Livre livre = livreDAO.getAllLivres().get(selectedIndex);
            livreView.setDetails(livre);
        }
    }
    private int generateId() {
        // Logique pour générer un nouvel ID unique pour un livre
        return livreDAO.getAllLivres().size() + 1; // Exemple simple
    }


}