package vue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import controllers.LivreController;
import model.Livre;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
 

public class LivreView extends JPanel {
    private JTextField searchField;
    private JComboBox<String> criteriaComboBox;
    private JCheckBox disponibleCheckBox;
    private JButton searchButton, addButton, editButton, deleteButton;
    private JPanel popularPanel;
    private JPanel detailsPanel;
    private JButton borrowButton;  // Bouton Emprunter
    private JTextField userIdField;
    private Livre selectedLivre;   // Livre sélectionné pour les détails
    // Champs pour le formulaire d'ajout
    private JTextField titreField;
    private JTextField auteurField;
    private JTextField anneeField;
    private JTextField isbnField;
    private JTextField descriptionField;
    private JTextField editeurField;
    private JTextField totalExemplairesField;
    private JTextField imageField;
    private JTextField genreField;
    private JButton addFormSubmitButton; // Déclaration du bouton pour soumettre le formulaire
    private JButton chooseImageButton;
    public LivreView() {
        initUI(); // Initialiser l'interface utilisateur
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Initialiser le panneau des détails
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails du Livre"));
        detailsPanel.setPreferredSize(new Dimension(300, 300)); // Taille fixe pour tous les livres
        detailsPanel.setVisible(false); // Cacher le panneau des détails au démarrage
        add(detailsPanel, BorderLayout.EAST);

        // Initialiser les boutons
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        chooseImageButton = new JButton("Choisir Image");
        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        criteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Année", "ISBN"});
        disponibleCheckBox = new JCheckBox("Disponible");
        searchButton = new JButton("Rechercher");

        // Ajout d'icône pour le bouton Ajouter
        addButton.setIcon(resizeIcon(createIcon("src/resources/add-icon.png"), 20, 20)); // Redimensionner à 30x30 pixels
        
        searchPanel.add(new JLabel("Rechercher : "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Critère : "));
        searchPanel.add(criteriaComboBox);
        searchPanel.add(disponibleCheckBox);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);

        add(searchPanel, BorderLayout.NORTH);

     // Affichage des livres
        popularPanel = new JPanel();
        popularPanel.setLayout(new GridLayout(0, 5, 10, 10)); // 0 pour le nombre de lignes, 5 colonnes, espacement de 10 pixels
        JScrollPane bookScrollPane = new JScrollPane(popularPanel);
        add(bookScrollPane, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Initialiser le bouton "Emprunter"
        borrowButton = new JButton("Emprunter");
        borrowButton.setEnabled(false); // Désactiver le bouton au début
        borrowButton.setVisible(false); // Masquer le bouton au début
        detailsPanel.add(borrowButton); // Ajouter le bouton au panneau des détails

        // Initialiser le champ pour l'ID utilisateur
        userIdField = new JTextField();
        userIdField.setColumns(5); // Limiter à 5 caractères visibles
        userIdField.setVisible(false); // Le cacher au début
        detailsPanel.add(userIdField); // Ajouter le champ au panneau des détails

        // Initialiser les champs pour le formulaire d'ajout
        titreField = new JTextField();
        auteurField = new JTextField();
        anneeField = new JTextField();
        isbnField = new JTextField();
        descriptionField = new JTextField();
        editeurField = new JTextField();
        totalExemplairesField = new JTextField();
        imageField = new JTextField();
        genreField = new JTextField(); 
    }

 public void showAddLivreForm() {
    JDialog addLivreDialog = new JDialog();
    addLivreDialog.setTitle("Ajouter un Livre");
    addLivreDialog.setSize(500, 400);
    addLivreDialog.setLayout(new GridBagLayout());
    addLivreDialog.setLocationRelativeTo(null); // Centrer le dialogue

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // Espacement autour des composants
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Ligne 1 : Titre
    gbc.gridx = 0;
    gbc.gridy = 0;
    addLivreDialog.add(new JLabel("Titre:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(titreField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 2 : Auteur
    gbc.gridx = 0;
    gbc.gridy = 1;
    addLivreDialog.add(new JLabel("Auteur:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(auteurField, gbc); // Utiliser le champ de texte de la classe
    // Ligne 3 : Genre
    gbc.gridx = 0;
    gbc.gridy = 2;
    addLivreDialog.add(new JLabel("Genre:"), gbc);
    gbc.gridx = 1;
    String[] genres = {"Fiction", "Non-Fiction", "Science", "Fantasy", "Biography"};
    JComboBox<String> genreComboBox = new JComboBox<>(genres); // Créer le JComboBox pour le genre
    addLivreDialog.add(genreComboBox, gbc); // Ajouter le JComboBox au dialogue


    // Ligne 4 : Année
    gbc.gridx = 0;
    gbc.gridy = 3;
    addLivreDialog.add(new JLabel("Année:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(anneeField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 5 : ISBN
    gbc.gridx = 0;
    gbc.gridy = 4;
    addLivreDialog.add(new JLabel("ISBN:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(isbnField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 6 : Description
    gbc.gridx = 0;
    gbc.gridy = 5;
    addLivreDialog.add(new JLabel("Description:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(descriptionField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 7 : Éditeur
    gbc.gridx = 0;
    gbc.gridy = 6;
    addLivreDialog.add(new JLabel("Éditeur:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(editeurField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 8 : Total Exemplaires
    gbc.gridx = 0;
    gbc.gridy = 7;
    addLivreDialog.add(new JLabel("Total Exemplaires:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(totalExemplairesField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 9 : Image
    gbc.gridx = 0;
    gbc.gridy = 8;
    addLivreDialog.add(new JLabel("Image:"), gbc);
    gbc.gridx = 1;
    addLivreDialog.add(imageField, gbc); // Utiliser le champ de texte de la classe

    // Ligne 10 : Choisir Image Button
    gbc.gridx = 1;
    gbc.gridy = 9;
    JButton chooseImageButton = new JButton("Choisir Image");
    addLivreDialog.add(chooseImageButton, gbc);

    // Ligne 11 : Submit Button
    gbc.gridx = 0;
    gbc.gridy = 10;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    addFormSubmitButton = new JButton("Ajouter");
    addLivreDialog.add(addFormSubmitButton, gbc);

    // Show the dialog
    addLivreDialog.setVisible(true);
}
    public JButton getChooseImageButton() {
        return chooseImageButton; // Assurez-vous que chooseImageButton est un champ de classe
    }
    public JButton getAddFormSubmitButton() {
        return addFormSubmitButton; // Retourner le bouton pour l'ajout
    }
    
    // Méthode pour afficher la liste des livres
    public void displayBooks(List<Livre> livres) {
        popularPanel.removeAll(); // Vider le panneau avant de redessiner
        for (Livre livre : livres) {
            JPanel livrePanel = createLivrePanel(livre); // Créez un panneau pour chaque livre
            popularPanel.add(livrePanel);
        }
        popularPanel.revalidate(); // Revalider le panneau
        popularPanel.repaint(); // Repeindre le panneau
    }

    
    private JPanel createLivrePanel(Livre livre) {
        JPanel livrePanel = new JPanel();
        livrePanel.setLayout(new BoxLayout(livrePanel, BoxLayout.Y_AXIS));
        livrePanel.setBackground(new Color(0, 0, 0, 0)); // Fond transparent

        // Créer l'image de couverture
        ImageIcon imageIcon = new ImageIcon(livre.getImageUrl());
        if (imageIcon.getIconWidth() == -1) {
            imageIcon = new ImageIcon("src/resources/default-book.jpeg");
        }
        
        // Redimensionner l'image de couverture
        imageIcon = resizeIcon(imageIcon, 150, 200); // Redimensionner à 150x200 pixels
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajouter le titre du livre en italique
        JLabel titleLabel = new JLabel(livre.getTitre());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.ITALIC)); // Titre en italique
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        livrePanel.add(imageLabel);
        livrePanel.add(titleLabel);
        editButton.setIcon(resizeIcon(createIcon("src/resources/edit-icon.png"), 20, 20)); // Redimensionner à 30x30 pixels
        deleteButton.setIcon(resizeIcon(createIcon("src/resources/delete-icon.png"), 20, 20)); // Redimensionner à 30x30 pixels

        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        // Panneau pour les boutons "Modifier" et "Supprimer"
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Disposition centrée
        buttonPanel.setBackground(new Color(0, 0, 0, 0)); // Fond transparent pour le panneau des boutons


  
        // Ajouter le panneau des boutons au panneau du livre
        livrePanel.add(buttonPanel);

        
        return livrePanel;
    }
    
// Méthode pour afficher les détails du livre sélectionné
	public void displayLivreDetails(Livre livre) {
    detailsPanel.removeAll(); // Vider les détails du livre précédent

    detailsPanel.add(new JLabel("Titre: " + livre.getTitre()));
    detailsPanel.add(new JLabel("Auteur: " + livre.getAuteur()));
    detailsPanel.add(new JLabel("Année: " + livre.getAnneePublication()));
    detailsPanel.add(new JLabel("ISBN: " + livre.getIsbn()));
    detailsPanel.add(new JLabel("Description: " + livre.getDescription()));
    detailsPanel.add(new JLabel("Éditeur: " + livre.getEditeur()));
    detailsPanel.add(new JLabel("Exemplaires de livre: " + livre.getTotalExemplaires()));
    detailsPanel.add(new JLabel("Exemplaires restants en stock: " + livre.getExemplairesDisponibles()));

    // Panneau utilisateur pour champ ID et bouton emprunter
    JPanel userIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    userIdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel userIdLabel = new JLabel("ID Utilisateur:");
    userIdField = new JTextField();
    userIdField.setColumns(10); // Largeur pour 10 caractères
    userIdField.setMargin(new Insets(2, 2, 2, 2));

    userIdPanel.add(userIdLabel);
    userIdPanel.add(userIdField);
    detailsPanel.add(userIdPanel);

    // Ajouter le bouton Emprunter
    borrowButton.setVisible(true); // Rendre le bouton visible
    detailsPanel.add(Box.createVerticalStrut(10)); // Espacement
    detailsPanel.add(borrowButton);

    detailsPanel.setVisible(true); // Rendre le panneau visible
    detailsPanel.revalidate(); // Revalider le panneau
    detailsPanel.repaint(); // Repeindre le panneau
}
 
 
	public void showEditLivreForm(Livre livre) {
	    JDialog editLivreDialog = new JDialog();
	    editLivreDialog.setTitle("Modifier un Livre");
	    editLivreDialog.setSize(400, 300);
	    editLivreDialog.setLayout(new GridLayout(0, 2));
	
	    // Champs pour le formulaire de modification
	    JTextField titreField = new JTextField(livre.getTitre());
	    JTextField auteurField = new JTextField(livre.getAuteur());
	    JTextField anneeField = new JTextField(String.valueOf(livre.getAnneePublication()));
	    JTextField isbnField = new JTextField(livre.getIsbn());
	    JTextField descriptionField = new JTextField(livre.getDescription());
	    JTextField editeurField = new JTextField(livre.getEditeur());
	    JTextField totalExemplairesField = new JTextField(String.valueOf(livre.getTotalExemplaires()));
	    JTextField imageField = new JTextField(livre.getImageUrl());
	
	    // Ajout des champs au dialog
	    editLivreDialog.add(new JLabel("Titre:"));
	    editLivreDialog.add(titreField);
	    editLivreDialog.add(new JLabel("Auteur:"));
	    editLivreDialog.add(auteurField);
	    editLivreDialog.add(new JLabel("Année:"));
	    editLivreDialog.add(anneeField);
	    editLivreDialog.add(new JLabel("ISBN:"));
	    editLivreDialog.add(isbnField);
	    editLivreDialog.add(new JLabel("Description:"));
	    editLivreDialog.add(descriptionField);
	    editLivreDialog.add(new JLabel("Éditeur:"));
	    editLivreDialog.add(editeurField);
	    editLivreDialog.add(new JLabel("Total Exemplaires:"));
	    editLivreDialog.add(totalExemplairesField);
	    editLivreDialog.add(new JLabel("Image:"));
	    editLivreDialog.add(imageField);
	
	    // Bouton pour soumettre le formulaire
	    JButton submitButton = new JButton("Modifier");
	    editLivreDialog.add(submitButton);
	
	     
	    editLivreDialog.setVisible(true); // Afficher le dialogue
	}
	 
	
	// Getters pour récupérer les valeurs des champs
	
	public JTextField getGenreField() {
	    return genreField; // Assurez-vous d'avoir un champ pour le genre
	}

	public JTextField getImageField() {
	    return imageField; // Champ pour l'URL de l'image
	}
	    public String getTitreFieldValue() {
	        return titreField.getText();
	    }

	    public String getAuteurFieldValue() {
	        return auteurField.getText();
	    }

	    public int getAnneeFieldValue() {
	        return Integer.parseInt(anneeField.getText());
	    }

	    public String getIsbnFieldValue() {
	        return isbnField.getText();
	    }

	    public String getDescriptionFieldValue() {
	        return descriptionField.getText();
	    }

	    public String getEditeurFieldValue() {
	        return editeurField.getText();
	    }

	    public int getTotalExemplairesFieldValue() {
	        return Integer.parseInt(totalExemplairesField.getText());
	    }

	    public String getImageFieldValue() {
	        return imageField.getText();
	    }
	
	
    private ImageIcon createIcon(String path) {
        return new ImageIcon(path); // Crée l'icône à partir du chemin de l'image
    }
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        // Obtenir l'image à partir de l'icône
        Image img = icon.getImage();
        
        // Calculer le rapport d'aspect
        double aspectRatio = (double) icon.getIconWidth() / (double) icon.getIconHeight();
        
        // Calculer la nouvelle largeur et hauteur en fonction du rapport d'aspect
        if (width / aspectRatio <= height) {
            height = (int) (width / aspectRatio);
        } else {
            width = (int) (height * aspectRatio);
        }
        
        // Redimensionner l'image
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        
        // Retourner une nouvelle ImageIcon avec l'image redimensionnée
        return new ImageIcon(resizedImage);
    }

    public JTextField getSearchField() {
        return searchField;
    }
    public JTextField getUserIdField() {
        return userIdField;
    }

    public JComboBox<String> getCriteriaComboBox() {
        return criteriaComboBox;
    }

    public JCheckBox getDisponibleCheckBox() {
        return disponibleCheckBox;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getAddButton() {
        return addButton;
    }
    public JButton getEditButton() {
        return editButton;
    }
    public JPanel getPopularPanel() {
        return popularPanel;
    }

    public JPanel getDetailsPanel() {
        return detailsPanel;
    }

    public JButton getBorrowButton() {
        return borrowButton;
    }

    public Livre getSelectedLivre() {
        return selectedLivre;
    }
    public JLabel getDeleteLabel() {
        // Créez un JLabel pour le bouton "Supprimer" et retournez-le
        return new JLabel(resizeIcon(createIcon("src/resources/delete-icon.png"), 20, 20));
    }

    public JLabel getEditLabel() {
        // Créez un JLabel pour le bouton "Modifier" et retournez-le
        return new JLabel(resizeIcon(createIcon("src/resources/edit-icon.png"), 20, 20));
    }
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

	public JButton getDeleteButton() {
		return deleteButton;
	}
    public void hideAddLivreForm() {
        System.out.println("Formulaire d'ajout masqué.");
    }
 

    public void clearAddLivreForm() {
        System.out.println("Formulaire d'ajout réinitialisé.");
    }
 

    public void hideEditLivreForm() {
        System.out.println("Formulaire d'édition masqué.");
    }

    public JTextField getTitreField() {
        return titreField;
    }

    public JTextField getAuteurField() {
        return auteurField;
    }

    public JTextField getAnneeField() {
        return anneeField;
    }

    public JTextField getIsbnField() {
        return isbnField;
    }

    public JTextField getDescriptionField() {
        return descriptionField;
    }

    public JTextField getEditeurField() {
        return editeurField;
    }

    public JTextField getTotalExemplairesField() {
        return totalExemplairesField;
    }

   
    public void clearEditLivreForm() {
        System.out.println("Formulaire d'édition réinitialisé.");
    }

 
}
