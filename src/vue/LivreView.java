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
    private Livre selectedLivre;   // Livre sélectionné pour les détails
    private LivreController livreController; // Référence au contrôleur

    public LivreView() {
        initUI(); // Initialiser l'interface utilisateur
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        criteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Année", "ISBN"});
        disponibleCheckBox = new JCheckBox("Disponible");
        searchButton = new JButton("Rechercher");
        addButton = createButtonWithIcon("Ajouter un Livre", "src/resources/add-icon.png");

        searchPanel.add(new JLabel("Rechercher : "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Critère : "));
        searchPanel.add(criteriaComboBox);
        searchPanel.add(disponibleCheckBox);
        searchPanel.add(searchButton);
        searchPanel.add(addButton);

        add(searchPanel, BorderLayout.NORTH);

        // Affichage des livres
        popularPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        JScrollPane bookScrollPane = new JScrollPane(popularPanel);
        add(bookScrollPane, BorderLayout.CENTER);

        // Détails du livre
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails du Livre"));
        add(detailsPanel, BorderLayout.EAST);

        // Bouton Emprunter
        borrowButton = new JButton("Emprunter");
        borrowButton.setEnabled(false);  // Le bouton est désactivé au début
        add(borrowButton, BorderLayout.SOUTH);
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
                }
            });
        }
        popularPanel.revalidate(); // Revalider le panneau
        popularPanel.repaint(); // Repeindre le panneau
    }
    // Créer un panneau individuel pour un livre
  
    
	private JPanel createLivrePanel(Livre livre) {
	    JPanel livrePanel = new JPanel();
	    livrePanel.setLayout(new BoxLayout(livrePanel, BoxLayout.Y_AXIS));
	
	    // Créer l'image de couverture
	    ImageIcon imageIcon;
	    try {
	        imageIcon = new ImageIcon(livre.getImageUrl()); // Assurez-vous que l'URL de l'image est correcte
	        if (imageIcon.getIconWidth() == -1) {
	            // Si l'image n'est pas trouvée, utiliser une image par défaut
	            imageIcon = new ImageIcon("src/resources/default-book.jpeg");
	        }
	    } catch (Exception e) {
	        imageIcon = new ImageIcon("src/resources/default-book.jpeg"); // Image par défaut
	    }
	
	    JLabel imageLabel = new JLabel(imageIcon);
	    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer l'image
	
	    // Créer les autres informations du livre
	    JLabel titreLabel = new JLabel("Titre: " + livre.getTitre());
	    JLabel auteurLabel = new JLabel("Auteur: " + livre.getAuteur());
	    JLabel anneeLabel = new JLabel("Année: " + livre.getAnneePublication());
	
	    // Ajouter tout cela dans le panneau
	    livrePanel.add(imageLabel);
	    livrePanel.add(titreLabel);
	    livrePanel.add(auteurLabel);
	    livrePanel.add(anneeLabel);
	
	    livrePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Ajouter une bordure autour de chaque livre
	
	    return livrePanel;
	}
	    
    
    // Méthode pour afficher les détails du livre sélectionné
    public void displayLivreDetails(Livre livre) {
        this.selectedLivre = livre;  // Sauvegarder le livre sélectionné
        detailsPanel.removeAll();

        detailsPanel.add(new JLabel("Titre: " + livre.getTitre()));
        detailsPanel.add(new JLabel("Auteur: " + livre.getAuteur()));
        detailsPanel.add(new JLabel("Année: " + livre.getAnneePublication()));
        detailsPanel.add(new JLabel("ISBN: " + livre.getIsbn()));
        detailsPanel.add(new JLabel("Description: " + livre.getDescription()));
        detailsPanel.add(new JLabel("Éditeur: " + livre.getEditeur()));
        detailsPanel.add(new JLabel("Exemplaires restants: " + livre.getTotalExemplaires()));

        // Mettre à jour le bouton Emprunter pour qu'il soit activé lorsque les détails du livre sont affichés
        borrowButton.setEnabled(true); // Activer le bouton Emprunter

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    public void showAddLivreForm(LivreController controller) {
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
        
        // Genre : ComboBox pour les genres
        JComboBox<String> genreComboBox = new JComboBox<>(new String[]{"Science", "Histoire", "Littérature", "Arts", "Autre"});

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
        addLivreDialog.add(new JLabel("Genre:"));
        addLivreDialog.add(genreComboBox);

        // Bouton pour soumettre le formulaire
        JButton submitButton = new JButton("Ajouter");
        submitButton.addActionListener(e -> {
            try {
                // Créer un nouvel objet Livre
                Livre newLivre = new Livre(
                        0, // ID généré automatiquement dans le DAO
                        titreField.getText(),
                        auteurField.getText(),
                        (String) genreComboBox.getSelectedItem(), // Genre sélectionné
                        Integer.parseInt(anneeField.getText()),
                        "src/resources/default_image.png", // Chemin par défaut pour l'image
                        isbnField.getText(),
                        descriptionField.getText(),
                        editeurField.getText(),
                        Integer.parseInt(totalExemplairesField.getText())
                );
                controller.addLivre(newLivre); // Appeler la méthode d'ajout dans le contrôleur
                addLivreDialog.dispose(); // Fermer le dialog après l'ajout
            } catch (Exception ex) {
                showMessage("Erreur lors de l'ajout du livre: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        addLivreDialog.add(submitButton);

        addLivreDialog.setVisible(true); // Afficher le dialog
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
        return borrowButton; // Assurez-vous que borrowButton est bien initialisé
    }

    public Livre getSelectedLivre() {
        return selectedLivre; // Retourner le livre sélectionné
    }

    private JButton createButtonWithIcon(String text, String iconPath) {
        JButton button = new JButton(text);
        ImageIcon icon = new ImageIcon(iconPath); // Charge l'icône à partir du chemin spécifié

        // Redimensionner l'icône
        Image img = icon.getImage(); // Obtenir l'image de l'icône
        Image newImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Redimensionner l'image
        button.setIcon(new ImageIcon(newImg)); // Définir l'icône redimensionnée

        return button;
    }
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}