package controllers;

import model.Livre;
import model.LivreDAO;
import vue.LivreView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class LivreController {
    private LivreDAO livreDAO;
    private LivreView livreView;

    // Constructeur
    public LivreController(LivreView livreView, LivreDAO livreDAO) {
        this.livreView = livreView; // Initialiser le contrôleur avec la vue
        this.livreDAO = livreDAO;
        initListeners();
        loadLivres(); // Charger et afficher les livres dès le lancement
    }

    private void initListeners() {
        livreView.getAddButton().addActionListener(e -> livreView.showAddLivreForm());

        // Ajouter le listener pour le bouton "Choisir Image"
        SwingUtilities.invokeLater(() -> {
            livreView.getChooseImageButton().addActionListener(e -> {
                System.out.println("Le bouton a été cliqué.");
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    livreView.getImageField().setText(selectedFile.getAbsolutePath());  // Affiche le chemin de l'image
                }
            });
     
            livreView.testButton.addActionListener(e -> {
                System.out.println("Test Button Clicked"); // Vérifiez si ce log s'affiche dans la console
            });

     


        livreView.getEditButton().addActionListener(e -> {
            Livre selectedLivre = livreView.getSelectedLivre();
            if (selectedLivre != null) {
                showEditLivreForm(selectedLivre);
            } else {
                showMessage("Veuillez sélectionner un livre à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        livreView.getDeleteButton().addActionListener(e -> {
            Livre selectedLivre = livreView.getSelectedLivre();
            if (selectedLivre != null) {
                confirmDeleteLivre(selectedLivre);
            } else {
                showMessage("Veuillez sélectionner un livre à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        livreView.getBorrowButton().addActionListener(e -> {
            Livre selectedLivre = livreView.getSelectedLivre();
            String userId = livreView.getUserIdField().getText();
            if (selectedLivre != null && !userId.isEmpty()) {
                emprunterLivre(selectedLivre, userId);
            } else {
                showMessage("Veuillez sélectionner un livre et entrer un ID utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    public void loadLivres() {
        List<Livre> livres = livreDAO.getAllLivres();
        livreView.getPopularPanel().removeAll(); // Vider le panneau avant de redessiner

        for (Livre livre : livres) {
            JPanel livrePanel = livreView.createLivrePanel(livre);

            // Récupérer les boutons
            JPanel buttonPanel = (JPanel) livrePanel.getComponent(2); // Assurez-vous que le panneau des boutons est à l'index 2
            JButton editButton = (JButton) buttonPanel.getComponent(0); // Bouton Modifier
            JButton deleteButton = (JButton) buttonPanel.getComponent(1); // Bouton Supprimer

            // Ajouter les ActionListeners
            editButton.addActionListener(e -> showEditLivreForm(livre)); // Appeler la méthode pour modifier le livre
            deleteButton.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(null, 
                    "Êtes-vous sûr de vouloir supprimer ce livre ?", 
                    "Confirmation de suppression", 
                    JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    deleteLivre(livre); // Appeler la méthode pour supprimer le livre
                }
            });

            // Ajouter un listener pour afficher les détails du livre
            livrePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showLivreDetails(livre); // Afficher les détails du livre
                }
            });

            livreView.getPopularPanel().add(livrePanel);
        }

        livreView.getPopularPanel().revalidate(); // Revalider le panneau
       


        livreView.getPopularPanel().repaint(); // Repeindre le panneau
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
            validateLivreFields(titre, auteur, isbn, livreView.getAnneeField().getText(), livreView.getTotalExemplairesField().getText());
            // Génération de l'objet Livre et ajout
            Livre newLivre = new Livre(livreDAO.generateNewId(), titre, auteur, genre, 
                                        Integer.parseInt(livreView.getAnneeField().getText().trim()), 
                                        imageUrl, isbn, description, editeur, 
                                        Integer.parseInt(livreView.getTotalExemplairesField().getText().trim()));
            livreDAO.addLivre(newLivre);
            JOptionPane.showMessageDialog(livreView, "Le livre a été ajouté avec succès.");
            loadLivres();
            livreView.clearAddLivreForm();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(livreView, e.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        }

    }
   
    public void showEditLivreForm(Livre livre) {
        livreView.showEditLivreForm(livre); // Show the edit dialog

  
    }
    
    public void editLivre(Livre livre) {
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
            String anneeText = livreView.getAnneeField().getText().trim();
            if (anneeText.isEmpty()) {
                throw new IllegalArgumentException("L'année de publication ne peut pas être vide.");
            }
            anneePublication = Integer.parseInt(anneeText);

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

            // Mettre à jour les détails du livre
            livre.setTitre(titre);
            livre.setAuteur(auteur);
            livre.setGenre(genre);
            livre.setAnneePublication(anneePublication);
            livre.setImageUrl(imageUrl);
            livre.setIsbn(isbn);
            livre.setDescription(description);
            livre.setEditeur(editeur);
            livre.setTotalExemplaires(totalExemplaires);

            // Mettre à jour le livre dans le modèle
            livreDAO.updateLivre(livre);

            // Afficher un message de succès
            JOptionPane.showMessageDialog(livreView, "Le livre a été modifié avec succès.");

            // Mettre à jour la liste des livres affichés
            loadLivres();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(livreView, "Veuillez entrer des valeurs numériques valides pour l'année et le nombre d'exemplaires.", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(livreView, e.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(livreView, "Erreur lors de la modification du livre: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmDeleteLivre(Livre livre) {
        int confirmation = JOptionPane.showConfirmDialog(livreView, "Êtes-vous sûr de vouloir supprimer ce livre ?", "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            deleteLivre(livre);
        }
    }

    public void deleteLivre(Livre livre) {
        livreDAO.deleteLivre(livre.getId());
        JOptionPane.showMessageDialog(livreView, "Le livre a été supprimé avec succès.");
        loadLivres(); // Mettre à jour la liste des livres affichés
    }

    public void showLivreDetails(Livre livre) {
        livreView.displayLivreDetails(livre); // Afficher les détails du livre

        // Ajouter l'ActionListener pour le bouton Emprunter
        livreView.getBorrowButton().addActionListener(e -> {
            String userId = livreView.getUserIdField().getText();
            if (!userId.isEmpty()) {
                emprunterLivre(livre, userId); // Appeler la méthode pour emprunter le livre
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez entrer un ID utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void emprunterLivre(Livre livre, String userId) {
        // Logique pour emprunter le livre
        if (livre.isDisponible()) {
            // Ajoutez ici la logique pour enregistrer l'emprunt
            JOptionPane.showMessageDialog(null, "Livre emprunté avec succès !");
        } else {
            JOptionPane.showMessageDialog(null, "Désolé, ce livre n'est pas disponible.");
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(livreView, message, title, messageType);
    }
    private void validateLivreFields(String titre, String auteur, String isbn, String anneeText, String totalExemplairesText) throws IllegalArgumentException {
        if (titre.isEmpty()) throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        if (auteur.isEmpty()) throw new IllegalArgumentException("L'auteur ne peut pas être vide.");
        if (isbn.isEmpty()) throw new IllegalArgumentException("L'ISBN ne peut pas être vide.");
        if (anneeText.trim().isEmpty()) throw new IllegalArgumentException("L'année de publication ne peut pas être vide.");
        if (totalExemplairesText.trim().isEmpty()) throw new IllegalArgumentException("Le nombre total d'exemplaires ne peut pas être vide.");
        try {
            Integer.parseInt(anneeText.trim());
            Integer.parseInt(totalExemplairesText.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Veuillez entrer des valeurs numériques valides pour l'année et le nombre d'exemplaires.");
        }
    }

}