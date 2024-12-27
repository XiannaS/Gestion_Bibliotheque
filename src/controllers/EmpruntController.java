package controllers;

import model.Emprunt;
import model.EmpruntDAO;
import model.Livre;
import model.LivreDAO;
import model.User;
import model.UserDAO;
import vue.EmpruntView;
import javax.swing.*;
import exception.EmpruntException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des emprunts de livres.
 * Permet d'effectuer diverses actions liées aux emprunts comme emprunter, retourner, renouveler des livres, etc.
 */
public class EmpruntController {

    private EmpruntDAO empruntModel;
    private UserDAO userDAO;
    private LivreDAO livreDAO;
    private EmpruntView empruntView;
    private static final Logger LOGGER = Logger.getLogger(EmpruntController.class.getName());

    /**
     * Constructeur pour initialiser les DAOs pour les emprunts, les utilisateurs et les livres.
     *
     * @param empruntView La vue liée au contrôleur
     * @param csvFileEmprunts Le fichier CSV contenant les emprunts.
     * @param csvFileLivres Le fichier CSV contenant les livres.
     * @param csvFileUsers Le fichier CSV contenant les utilisateurs.
     */
    public EmpruntController(EmpruntView empruntView, String csvFileEmprunts, String csvFileLivres, String csvFileUsers) {
        this.empruntModel = new EmpruntDAO(csvFileEmprunts);
        this.livreDAO = new LivreDAO(csvFileLivres);
        this.userDAO = new UserDAO(csvFileUsers);
        this.empruntView = empruntView;
        ajouterEcouteurs();
        chargerEmprunts("Tous");
    }

