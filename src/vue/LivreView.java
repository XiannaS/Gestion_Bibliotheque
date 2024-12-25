package vue;

import javax.swing.*;
import controllers.EmpruntController; // Importer EmpruntController
import controllers.LivreController;
import controllers.UserController; // Importer UserController
import model.Livre;
import model.User;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LivreView extends JPanel {
    private JTextField searchField, titreField, auteurField, anneeField, imageUrlField,isbnField,descriptionField,editeurField,exemplairesField;
    private JCheckBox disponibleCheckBox;
    private JComboBox<String> criteriaComboBox, genreComboBox;
    private JPanel popularPanel;
    private JPanel detailsPanel; // Panneau pour afficher les détails du livre
    private LivreController livreController;
    private EmpruntController empruntController; // Déclaration de EmpruntController
    private UserController userController; // Déclaration de UserController
    private EmpruntView empruntView;
    public LivreView(LivreController livreController) {
        this.livreController = livreController;
        // Initialiser les contrôleurs ici
        this.empruntController = new EmpruntController("C:/Eclipse/gestionbibli/src/main/resources/ressources/emprunt.csv", ""
        		+ "C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv", 
        		"C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv");
        this.userController = new UserController("C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv");
        this.empruntView = empruntView; 
        initUI();
    }
    private void initUI() {
        setLayout(new BorderLayout());

        // Panneau de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        criteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Année", "ISBN"}); // Ajout de l'ISBN comme critère
        disponibleCheckBox = new JCheckBox("Disponible");
        JButton searchButton = new JButton("Rechercher");

        // Icône d'ajout
        ImageIcon addIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/add-icon.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton addButton = new JButton(addIcon);
        addButton.setToolTipText("Ajouter un Livre");
        addButton.setPreferredSize(new Dimension(30, 30)); // Ajustez la taille du bouton si nécessaire
        addButton.addActionListener(e -> showAddLivreForm()); // Action pour ajouter un livre

        searchPanel.add(new JLabel("Rechercher : "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Critère : "));
        searchPanel.add(criteriaComboBox);
        searchPanel.add(disponibleCheckBox);
        searchPanel.add(searchButton);
        searchPanel.add(addButton); // Ajouter le bouton "Ajouter" au panneau de recherche

        add(searchPanel, BorderLayout.NORTH);

        // Ajout de l'action de recherche
        searchButton.addActionListener(e -> searchLivres());

        // Panneau des livres
        popularPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 4 colonnes
        JScrollPane bookScrollPane = new JScrollPane(popularPanel);
        add(bookScrollPane, BorderLayout.CENTER);

        // Panneau pour afficher les détails du livre
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails du Livre"));
        add(detailsPanel, BorderLayout.EAST); // Ajout du panneau de détails à droite

        // Affichage de tous les livres au démarrage
        displayAllLivres();
    }

 
    
    private void searchLivres() {
        String searchTerm = searchField.getText();
        String selectedCriteria = (String) criteriaComboBox.getSelectedItem();
        boolean isAvailable = disponibleCheckBox.isSelected();
        List<Livre> resultats = livreController.getAllLivres();

        // Filtrer les résultats selon les critères
        if (selectedCriteria.equals("Titre")) {
            resultats.removeIf(livre -> !livre.getTitre().toLowerCase().contains(searchTerm.toLowerCase()));
        } else if (selectedCriteria.equals("Auteur")) {
            resultats.removeIf(livre -> !livre.getAuteur().toLowerCase().contains(searchTerm.toLowerCase()));
        } else if (selectedCriteria.equals("Année")) {
            try {
                int annee = Integer.parseInt(searchTerm);
                resultats.removeIf(livre -> livre.getAnneePublication() != annee);
            } catch (NumberFormatException e) {
                livreController.showMessage("Veuillez entrer une année valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (isAvailable) {
            resultats.removeIf(livre -> !livre.isDisponible());
        }

        updateLivrePanels(resultats);
    }

    public void displayAllLivres() {
        List<Livre> livres = livreController.getAllLivres();
        updateLivrePanels(livres);
    }

    private void updateLivrePanels(List<Livre> livres) {
        popularPanel.removeAll();

        for (Livre livre : livres) {
            JPanel panel = createLivrePanel(livre);
            popularPanel.add(panel);
        }

        popularPanel.revalidate();
        popularPanel.repaint();
    }

    private JPanel createLivrePanel(Livre livre) {
        JPanel panel = new JPanel(new GridBagLayout()); // Utiliser GridBagLayout pour un meilleur contrôle
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Espacement autour des composants

        // Vérifiez si l'URL de l'image est valide
        ImageIcon imageIcon = new ImageIcon(livre.getImageUrl());
        if (imageIcon.getIconWidth() == -1) {
            imageIcon = new ImageIcon("C:\\Eclipse\\gestionbibli\\src\\main\\resources\\ressources\\default-book.jpeg"); // Image par défaut
        }

        // Ajustez l'image pour qu'elle ne soit pas coupée
        imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH));

        JLabel imageLabel = new JLabel(imageIcon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Prendre toute la largeur pour l'image
        panel.add(imageLabel, gbc);

        JLabel titleLabel = new JLabel(livre.getTitre());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Prendre toute la largeur pour le titre
        panel.add(titleLabel, gbc);

        JLabel authorLabel = new JLabel("Auteur : " + livre.getAuteur());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Prendre toute la largeur pour l'auteur
        panel.add(authorLabel, gbc);

        // Panneau pour les boutons d'édition et de suppression
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centrer les boutons

        // Redimensionner les icônes
        ImageIcon editIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/edit-icon.png")
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ImageIcon deleteIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/delete-icon.png")
                .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        // Ajout des icônes de modification et de suppression
        JButton editButton = new JButton(editIcon);
        editButton.setPreferredSize(new Dimension(25, 25)); // Ajustez la taille du bouton si nécessaire
        editButton.addActionListener(e -> updateLivreForm(livre));

        JButton deleteButton = new JButton(deleteIcon);
        deleteButton.setPreferredSize(new Dimension(25, 25)); // Ajustez la taille du bouton si nécessaire
        deleteButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(panel, "Êtes-vous sûr de vouloir supprimer ce livre ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                livreController.deleteLivre(livre.getId());
                displayAllLivres();
            }
        });

        // Ajouter les boutons au panneau des boutons
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Ajouter le panneau des boutons au panneau principal
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Prendre toute la largeur pour le panneau des boutons
        panel.add(buttonPanel, gbc);

        // Ajoutez le MouseListener uniquement à l'image
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showLivreDetails(livre);
            }
        });

        return panel;
    }
    
    private void showLivreDetails(Livre livre) {
        detailsPanel.removeAll(); // Effacer les détails précédents

        // Ajouter les détails du livre
        detailsPanel.add(new JLabel("Titre : " + livre.getTitre()));
        detailsPanel.add(new JLabel("Auteur : " + livre.getAuteur()));
        detailsPanel.add(new JLabel("Année de publication : " + livre.getAnneePublication()));
        detailsPanel.add(new JLabel("Genre : " + livre.getGenre()));
        detailsPanel.add(new JLabel("URL de l'image : " + livre.getImageUrl()));
        detailsPanel.add(new JLabel("Total d'exemplaires : " + livre.getTotalExemplaires())); // Total d'exemplaires
        detailsPanel.add(new JLabel("Exemplaires disponibles : " + livre.getExemplairesDisponibles())); // Exemplaires disponibles
        detailsPanel.add(new JLabel("ISBN : " + livre.getIsbn()));
        detailsPanel.add(new JLabel("Description : " + livre.getDescription()));
        detailsPanel.add(new JLabel("Éditeur : " + livre.getEditeur()));

        // Créer un panneau pour l'ID utilisateur
        JPanel userPanel = new JPanel();
        JTextField userIdField = new JTextField(10); // Champ pour l'ID utilisateur, largeur réduite
        userPanel.add(new JLabel("ID Utilisateur :"));
        userPanel.add(userIdField);

        // Ajouter le panneau de l'ID utilisateur au panneau de détails
        detailsPanel.add(userPanel);

        // Bouton pour emprunter le livre
        JButton borrowButton = new JButton("Emprunter"); 
        borrowButton.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            if (!userId.isEmpty()) {
                User user = empruntController.getUserById(userId);
                if (user != null) {
                    if (livre.getExemplairesDisponibles() > 0) { // Vérifier la disponibilité
                    	empruntController.emprunterLivre(livre, user);
                        if (empruntView != null) {
                            empruntView.chargerEmprunts("Tous"); // Recharger les emprunts
                        }
                        showLivreDetails(livre); // Mettre à jour l'affichage des détails
                    } else {
                        JOptionPane.showMessageDialog(detailsPanel, "Ce livre n'est pas disponible.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(detailsPanel, "Utilisateur non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(detailsPanel, "Veuillez entrer un ID utilisateur valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        detailsPanel.add(borrowButton);

        // Définir une taille fixe pour le panneau de détails
        detailsPanel.setPreferredSize(new Dimension(300, 400)); // Ajustez la taille selon vos besoins

        detailsPanel.revalidate(); // Revalider le panneau pour afficher les nouveaux détails
        detailsPanel.repaint(); // Repeindre le panneau
    }
    
    private void showAddLivreForm() {
	    JDialog addDialog = new JDialog((Frame) null, "Ajouter un Livre", true);
	    addDialog.setLayout(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();

	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(5, 5, 5, 5);

	    titreField = new JTextField();
	    auteurField = new JTextField();
	    anneeField = new JTextField();
	    genreComboBox = new JComboBox<>(new String[]{"Science Fiction", "Histoire", "Roman", "Aventure"});
	    imageUrlField = new JTextField();
	    isbnField = new JTextField(); // Champ pour l'ISBN
	    descriptionField = new JTextField(); // Champ pour la description
	    editeurField = new JTextField(); // Champ pour l'éditeur
	    exemplairesField = new JTextField(); // Champ pour le nombre d'exemplaires

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    addDialog.add(new JLabel("Titre :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(titreField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    addDialog.add(new JLabel("Auteur :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(auteurField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    addDialog.add(new JLabel("Année de publication :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(anneeField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    addDialog.add(new JLabel("Genre :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(genreComboBox, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 4;
	    addDialog.add(new JLabel("URL de l'image :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(imageUrlField, gbc);

	    // Bouton pour choisir l'image
	    JButton chooseImageButton = new JButton("Choisir image du livre");
	    chooseImageButton.addActionListener(e -> {
	        JFileChooser fileChooser = new JFileChooser();
	        int returnValue = fileChooser.showOpenDialog(addDialog);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
	            imageUrlField.setText(selectedFilePath);
	        }
	    });
	    gbc.gridx = 1;
	    gbc.gridy = 5;
	    addDialog.add(chooseImageButton, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 6;
	    addDialog.add(new JLabel("ISBN :"), gbc); // Champ pour l'ISBN
	    gbc.gridx = 1;
	    addDialog.add(isbnField, gbc); // Champ pour l'ISBN

	    gbc.gridx = 0;
	    gbc.gridy = 7;
	    addDialog.add(new JLabel("Description :"), gbc); // Champ pour la description
	    gbc.gridx = 1;
	    addDialog.add(descriptionField, gbc); // Champ pour la description

	    gbc.gridx = 0;
	    gbc.gridy = 8;
	    addDialog.add(new JLabel("Éditeur :"), gbc); // Champ pour l'éditeur
	    gbc.gridx = 1;
	    addDialog.add(editeurField, gbc); // Champ pour l'éditeur

	    gbc.gridx = 0;
	    gbc.gridy = 9;
	    addDialog.add(new JLabel("Nombre d'exemplaires :"), gbc); // Champ pour le nombre d'exemplaires
	    gbc.gridx = 1;
	    addDialog.add(exemplairesField, gbc); // Champ pour le nombre d'exemplaires

	    JButton addButton = new JButton("Ajouter");
	    addButton.addActionListener(e -> addLivre(addDialog));
	    gbc.gridx = 0;
	    gbc.gridy = 10;
	    gbc.gridwidth = 2;
	    addDialog.add(addButton, gbc);

	    addDialog.setSize(400, 500);
	    addDialog.setLocationRelativeTo(this);
	    addDialog.setVisible(true);
	} 
 
    private void addLivre(JDialog dialog) {
        try {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            int annee = Integer.parseInt(anneeField.getText());
            String genre = (String) genreComboBox.getSelectedItem();
            String imageUrl = imageUrlField.getText();
            String isbn = isbnField.getText(); // Champ pour l'ISBN
            String description = descriptionField.getText(); // Champ pour la description
            String editeur = editeurField.getText(); // Champ pour l'éditeur
            int totalExemplaires = Integer.parseInt(exemplairesField.getText()); // Champ pour le nombre total d'exemplaires

            // Créer un livre avec le nombre total d'exemplaires
            Livre livre = new Livre(livreController.getNextLivreId(), titre, auteur, genre, annee, imageUrl, isbn, description, editeur, totalExemplaires);
            livreController.addLivre(livre);

            dialog.dispose();
            displayAllLivres();
            livreController.showMessage("Livre ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            livreController.showMessage("Veuillez entrer une année valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            livreController.showMessage("Erreur dans les champs de saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

private void updateLivreForm(Livre livre) {
    JDialog updateDialog = new JDialog((Frame) null, "Modifier le Livre", true);
    updateDialog.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    titreField = new JTextField(livre.getTitre());
    auteurField = new JTextField(livre.getAuteur());
    anneeField = new JTextField(String.valueOf(livre.getAnneePublication()));
    genreComboBox = new JComboBox<>(new String[]{"Science Fiction", "Histoire", "Roman", "Aventure"});
    genreComboBox.setSelectedItem(livre.getGenre());
    imageUrlField = new JTextField(livre.getImageUrl());
    imageUrlField.setEditable(false); // Rendre le champ non modifiable
    imageUrlField.setBackground(Color.LIGHT_GRAY); // Changer la couleur de fond pour indiquer qu'il est non modifiable
    isbnField = new JTextField(livre.getIsbn());
    descriptionField = new JTextField(livre.getDescription());
    editeurField = new JTextField(livre.getEditeur());
    exemplairesField = new JTextField(String.valueOf(livre.getTotalExemplaires()));

    gbc.gridx = 0;
    gbc.gridy = 0;
    updateDialog.add(new JLabel("Titre :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(titreField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    updateDialog.add(new JLabel("Auteur :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(auteurField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    updateDialog.add(new JLabel("Année de publication :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(anneeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    updateDialog.add(new JLabel("Genre :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(genreComboBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    updateDialog.add(new JLabel("URL de l'image :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(imageUrlField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    updateDialog.add(new JLabel("ISBN :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(isbnField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 6;
    updateDialog.add(new JLabel("Description :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(descriptionField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 7;
    updateDialog.add(new JLabel("Éditeur :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(editeurField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 8;
    updateDialog.add(new JLabel("Nombre d'exemplaires :"), gbc);
    gbc.gridx = 1;
    updateDialog.add(exemplairesField, gbc);

    JButton updateButton = new JButton("Mettre à jour");
    updateButton.addActionListener(e -> {
        try {
            // Vérification de l'URL de l'image
            if (!isValidImageUrl(imageUrlField.getText())) {
                throw new IllegalArgumentException("L'URL de l'image n'est pas valide.");
            }
            updateLivre(livre, updateDialog);
        } catch (Exception ex) {
            livreController.showMessage(ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.gridwidth = 2;
    updateDialog.add(updateButton, gbc);

    updateDialog.setSize(400, 500);
    updateDialog.setLocationRelativeTo(this);
    updateDialog.setVisible(true);
}

private void updateLivre(Livre livre, JDialog dialog) {
    try {
        String titre = titreField.getText();
        String auteur = auteurField.getText();
        int annee = Integer.parseInt(anneeField.getText());
        String genre = (String) genreComboBox.getSelectedItem();
        String imageUrl = imageUrlField.getText();
        String isbn = isbnField.getText();
        String description = descriptionField.getText();
        String editeur = editeurField.getText();
        int totalExemplaires = Integer.parseInt(exemplairesField.getText());

        livre.setTitre(titre);
        livre.setAuteur(auteur);
        livre.setAnneePublication(annee);
        livre.setGenre(genre);
        livre.setImageUrl(imageUrl);
        livre.setIsbn(isbn);
        livre.setDescription(description);
        livre.setEditeur(editeur);
        livre.setTotalExemplaires(totalExemplaires);

        livreController.updateLivre(livre);
        dialog.dispose();
        
        // Appel à showLivreDetails pour mettre à jour l'affichage
        showLivreDetails(livre); // Passer le livre modifié pour afficher les nouvelles valeurs
        livreController.showMessage("Livre mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
    } catch (NumberFormatException e) {
        livreController.showMessage("Veuillez entrer une année et un nombre d'exemplaires valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        livreController.showMessage("Erreur dans les champs de saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
// Méthode pour vérifier si l'URL de l'image est valide
private boolean isValidImageUrl(String url) {
    try {
        ImageIcon imageIcon = new ImageIcon(url);
        return imageIcon.getIconWidth() != -1; // Vérifie si l'image est valide
    } catch (Exception e) {
        return false; // Si une exception est levée, l'URL n'est pas valide
    }
}
  
}