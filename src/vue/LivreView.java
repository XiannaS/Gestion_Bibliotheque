package vue;

import javax.swing.*;
import controllers.EmpruntController; // Importer EmpruntController
import controllers.LivreController;
import controllers.UserController; // Importer UserController
import exception.LivreException;
import model.Livre;
import model.User;

import java.awt.*;
import java.awt.event.ActionListener;
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
    
    // Le constructeur prend maintenant les contrôleurs existants comme paramètres
    public LivreView(LivreController livreController, EmpruntController empruntController, UserController userController, EmpruntView empruntView) {
        this.livreController = livreController;
        this.empruntController = empruntController;
        this.userController = userController;
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

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        criteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Année", "ISBN"});
        disponibleCheckBox = new JCheckBox("Disponible");

        JButton searchButton = createButton("Rechercher", e -> searchLivres());
        JButton addButton = createButtonWithIcon("Ajouter un Livre", e -> showAddLivreForm());

        searchPanel.add(new JLabel("Rechercher : "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Critère : "));
        searchPanel.add(criteriaComboBox);
        searchPanel.add(disponibleCheckBox);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);

        return searchPanel;
    }
 
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    private JButton createButtonWithIcon(String tooltip, ActionListener action) {
        ImageIcon addIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/add-icon.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton button = new JButton(addIcon);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(30, 30));
        button.addActionListener(action);
        return button;
    }
 
    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails du Livre"));
        return detailsPanel;
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
	    JPanel panel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(5, 5, 5, 5);
	
	    ImageIcon imageIcon = new ImageIcon(livre.getImageUrl());
	    if (imageIcon.getIconWidth() == -1) {
	        imageIcon = new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/default-book.jpeg");
	    }
	    imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH));
	
	    JLabel imageLabel = new JLabel(imageIcon);
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 2;
	    panel.add(imageLabel, gbc);
	
	    JLabel titleLabel = new JLabel(livre.getTitre());
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.gridwidth = 2;
	    panel.add(titleLabel, gbc);
	
	    JLabel authorLabel = new JLabel("Auteur : " + livre.getAuteur());
	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.gridwidth = 2;
	    panel.add(authorLabel, gbc);
	
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	
	    // Icône pour modifier un livre
	    ImageIcon editIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/edit-icon.png")
	            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	    JButton editButton = new JButton(editIcon);
	    editButton.setToolTipText("Modifier");
	    editButton.setPreferredSize(new Dimension(30, 30));
	    editButton.addActionListener(e -> updateLivreForm(livre));
	
	    // Icône pour supprimer un livre
	    ImageIcon deleteIcon = new ImageIcon(new ImageIcon("C:/Eclipse/gestionbibli/src/main/resources/ressources/delete-icon.png")
	            .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
	    JButton deleteButton = new JButton(deleteIcon);
	    deleteButton.setToolTipText("Supprimer");
	    deleteButton.setPreferredSize(new Dimension(30, 30));
	    deleteButton.addActionListener(e -> {
	        int confirmation = JOptionPane.showConfirmDialog(panel, "Êtes-vous sûr de vouloir supprimer ce livre ?", 
	                "Confirmation de suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	        if (confirmation == JOptionPane.YES_OPTION) {
	            try {
	                livreController.deleteLivre(livre.getId());
	                displayAllLivres(); // Mise à jour de la vue après suppression
	                JOptionPane.showMessageDialog(panel, "Livre supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
	            } catch (LivreException ex) {
	                JOptionPane.showMessageDialog(panel, "Erreur lors de la suppression du livre : " + ex.getMessage(), 
	                        "Erreur", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    });
	
	    buttonPanel.add(editButton);
	    buttonPanel.add(deleteButton);
	
	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.gridwidth = 2;
	    panel.add(buttonPanel, gbc);
	
	    imageLabel.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            showLivreDetails(livre);
	        }
	    });
	
	    return panel;
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
	    
	    // Initialiser imageUrlField ici
	    imageUrlField = new JTextField(); // Assurez-vous de l'initialiser
	    imageUrlField.setEditable(false); // Rendre le champ non modifiable
	    isbnField = new JTextField();
	    descriptionField = new JTextField();
	    editeurField = new JTextField();
	    exemplairesField = new JTextField();

	 
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
	    addDialog.add(new JLabel("ISBN :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(isbnField, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 7;
	    addDialog.add(new JLabel("Description :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(descriptionField, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 8;
	    addDialog.add(new JLabel("Éditeur :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(editeurField, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 9;
	    addDialog.add(new JLabel("Nombre d'exemplaires :"), gbc);
	    gbc.gridx = 1;
	    addDialog.add(exemplairesField, gbc);
	
	    JButton addButton = new JButton("Ajouter");
	    addButton.addActionListener(e -> addLivre(addDialog ));
	    gbc.gridx = 0;
	    gbc.gridy = 10;
	    gbc.gridwidth = 2;
	    addDialog.add(addButton, gbc);
	
	    addDialog.setSize(400, 500);
	    addDialog.setLocationRelativeTo(this);
	    addDialog.setVisible(true);
	}
	

	private void addLivre(JDialog dialog) {
	    // Récupérer les valeurs des champs
	    String titre = titreField.getText();
	    String auteur = auteurField.getText();
	    int annee;
	    try {
	        annee = Integer.parseInt(anneeField.getText());
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(dialog, "Veuillez entrer une année valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    String genre = (String) genreComboBox.getSelectedItem();
	    String imageUrl = imageUrlField.getText();
	    String isbn = isbnField.getText();
	    String description = descriptionField.getText();
	    String editeur = editeurField.getText();
	    int totalExemplaires;
	    try {
	        totalExemplaires = Integer.parseInt(exemplairesField.getText());
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(dialog, "Veuillez entrer un nombre valide d'exemplaires.", "Erreur", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    // Créer un nouvel objet Livre
	    Livre livre = new Livre(livreController.getNextLivreId(), titre, auteur, genre, annee, imageUrl, isbn, description, editeur, totalExemplaires);

	    // Logique pour ajouter le livre via le contrôleur
	    try {
	        livreController.addLivre(livre);
	     // Après l'ajout réussi
	        displayAllLivres(); // Recharger tous les livres dans la vue// Appel à la méthode addLivre du contrôleur
	        JOptionPane.showMessageDialog(dialog, "Livre ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
	        dialog.dispose(); // Fermer le dialogue après l'ajout
	    } catch (LivreException e) {
	        JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout du livre : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(dialog, "Une erreur s'est produite lors de l'ajout du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
    private void showLivreDetails(Livre livre) {
        detailsPanel.removeAll(); // Effacer les détails précédents
        detailsPanel.setLayout(new GridBagLayout()); // Utiliser GridBagLayout pour un meilleur contrôle
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Espacement autour des composants

        // Ajouter les détails du livre
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Titre : " + livre.getTitre()), gbc);

        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Auteur : " + livre.getAuteur()), gbc);

        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Année de publication : " + livre.getAnneePublication()), gbc);

        gbc.gridy = 3;
        detailsPanel.add(new JLabel("Genre : " + livre.getGenre()), gbc);

        gbc.gridy = 4;
        detailsPanel.add(new JLabel("URL de l'image : " + livre.getImageUrl()), gbc);

        gbc.gridy = 5;
        detailsPanel.add(new JLabel("Total d'exemplaires : " + livre.getTotalExemplaires()), gbc);

        gbc.gridy = 6;
        detailsPanel.add(new JLabel("Exemplaires disponibles : " + livre.getExemplairesDisponibles()), gbc);

        gbc.gridy = 7;
        detailsPanel.add(new JLabel("ISBN : " + livre.getIsbn()), gbc);

        gbc.gridy = 8;
        detailsPanel.add(new JLabel("Description : " + livre.getDescription()), gbc);

        gbc.gridy = 9;
        detailsPanel.add(new JLabel("Éditeur : " + livre.getEditeur()), gbc);

        // Créer un panneau pour l'ID utilisateur
        JPanel userPanel = new JPanel();
        JTextField userIdField = new JTextField(10); // Champ pour l'ID utilisateur, largeur réduite
        userPanel.add(new JLabel("ID Utilisateur :"));
        userPanel.add(userIdField);

        gbc.gridy = 10;
        detailsPanel.add(userPanel, gbc);

        // Bouton pour emprunter le livre
        JButton borrowButton = new JButton("Emprunter");
        borrowButton.addActionListener(e -> borrowLivre(livre, userIdField));
        gbc.gridy = 11;
        detailsPanel.add(borrowButton, gbc);

        // Définir une taille maximale pour le panneau de détails
        detailsPanel.setPreferredSize(new Dimension(300, 400)); // Ajustez la taille selon vos besoins
        detailsPanel.revalidate(); // Revalider le panneau pour afficher les nouveaux détails
        detailsPanel.repaint(); // Repeindre le panneau
    }
   
    private void borrowLivre(Livre livre, JTextField userIdField) {
        String userId = userIdField.getText();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer votre ID utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Utiliser la méthode générique pour obtenir l'utilisateur
        User user = empruntController.getEntityById(userId, "User");
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Utilisateur introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Emprunter le livre
        try {
            empruntController.emprunterLivre(livre, user);
           
            // Mettre à jour la vue des emprunts
            if (empruntView != null) {
                empruntView.chargerEmprunts("Tous"); // Recharger tous les emprunts
            } else {
                System.out.println("Erreur : empruntView est null.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'emprunt du livre : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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
	
	 // Bouton pour choisir une nouvelle image
	    JButton chooseImageButton = new JButton("Choisir image du livre");
	    chooseImageButton.addActionListener(e -> {
	        JFileChooser fileChooser = new JFileChooser();
	        int returnValue = fileChooser.showOpenDialog(updateDialog); // Change addDialog to updateDialog
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
	            imageUrlField.setText(selectedFilePath); // Set the URL in the text field
	        }
	    });
	    
	    gbc.gridx = 1;
	    gbc.gridy = 5;
	    updateDialog.add(chooseImageButton, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 6;
	    updateDialog.add(new JLabel("ISBN :"), gbc);
	    gbc.gridx = 1;
	    updateDialog.add(isbnField, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 7;
	    updateDialog.add(new JLabel("Description :"), gbc);
	    gbc.gridx = 1;
	    updateDialog.add(descriptionField, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 8;
	    updateDialog.add(new JLabel("Éditeur :"), gbc);
	    gbc.gridx = 1;
	    updateDialog.add(editeurField, gbc);
	
	    gbc.gridx = 0;
	    gbc.gridy = 9;
	    updateDialog.add(new JLabel("Nombre d'exemplaires :"), gbc);
	    gbc.gridx = 1;
	    updateDialog.add(exemplairesField, gbc);
	
	    JButton updateButton = new JButton("Mettre à jour");
	    updateButton.addActionListener(e -> {
	        try {
	            // Vérification de l'URL de l'image
	            if (!livreController.isValidImageUrl(imageUrlField.getText())) {
	                throw new IllegalArgumentException("L'URL de l'image n'est pas valide.");
	            }
	            updateLivre(livre, updateDialog); // Mettre à jour le livre
	        } catch (Exception ex) {
	            livreController.showMessage(ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	        }
	    });
	    gbc.gridx = 0;
	    gbc.gridy = 10;
	    gbc.gridwidth = 2;
	    updateDialog.add(updateButton, gbc);
	
	    updateDialog.setSize(400, 500);
	    updateDialog.setLocationRelativeTo(this);
	    updateDialog.setVisible(true);
	}

	private void updateLivre(Livre livre, JDialog dialog) {
	    try {
	        // Récupération des entrées dans la vue
	        String titre = titreField.getText();
	        String auteur = auteurField.getText();
	        int annee = Integer.parseInt(anneeField.getText());
	        String genre = (String) genreComboBox.getSelectedItem();
	        String imageUrl = imageUrlField.getText();
	        String isbn = isbnField.getText();
	        String description = descriptionField.getText();
	        String editeur = editeurField.getText();
	        int totalExemplaires = Integer.parseInt(exemplairesField.getText());

	        // Demander au contrôleur de valider et mettre à jour le livre
	        livreController.updateLivre(livre, titre, auteur, annee, genre, imageUrl, isbn, description, editeur, totalExemplaires);

	        // Fermeture du dialogue de mise à jour
	        dialog.dispose();

	        // Mise à jour de l'affichage avec les nouveaux détails
	        showLivreDetails(livre);

	    } catch (NumberFormatException e) {
	        livreController.showMessage("Veuillez entrer des valeurs valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
	    }
	}

	 

 
}