    // Méthode centralisée pour afficher les messages d'information et d'erreur
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(empruntView, message, title, messageType);
    }

    
    /**
     * Permet à un utilisateur d'emprunter un livre.
     * 
     * @param livre Le livre à emprunter.
     * @param user L'utilisateur qui souhaite emprunter le livre.
     */
    public void emprunterLivre(Livre livre, User user) {
        // Vérification si le livre est disponible
        if (!livre.isDisponible()) {
            showMessage("Aucun exemplaire disponible pour emprunt.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier si l'utilisateur est actif
        if (!user.isStatut()) {
            showMessage("L'utilisateur n'est pas actif.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier les pénalités
        List<Emprunt> empruntsEnCours = getEmpruntsEnCours();
        for (Emprunt emprunt : empruntsEnCours) {
            if (emprunt.getUserId().equals(user.getId()) && emprunt.getPenalite() > 0) {
                showMessage("L'utilisateur a des pénalités. Emprunt impossible.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Vérifier si l'utilisateur a déjà emprunté ce livre
        if (hasUserAlreadyBorrowedBook(user, livre)) {
            showMessage("L'utilisateur a déjà emprunté ce livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un emprunt
        LocalDate dateRetourPrevue = LocalDate.now().plusDays(7);
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), dateRetourPrevue, null, false, 0);
        
        // Ajouter l'emprunt au modèle
        empruntModel.ajouterEmprunt(emprunt);

        // Mise à jour de la disponibilité du livre
        livre.emprunter();
        livreDAO.updateLivre(livre); 
        chargerEmprunts("Tous");
        showMessage("Livre emprunté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Vérifie si un utilisateur a déjà emprunté ce livre.
     * 
     * @param user L'utilisateur.
     * @param livre Le livre.
     * @return true si l'utilisateur a déjà emprunté ce livre, false sinon.
     */
    public boolean hasUserAlreadyBorrowedBook(User user, Livre livre) {
        return empruntModel.listerEmprunts().stream()
                .anyMatch(e -> e.getUserId().equals(user.getId()) && e.getLivreId() == livre.getId() && !e.isRendu());
    }

    /**
     * Permet de retourner un livre emprunté.
     *
     * @param empruntId L'ID de l'emprunt à retourner.
     */
    public void retournerLivre(int empruntId) {
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
        if (emprunt == null || emprunt.isRendu()) {
            showMessage("Emprunt non trouvé ou déjà retourné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Marquer l'emprunt comme retourné
        emprunt.retournerLivre();

        // Récupérer le livre à partir de l'ID de l'emprunt
        Livre livre = getEntityById(String.valueOf(emprunt.getLivreId()), "Livre"); // Utilisez l'ID du livre de l'emprunt
        if (livre != null) {
            livre.retourner(); // Mettre à jour la disponibilité du livre
            livreDAO.updateLivre(livre); // Sauvegarder les changements du livre
        } else {
            showMessage("Livre non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mise à jour de l'emprunt
        empruntModel.updateEmprunt(emprunt);
        chargerEmprunts("Tous");
        showMessage("Livre retourné avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Retourne la liste des emprunts en cours (non retournés).
     *
     * @return La liste des emprunts en cours.
     */
    public List<Emprunt> getEmpruntsEnCours() {
        return empruntModel.listerEmprunts().stream()
                .filter(emprunt -> !emprunt.isRendu())
                .toList();
    }

    /**
     * Retourne l'historique des emprunts (retournés).
     *
     * @return La liste des emprunts retournés.
     */
    public List<Emprunt> getHistoriqueEmprunts() {
        return empruntModel.listerEmprunts().stream()
                .filter(Emprunt::isRendu)
                .toList();
    }

    /**
     * Renouvelle un emprunt si possible.
     *
     * @param empruntId L'ID de l'emprunt à renouveler.
     */
    public void renouvelerEmprunt(int empruntId) throws EmpruntException {
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
		if (emprunt == null || emprunt.isRendu()) {
		    showMessage("Emprunt non trouvé ou déjà retourné.", "Erreur", JOptionPane.ERROR_MESSAGE);
		    return;
		}

		if (emprunt.getPenalite() > 0) {
		    showMessage("L'emprunt a une pénalité. Renouvellement impossible.", "Erreur", JOptionPane.ERROR_MESSAGE);
		    return;
		}

		if (emprunt.getNombreRenouvellements() >= 1) {
		    showMessage("Le renouvellement ne peut être effectué plus d'une fois.", "Erreur", JOptionPane.ERROR_MESSAGE);
		    return;
		}

		// Renouveler l'emprunt
		emprunt.setNombreRenouvellements(emprunt.getNombreRenouvellements() + 1);
		LocalDate nouvelleDateRetourPrevue = emprunt.getDateRetourPrevue().plusDays(14);
		emprunt.setDateRetourPrevue(nouvelleDateRetourPrevue);

		empruntModel.updateEmprunt(emprunt);
		 chargerEmprunts("Tous");
		showMessage("Emprunt renouvelé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Supprime un emprunt spécifique en fonction de son ID.
     *
     * @param empruntId L'ID de l'emprunt à supprimer.
     */
    public void supprimerEmprunt(int empruntId) {
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
        if (emprunt == null) {
            showMessage("Emprunt non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        empruntModel.supprimerEmprunt(empruntId);
        chargerEmprunts("Tous");
        showMessage("Emprunt supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Ajoute les écouteurs nécessaires aux composants de la vue.
     */
  
  private void ajouterEcouteurs() {
       // empruntView.getEmprunterButton().addActionListener(e -> {
          //  Livre livre = empruntView.getSelectedLivre();
          //  User user = empruntView.getSelectedUser();
           // emprunterLivre(livre, user);
       // });

        empruntView.getRetournerButton().addActionListener(e -> {
            int empruntId = empruntView.getSelectedEmpruntId();
            retournerLivre(empruntId);
        });

        empruntView.getRenouvelerButton().addActionListener(e -> {
            int empruntId = empruntView.getSelectedEmpruntId();
            try {
				renouvelerEmprunt(empruntId);
			} catch (EmpruntException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        empruntView.getSupprimerButton().addActionListener(e -> {
            int empruntId = empruntView.getSelectedEmpruntId();
            supprimerEmprunt(empruntId);
        });
    }
   
	 public List<Emprunt> listerEmprunts() {
	        return empruntModel.listerEmprunts(); // Utilise le DAO pour lister les emprunts
	    }
	   
	 public <T> T getEntityById(String id, String entityType) {
		    try {
		        switch (entityType) {
		            case "User":
		                return (T) userDAO.rechercherParID(id);  // Recherche d'un utilisateur
		            case "Livre":
		                return (T) livreDAO.rechercherParID(Integer.parseInt(id));  // Recherche d'un livre
		            case "Emprunt":
		                return (T) empruntModel.getEmpruntById(Integer.parseInt(id));  // Recherche d'un emprunt
		            default:
		                throw new IllegalArgumentException("Type d'entité inconnu : " + entityType);
		        }
		    } catch (Exception e) {
		        showMessage("Erreur lors de la récupération de l'entité : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		        return null;
		    }
		}


    public Emprunt getLastEmprunt() {
        List<Emprunt> emprunts = empruntModel.listerEmprunts();
        if (emprunts.isEmpty()) {
            return null; // Aucun emprunt
        }
        return emprunts.get(emprunts.size() - 1); // Retourne le dernier emprunt
    }
    public void chargerEmprunts(String filtre) {
        List<Emprunt> emprunts = new ArrayList<>();

        switch (filtre) {
            case "Tous":
                emprunts = this.listerEmprunts(); // Utilisez `this` pour référencer l'instance actuelle
                break;
            case "En cours":
                emprunts = this.getEmpruntsEnCours();
                break;
            case "Historique":
                emprunts = this.getHistoriqueEmprunts();
                break;
            // Ajoutez d'autres filtres si nécessaire
        }
        empruntView.updateEmpruntsTable(emprunts);
    }

}
