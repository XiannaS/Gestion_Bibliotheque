package controllers;

import model.Livre;
import model.User;
import model.EmpruntDAO;
import model.LivreDAO;
import model.UserDAO;
import vue.LivreView;
import exception.LivreException;

import javax.swing.*;

import java.awt.FlowLayout;
import java.awt.GridLayout;
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

    public void showAddLivreForm() {
        JDialog addLivreDialog = new JDialog();
        addLivreDialog.setTitle("Ajouter un Livre");
        addLivreDialog.setSize(400, 300);
        addLivreDialog.setLayout(new GridLayout(0, 2));

        // Champs pour le formulaire d'ajout
        JTextField titreField = new JTextField();
        JTextField auteurField = new JTextField();
        JTextField anneeField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField editeurField = new JTextField();
        JTextField totalExemplairesField = new JTextField();
        
        // Champ pour l'URL de l'image
        JTextField imageField = new JTextField();
        imageField.setEditable(false); // Rendre le champ non modifiable
        JButton chooseImageButton = new JButton("Choisir une image");

        // Panneau pour le champ d'image et le bouton
        JPanel imagePanel = new JPanel(new FlowLayout());
        imagePanel.add(imageField);
        imagePanel.add(chooseImageButton);

        // Ajout des champs au dialog
        addLivreDialog.add(new JLabel("Titre:"));
        addLivreDialog.add(titreField);
        addLivreDialog.add(new JLabel("Auteur:"));
        addLivreDialog.add(auteurField);
        addLivreDialog.add(new JLabel("Année:"));
        addLivreDialog.add(anneeField);
        addLivreDialog.add(new JLabel("ISBN:"));
        addLivreDialog.add(isbnField);
        addLivreDialog.add(new JLabel("Description:"));
        addLivreDialog.add(descriptionField);
        addLivreDialog.add(new JLabel("Éditeur:"));
        addLivreDialog.add(editeurField);
        addLivreDialog.add(new JLabel("Total Exemplaires:"));
        addLivreDialog.add(totalExemplairesField);
        addLivreDialog.add(new JLabel("Image:"));
        addLivreDialog.add(imagePanel); // Ajouter le panneau avec le champ et le bouton

        // Bouton pour soumettre le formulaire
        JButton submitButton = new JButton("Ajouter");
        addLivreDialog.add(submitButton);

        addLivreDialog.setVisible(true); // Afficher le dialogue
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