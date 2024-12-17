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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EmpruntController {
    private EmpruntDAO empruntModel;
    private EmpruntView empruntView;
	private UserDAO userDAO;
	private LivreDAO livreDAO;
	 private List<Emprunt> emprunts;
    // Constructeur
    public EmpruntController(String csvFileEmprunts, String csvFileLivres, String csvFileUsers) {
        this.empruntModel = new EmpruntDAO(csvFileEmprunts);
        this.livreDAO = new LivreDAO(csvFileLivres);
        this.userDAO = new UserDAO(csvFileUsers);
        this.emprunts = new ArrayList<>();
    }

    public void emprunterLivre(Livre livre, User user) {
        if (!livre.isDisponible()) {
            JOptionPane.showMessageDialog(null, "Le livre est déjà emprunté.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!user.isStatut()) {
            JOptionPane.showMessageDialog(null, "L'utilisateur n'est pas actif.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier si l'utilisateur a déjà emprunté ce livre
        List<Emprunt> empruntsEnCours = getEmpruntsEnCours();
        for (Emprunt emprunt : empruntsEnCours) {
            if (emprunt.getLivreId() == livre.getId() && emprunt.getUserId().equals(user.getId())) {
                JOptionPane.showMessageDialog(null, "L'utilisateur a déjà emprunté ce livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Définir la date de retour prévue (par exemple, 14 jours après l'emprunt)
        LocalDate dateRetourPrevue = LocalDate.now().plusDays(14);
        
        // Créer l'emprunt avec tous les paramètres nécessaires
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), dateRetourPrevue, null, false, 0);
        
        empruntModel.ajouterEmprunt(emprunt);
        livre.setDisponible(false); // Le livre n'est plus disponible
        livreDAO.updateLivre(livre); // Mettre à jour le statut du livre dans le modèle
        JOptionPane.showMessageDialog(null, "Livre emprunté avec succès.");
    }
    
    public void retournerLivre(int empruntId) {
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
        if (emprunt == null || emprunt.isRendu()) {
            JOptionPane.showMessageDialog(null, "Emprunt non trouvé ou déjà retourné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        emprunt.retournerLivre();
        Livre livre = getLivreById(emprunt.getLivreId());
        if (livre != null) {
            livre.setDisponible(true);
            livreDAO.updateLivre(livre);
        }

        empruntModel.updateEmprunt(emprunt); // Mise à jour de l'emprunt
        JOptionPane.showMessageDialog(null, "Livre retourné avec succès.");
    }


    // **Nouvelle méthode : Récupérer un livre par son ID**
    public Livre getLivreById(int id) {
        return livreDAO.rechercherParID(id);
    }

    // **Nouvelle méthode : Récupérer un utilisateur par son ID**
    public User getUserById(String userId) {
        return userDAO.rechercherParID(userId);
    }

    public List<Emprunt> getEmpruntsEnCours() {
        return empruntModel.listerEmprunts().stream()
                .filter(emprunt -> !emprunt.isRendu())
                .toList();
    }

    public List<Emprunt> getHistoriqueEmprunts() {
        return empruntModel.listerEmprunts().stream()
                .filter(Emprunt::isRendu)
                .toList();
    }

    public List<Emprunt> getEmpruntsTriesParPenalite() {
        return empruntModel.listerEmprunts().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getPenalite(), e1.getPenalite()))
                .toList();
    }
    
    public List<Emprunt> listerEmprunts() {
        return empruntModel.listerEmprunts(); // Appel de la méthode depuis EmpruntDAO
    }
    
    public void supprimerEmprunt(int empruntId) {
        try {
            empruntModel.supprimerEmprunt(empruntId);
            JOptionPane.showMessageDialog(null, "Emprunt supprimé avec succès.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'emprunt : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void renouvelerEmprunt(int empruntId) {
        try {
            empruntModel.renouvelerEmprunt(empruntId);
            JOptionPane.showMessageDialog(null, "Emprunt renouvelé avec succès.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors du renouvellement de l'emprunt : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    public List<Emprunt> rechercherEmprunts(String searchType, String searchText) {
        List<Emprunt> resultats = new ArrayList<>();
        for (Emprunt emprunt : listerEmprunts()) {
            switch (searchType) {
                case "ID Emprunt":
                    if (String.valueOf(emprunt.getId()).contains(searchText)) {
                        resultats.add(emprunt);
                    }
                    break;
                case "ID Livre":
                    if (String.valueOf(emprunt.getLivreId()).contains(searchText)) {
                        resultats.add(emprunt);
                    }
                    break;
                case "ID Utilisateur":
                    if (String.valueOf(emprunt.getUserId()).contains(searchText)) {
                        resultats.add(emprunt);
                    }
                    break;
                case "Date":
                    // Supposons que vous ayez une méthode pour formater la date
                    if (emprunt.getDateEmprunt().toString().contains(searchText) || 
                        emprunt.getDateRetourPrevue().toString().contains(searchText)) {
                        resultats.add(emprunt);
                    }
                    break;
                default:
                    break;
            }
        }
        return resultats;
    }
 
}