package controllers;

import model.Livre;
import model.LivreDAO;
import vue.LivreView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.util.List;

public class LivreController {
    private LivreDAO livreDAO;
    private LivreView livreView;

    public LivreController(LivreView livreView, LivreDAO livreDAO) {
        this.livreView = livreView;
        this.livreDAO = livreDAO;

        initListeners();
        loadLivres(); // Charger et afficher les livres dès le lancement
    }

    private void initListeners() {
        livreView.getAddButton().addActionListener(e -> showAddLivreForm());
        livreView.getEditButton().addActionListener(e -> editLivreClicked());
        livreView.getDeleteButton().addActionListener(e -> deleteLivreClicked());
     // Ajouter le listener pour le bouton "Choisir Image"
        livreView.getChooseImageButton().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                livreView.getImageField().setText(selectedFile.getAbsolutePath()); // Mettre le chemin de l'image dans le champ
            }
        });
    }

    private void showAddLivreForm() {
        livreView.showAddLivreForm();

        // Ajouter un écouteur pour le bouton "Ajouter" dans le formulaire
        livreView.getAddFormSubmitButton().addActionListener(e -> addLivreClicked());
    }

  public void addLivreClicked() {
    // Récupérer les données du formulaire
    String titre = livreView.getTitreField().getText();
    String auteur = livreView.getAuteurField().getText();
    String genre = livreView.getGenreField().getText();
    String isbn = livreView.getIsbnField().getText();
    String description = livreView.getDescriptionField().getText();
    String editeur = livreView.getEditeurField().getText();
    String imageUrl = livreView.getImageField().getText();
    int anneePublication;
    int totalExemplaires;

    try {
    	// Vérifier l'année de publication
    	String anneeText = livreView.getAnneeField().getText().trim(); // Utiliser trim() pour enlever les espaces
    	if (anneeText.isEmpty()) {
    	    throw new IllegalArgumentException("L'année de publication ne peut pas être vide.");
    	}
    	try {
    	    anneePublication = Integer.parseInt(anneeText);
    	} catch (NumberFormatException e) {
    	    throw new IllegalArgumentException("Veuillez entrer une année valide.");
    	}

        // Vérifier le nombre total d'exemplaires
        String totalExemplairesText = livreView.getTotalExemplairesField().getText();
        if (totalExemplairesText.isEmpty()) {
            throw new IllegalArgumentException("Le nombre total d'exemplaires ne peut pas être vide.");
        }
        totalExemplaires = Integer.parseInt(totalExemplairesText);

        // Vérifier si le titre est vide
        if (titre.isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }

        // Vérifier si l'auteur est vide
        if (auteur.isEmpty()) {
            throw new IllegalArgumentException("L'auteur ne peut pas être vide.");
        }

        // Vérifier si l'ISBN est vide
        if (isbn.isEmpty()) {
            throw new IllegalArgumentException("L'ISBN ne peut pas être vide.");
        }

        // Vérifier si le livre existe déjà
        if (livreDAO.livreExists(isbn)) {
            JOptionPane.showMessageDialog(livreView, "Un livre avec cet ISBN existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return; // Ne pas continuer si le livre existe déjà
        }

        // Générer un nouvel ID
        int id = livreDAO.generateNewId();

        // Créer un nouvel objet Livre
        Livre newLivre = new Livre(id, titre, auteur, genre, anneePublication, imageUrl, isbn, description, editeur, totalExemplaires);

        // Ajouter le livre au modèle
        livreDAO.addLivre(newLivre);

        // Afficher un message de succès
        JOptionPane.showMessageDialog(livreView, "Le livre a été ajouté avec succès.");

        // Mettre à jour la liste des livres affichés
        List<Livre> livres = livreDAO.getAllLivres(); // Récupérer la liste mise à jour des livres
        livreView.displayBooks(livres);

        // Réinitialiser le formulaire
        livreView.clearAddLivreForm();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(livreView, "Veuillez entrer des valeurs numériques valides pour l'année et le nombre d'exemplaires.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(livreView, e.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(livreView, "Erreur lors de l'ajout du livre: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
  
    private void editLivreClicked() {
        if (livreView.getSelectedLivre() != null) {
            livreView.showEditLivreForm(livreView.getSelectedLivre()); // Afficher le formulaire de modification
        } else {
            showMessage("Veuillez sélectionner un livre à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLivreClicked() {
        if (livreView.getSelectedLivre() != null) {
            int confirmation = JOptionPane.showConfirmDialog(null, 
                "Êtes-vous sûr de vouloir supprimer ce livre ?", 
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    livreDAO.deleteLivre(livreView.getSelectedLivre().getId()); // Supprimer le livre de la base de données
                    loadLivres(); // Recharger les livres après suppression
                    showMessage("Livre supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    showMessage("Erreur lors de la suppression du livre: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            showMessage("Veuillez sélectionner un livre à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    private void loadLivres() {
        List<Livre> livres = livreDAO.getAllLivres();
        livreView.displayBooks(livres);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(livreView, message, title, messageType);
    }
}