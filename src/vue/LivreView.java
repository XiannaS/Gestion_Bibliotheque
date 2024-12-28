package vue;

import javax.swing.*;
import controllers.LivreController;
import model.Livre;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LivreView extends JPanel {
    private JTextField searchField;
    private JComboBox<String> criteriaComboBox;
    private JCheckBox disponibleCheckBox;
    private JButton searchButton, addButton;
    private JPanel popularPanel;
    private JPanel detailsPanel;
    private JButton borrowButton;  // Bouton Emprunter
    private JTextField userIdField;
    private Livre selectedLivre;   // Livre sélectionné pour les détails
    private LivreController livreController; // Référence au contrôleur

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

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        criteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Année", "ISBN"});
        disponibleCheckBox = new JCheckBox("Disponible");
        searchButton = new JButton("Rechercher");
        addButton = new JButton();

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
        popularPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        JScrollPane bookScrollPane = new JScrollPane(popularPanel);
        add(bookScrollPane, BorderLayout.CENTER);

        // Initialiser le bouton "Emprunter"
        borrowButton = new JButton("Emprunter");
        borrowButton.setEnabled(false); // Désactiver le bouton au début
        borrowButton.setVisible(false); // Masquer le bouton au début
        detailsPanel.add(borrowButton); // Ajouter le bouton au panneau des détails

     // Initialiser le champ pour l'ID utilisateur
        userIdField = new JTextField();
        userIdField.setColumns(5); // Limiter à 5 caractères visibles
        userIdField.setMargin(new Insets(0, 0, 0, 0)); // Supprimer les marges internes
        userIdField.setPreferredSize(new Dimension(100, 20)); // Taille préférée compacte
        userIdField.setMaximumSize(new Dimension(100, 20)); // Taille maximale
        userIdField.setMinimumSize(new Dimension(50, 20)); // Taille minimale
        userIdField.setVisible(false); // Le cacher au début
        detailsPanel.add(userIdField); // Ajouter le champ au panneau des détails

  
    }

    
    // Méthode pour afficher la liste des livres
    public void displayBooks(List<Livre> livres) {
        popularPanel.removeAll(); // Vider le panneau avant de redessiner
        for (Livre livre : livres) {
            JPanel livrePanel = createLivrePanel(livre);
            popularPanel.add(livrePanel);

            // Ajouter un écouteur de souris pour sélectionner le livre
            livrePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedLivre = livre; // Mettre à jour le livre sélectionné
                    displayLivreDetails(livre); // Afficher les détails du livre
                    borrowButton.setEnabled(true); // Activer le bouton Emprunter
                    userIdField.setVisible(true); // Afficher le champ ID utilisateur
                }
            });
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
        imageIcon = resizeIcon(imageIcon, 150, 200); // Redimensionner à 80x120 pixels
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ajouter le titre du livre en italique
        JLabel titleLabel = new JLabel(livre.getTitre());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.ITALIC)); // Titre en italique
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        livrePanel.add(imageLabel);
        livrePanel.add(titleLabel);

        // Panneau pour les icônes "Supprimer" et "Modifier"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Disposition centrée
        buttonPanel.setBackground(new Color(0, 0, 0, 0)); // Fond transparent pour le panneau des boutons

        // Créer les icônes "Supprimer" et "Modifier" avec des JLabel
        JLabel deleteLabel = new JLabel(resizeIcon(createIcon("src/resources/delete-icon.png"), 20, 20)); // Redimensionner à 30x30 pixels
        deleteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Changer le curseur pour indiquer que c'est cliquable

        JLabel editLabel = new JLabel(resizeIcon(createIcon("src/resources/edit-icon.png"), 20, 20)); // Redimensionner à 30x30 pixels
        editLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Changer le curseur pour indiquer que c'est cliquable

        // Ajouter les icônes au panneau
        buttonPanel.add(deleteLabel);
        buttonPanel.add(editLabel);

        // Ajouter le panneau des boutons au panneau du livre
        livrePanel.add(buttonPanel);

        // Ajouter un écouteur de souris pour sélectionner le livre
        livrePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedLivre = livre; // Mettre à jour le livre sélectionné
                displayLivreDetails(livre); // Afficher les détails du livre
                detailsPanel.setVisible(true); // Rendre le panneau des détails visible
                borrowButton.setEnabled(true); // Activer le bouton Emprunter
                userIdField.setVisible(true); // Afficher le champ ID utilisateur
            }
        });

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
    detailsPanel.add(new JLabel("Exemplaires restants: " + livre.getTotalExemplaires()));

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

    detailsPanel.revalidate();
    detailsPanel.repaint();
}

	
	// Méthode pour afficher le formulaire d'ajout d'un livre
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
        JButton chooseImageButton = new JButton("Choisir une image");

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
        addLivreDialog.add(imageField);
        addLivreDialog.add(chooseImageButton);

        // Bouton pour soumettre le formulaire
        JButton submitButton = new JButton("Ajouter");
        addLivreDialog.add(submitButton);

        addLivreDialog.setVisible(true); // Afficher le dialogue
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

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
