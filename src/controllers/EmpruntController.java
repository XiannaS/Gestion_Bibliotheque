package controllers;

import model.Emprunt;
import model.EmpruntDAO;
import model.Livre;
import model.LivreDAO;
import model.User;
import model.UserDAO;
import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntController {
    private EmpruntDAO empruntModel;
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
        // Vérifier si le livre est disponible
        if (!livre.isDisponible()) {
            JOptionPane.showMessageDialog(null, "Aucun exemplaire disponible pour emprunt.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Vérifier si l'utilisateur est actif
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
        
        // Ajouter l'emprunt au modèle
        empruntModel.ajouterEmprunt(emprunt);
        
        // Mettre à jour la disponibilité du livre
        livre.emprunter(); // Décrémenter le nombre d'exemplaires disponibles
        livreDAO.updateLivre(livre); // Mettre à jour le livre dans le modèle
        JOptionPane.showMessageDialog(null, "Livre emprunté avec succès.");
    }
    
    public void retournerLivre(int empruntId) {
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
        if (emprunt == null || emprunt.isRendu()) {
            JOptionPane.showMessageDialog(null, "Emprunt non trouvé ou déjà retourné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Marquer l'emprunt comme retourné
        emprunt.retournerLivre();
        
        // Récupérer le livre associé à l'emprunt
        Livre livre = getLivreById(emprunt.getLivreId());
        if (livre != null) {
            livre.retourner(); // Augmenter le nombre d'exemplaires disponibles
            livreDAO.updateLivre(livre); // Mettre à jour le livre dans le modèle
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

    public List<Emprunt> chargerEmprunts(String triOption) {
        List<Emprunt> emprunts;

        switch (triOption) {
            case "En cours":
                emprunts = getEmpruntsEnCours();
                break;
            case "Historique":
                emprunts = getHistoriqueEmprunts();
                break;
            case "Par pénalités":
                emprunts = getEmpruntsTriesParPenalite();
                break;
            default:
                emprunts = listerEmprunts();
        }

        return emprunts;
    }
    public void supprimerTousLesEmprunts() {
        try {
            empruntModel.supprimerTousLesEmprunts(); // Méthode à implémenter dans EmpruntDAO
            JOptionPane.showMessageDialog(null, "Tous les emprunts ont été supprimés avec succès.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression des emprunts : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Emprunt getEmpruntById(int empruntId) {
        return empruntModel.getEmpruntById(empruntId);
    }
}