package controllers;

import model.Livre;
import model.User;
import model.EmpruntDAO;
import model.LivreDAO;
import model.UserDAO;
import vue.LivreView;
import exception.LivreException;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class LivreController {
    private LivreDAO livreDAO;
    private UserDAO userDAO;
    private EmpruntDAO empruntDAO;
    private LivreView livreView;

    // Constructeur
    public LivreController(LivreView livreView, LivreDAO livreDAO, UserDAO userDAO, EmpruntDAO empruntDAO) {
        this.livreView = livreView;
        this.livreDAO = livreDAO;
        this.userDAO = userDAO;
        this.empruntDAO = empruntDAO;

        initListeners();
        loadLivres(); // Charger et afficher les livres dès le lancement
    }

    private void initListeners() {
        // Rechercher un livre
        livreView.getSearchButton().addActionListener(e -> searchLivres());

        // Ajouter un livre
        livreView.getAddButton().addActionListener(e -> showAddLivreForm());
    }

    private void showAddLivreForm() {
        // Appeler la méthode de la vue pour afficher le formulaire d'ajout
        livreView.showAddLivreForm(this);
    }

    public void addLivre(Livre newLivre) {
        try {
            livreDAO.addLivre(newLivre); // Méthode à implémenter dans LivreDAO
            loadLivres(); // Recharger les livres après ajout
        } catch (Exception ex) {
            showMessage("Erreur lors de l'ajout du livre: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLivres() {
        List<Livre> livres = livreDAO.getAllLivres();
        livreView.displayBooks(livres);
    }

    public void searchLivres() {
        try {
            String searchTerm = livreView.getSearchField().getText();
            String criteria = (String) livreView.getCriteriaComboBox().getSelectedItem();
            boolean isAvailable = livreView.getDisponibleCheckBox().isSelected();
            List<Livre> resultats = searchLivres(searchTerm, criteria, isAvailable);
            livreView.displayBooks(resultats);
        } catch (LivreException ex) {
            showMessage(ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Livre> searchLivres(String searchTerm, String criteria, boolean isAvailable) throws LivreException {
        List<Livre> resultats;

        switch (criteria.toLowerCase()) {
            case "titre":
                resultats = searchByTitre(searchTerm);
                break;
            case "auteur":
                resultats = searchByAuteur(searchTerm);
                break;
            case "annee":
                try {
                    int annee = Integer.parseInt(searchTerm);
                    resultats = searchByAnnee(annee);
                } catch (NumberFormatException e) {
                    throw new LivreException("Le terme de recherche pour l'année doit être un nombre valide.");
                }
                break;
            case "isbn":
                resultats = searchByIsbn(searchTerm);
                break;
            default:
                throw new LivreException("Critère de recherche invalide. Veuillez choisir parmi : titre, auteur, année, ISBN.");
        }

        if (isAvailable) {
            resultats = resultats.stream()
                    .filter(Livre::isDisponible)
                    .collect(Collectors.toList());
        }

        return resultats;
    }

    public List<Livre> searchByTitre(String titre) {
        return livreDAO.getAllLivres().stream()
                .filter(livre -> livre.getTitre().toLowerCase().contains(titre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livre> searchByAuteur(String auteur) {
        return livreDAO.getAllLivres().stream()
                .filter(livre -> livre.getAuteur().toLowerCase().contains(auteur.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livre> searchByAnnee(int annee) {
        return livreDAO.getAllLivres().stream()
                .filter(livre -> livre.getAnneePublication() == annee)
                .collect(Collectors.toList());
    }

    public List<Livre> searchByIsbn(String isbn) {
        return livreDAO.getAllLivres().stream()
 .filter(livre -> livre.getIsbn().equals(isbn))
                .collect(Collectors.toList());
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }
}