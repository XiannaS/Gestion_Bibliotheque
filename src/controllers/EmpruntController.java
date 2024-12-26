package controllers;

import model.Emprunt;
import model.EmpruntDAO;
import model.Livre;
import model.LivreDAO;
import model.User;
import model.UserDAO;
import javax.swing.*;
import exception.EmpruntException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion des emprunts de livres.
 * Permet d'effectuer diverses actions liées aux emprunts comme emprunter, retourner, renouveler des livres, etc.
 */
public class EmpruntController {

    private EmpruntDAO empruntModel;
    private UserDAO userDAO;
    private LivreDAO livreDAO;
    private static final Logger LOGGER = Logger.getLogger(EmpruntController.class.getName());

    /**
     * Constructeur pour initialiser les DAOs pour les emprunts, les utilisateurs et les livres.
     *
     * @param csvFileEmprunts Le fichier CSV contenant les emprunts.
     * @param csvFileLivres Le fichier CSV contenant les livres.
     * @param csvFileUsers Le fichier CSV contenant les utilisateurs.
     */
    public EmpruntController(String csvFileEmprunts, String csvFileLivres, String csvFileUsers) {
        this.empruntModel = new EmpruntDAO(csvFileEmprunts);
        this.livreDAO = new LivreDAO(csvFileLivres);
        this.userDAO = new UserDAO(csvFileUsers);
        new ArrayList<>();
    }

    /**
     * Récupère une entité (Livre ou User) par son ID.
     *
     * @param id L'ID de l'entité à rechercher.
     * @param entityType Le type d'entité (Livre ou User).
     * @param <T> Le type de l'entité.
     * @return L'entité correspondante à l'ID et au type spécifié.
     * @throws IllegalArgumentException Si le type d'entité est inconnu.
     */
    public <T> T getEntityById(String id, String entityType) {
        switch (entityType) {
            case "Livre":
                return (T) livreDAO.rechercherParID(Integer.parseInt(id));
            case "User":
                return (T) userDAO.rechercherParID(id);
            case "Emprunt": // Ajout du cas pour Emprunt
                return (T) empruntModel.getEmpruntById(Integer.parseInt(id)); // Assurez-vous que cette méthode existe dans EmpruntDAO
            default:
                throw new IllegalArgumentException("Type d'entité inconnu");
        }
    }

    /**
     * Permet à un utilisateur d'emprunter un livre.
     * 
     * @param livre Le livre à emprunter.
     * @param user L'utilisateur qui souhaite emprunter le livre.
     */
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

    /**
     * Permet de retourner un livre emprunté.
     *
     * @param empruntId L'ID de l'emprunt à retourner.
     */
    public void retournerLivre(int empruntId) {
        // Retrieve the loan record by its ID
        Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
        
        // Check if the loan exists and if it has already been returned
        if (emprunt == null || emprunt.isRendu()) {
            JOptionPane.showMessageDialog(null, "Emprunt non trouvé ou déjà retourné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mark the loan as returned
        emprunt.retournerLivre();

        // Retrieve the associated book using getEntityById
        Livre livre = (Livre) getEntityById(String.valueOf(emprunt.getLivreId()), "Livre");
        
        // Check if the book was found
        if (livre != null) {
            livre.retourner(); // Increase the number of available copies
            livreDAO.updateLivre(livre); // Update the book in the model
        } else {
            JOptionPane.showMessageDialog(null, "Livre non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return; // Exit if the book is not found
        }

        // Update the loan record in the model
        empruntModel.updateEmprunt(emprunt);
        
        // Notify the user of success
        JOptionPane.showMessageDialog(null, "Livre retourné avec succès.");
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
     * Retourne la liste des emprunts triés par pénalités, du plus élevé au plus bas.
     *
     * @return La liste des emprunts triés par pénalités.
     */
    public List<Emprunt> getEmpruntsTriesParPenalite() {
        return empruntModel.listerEmprunts().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getPenalite(), e1.getPenalite()))
                .toList();
    }

    /**
     * Recherche des emprunts selon différents critères de recherche.
     *
     * @param searchType Le type de critère de recherche (ID Emprunt, ID Livre, ID Utilisateur, Date).
     * @param searchText Le texte de recherche.
     * @return Une liste d'emprunts correspondant à la recherche.
     */
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

    /**
     * Permet de charger les emprunts en fonction d'un critère de tri spécifique.
     *
     * @param triOption Le critère de tri (En cours, Historique, Par pénalités, Tous).
     * @return La liste des emprunts triés selon l'option choisie.
     */
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
            case "Tous":
                emprunts = listerEmprunts();
                break;
            default:
                emprunts = listerEmprunts(); // Par défaut, lister tous les emprunts
        }

        return emprunts;
    }

    /**
     * Supprime tous les emprunts dans le modèle.
     */
    public void supprimerTousLesEmprunts() {
        try {
            empruntModel.supprimerTousLesEmprunts();
            JOptionPane.showMessageDialog(null, "Tous les emprunts ont été supprimés avec succès.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression des emprunts : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Supprime un emprunt spécifique en fonction de son ID.
     *
     * @param empruntId L'ID de l'emprunt à supprimer.
     */
    public void supprimerEmprunt(int empruntId) {
        try {
            empruntModel.supprimerEmprunt(empruntId);
            JOptionPane.showMessageDialog(null, "Emprunt supprimé avec succès.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'emprunt : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renouvelle un emprunt, si possible.
     * Un emprunt peut être renouvelé une seule fois, et seulement si aucune pénalité n'existe.
     *
     * @param empruntId L'ID de l'emprunt à renouveler.
     */
    public void renouvelerEmprunt(int empruntId) {
        try {
            Emprunt emprunt = empruntModel.getEmpruntById(empruntId);
            if (emprunt == null || emprunt.isRendu()) {
                throw new EmpruntException.EmpruntDejaRetourneException("Emprunt non trouvé ou déjà retourné.");
            }

            // Contrôle des pénalités avant renouvellement
            if (emprunt.getPenalite() > 0) {
                throw new EmpruntException.PenaliteExistanteException("Il y a une pénalité. Renouvellement impossible.");
            }

            // Vérification pour empêcher un renouvellement supplémentaire
            if (emprunt.getNombreRenouvellements() >= 1) {
                throw new EmpruntException.RenouvellementNonAutoriseException("Le renouvellement ne peut être effectué plus d'une fois.");
            }

            // Incrémenter le nombre de renouvellements
            emprunt.setNombreRenouvellements(emprunt.getNombreRenouvellements() + 1);

            // Effectuer le renouvellement
            empruntModel.renouvelerEmprunt(empruntId);
            JOptionPane.showMessageDialog(null, "Emprunt renouvelé avec succès.");
            LOGGER.info("Emprunt renouvelé : " + empruntId);
        } catch (EmpruntException.PenaliteExistanteException | EmpruntException.EmpruntDejaRetourneException | EmpruntException.RenouvellementNonAutoriseException e) {
            LOGGER.warning(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.severe("Erreur inattendue : " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erreur inattendue : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retourne la liste de tous les emprunts.
     *
     * @return La liste de tous les emprunts.
     */
    public List<Emprunt> listerEmprunts() {
        return empruntModel.listerEmprunts();
    }

    public Emprunt getLastEmprunt() {
        List<Emprunt> emprunts = empruntModel.listerEmprunts();
        if (emprunts.isEmpty()) {
            return null; // Aucun emprunt trouvé
        }
        // Retourner le dernier emprunt (en supposant que la liste est triée par ID ou date)
        return emprunts.get(emprunts.size() - 1); // Dernier emprunt dans la liste
    }
    /**
     * Récupère un emprunt par son ID.
     *
     * @param empruntId L'ID de l'emprunt à récupérer.
     * @return L'emprunt correspondant à l'ID.
     */
   
}
