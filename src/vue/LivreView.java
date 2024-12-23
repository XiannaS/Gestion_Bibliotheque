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
    private JTextField searchField, titreField, auteurField, anneeField, imageUrlField;
    private JCheckBox disponibleCheckBox;
    private JComboBox<String> criteriaComboBox, genreComboBox;
    private JPanel popularPanel;
    private JPanel detailsPanel; // Panneau pour afficher les détails du livre
    private LivreController livreController;
    private EmpruntController empruntController; // Déclaration de EmpruntController
    private UserController userController; // Déclaration de UserController

    public LivreView(LivreController livreController) {
        this.livreController = livreController;

        // Initialiser les contrôleurs ici
        this.empruntController = new EmpruntController("C:/Eclipse/gestionbibli/src/main/resources/ressources/emprunt.csv", "C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv", "C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv");
        this.userController = new UserController("C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv");

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Panneau de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        criteriaComboBox = new JComboBox<>(new String[]{"Titre", "Auteur", "Année"});
        disponibleCheckBox = new JCheckBox("Disponible");
        JButton searchButton = new JButton("Rechercher");

        searchPanel.add(new JLabel("Rechercher : "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Critère : "));
        searchPanel.add(criteriaComboBox);
        searchPanel.add(disponibleCheckBox);
        searchPanel.add(searchButton);
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

        // Bouton Ajouter Livre
        JButton addButton = new JButton("Ajouter un Livre");
        addButton.addActionListener(e -> showAddLivreForm());
        add(addButton, BorderLayout.SOUTH);

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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Utiliser BoxLayout pour empiler les éléments verticalement
        panel.setPreferredSize(new Dimension(150, 250)); // Ajustez la taille ici

        // Vérifiez si l'URL de l'image est valide
        ImageIcon imageIcon = new ImageIcon(livre.getImageUrl());
        if (imageIcon.getIconWidth() == -1) {
            imageIcon = new ImageIcon("C:\\Eclipse\\gestionbibli\\src\\main\\resources\\ressources\\default-book.jpeg"); // Image par défaut
        }

        // Ajustez l'image pour qu'elle ne soit pas coupée
        imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH));

        JLabel imageLabel = new JLabel(imageIcon);
        JLabel titleLabel = new JLabel(livre.getTitre());
        JLabel authorLabel = new JLabel("Auteur : " + livre.getAuteur());

        // Ajoutez les composants au panneau
        panel.add(imageLabel);
        panel.add(titleLabel);
        panel.add(authorLabel);

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

        detailsPanel.add(new JLabel("Titre : " + livre.getTitre()));
        detailsPanel.add(new JLabel("Auteur : " + livre.getAuteur()));
        detailsPanel.add(new JLabel("Année de publication : " + livre.getAnneePublication()));
        detailsPanel.add(new JLabel("Genre : " + livre.getGenre()));
        detailsPanel.add(new JLabel("URL de l'image : " + livre.getImageUrl()));

        // Champ pour entrer l'ID de l'utilisateur
        JTextField userIdField = new JTextField();
        detailsPanel.add(new JLabel("ID Utilisateur :"));
        detailsPanel.add(userIdField);

        // Bouton pour emprunter le livre
        JButton borrowButton = new JButton("Emprunter");
        borrowButton.addActionListener(e -> {
            String userId = userIdField.getText().trim(); // Trim pour enlever les espaces
            if (!userId.isEmpty()) {
                User user = empruntController.getUserById(userId); // Récupérer l'utilisateur par ID
                if (user != null) {
                    empruntController.emprunterLivre(livre, user); // Appeler la méthode d'emprunt
                    displayAllLivres(); // Mettre à jour l'affichage des livres
                } else {
                    JOptionPane.showMessageDialog(detailsPanel, "Utilisateur non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(detailsPanel, "Veuillez entrer un ID utilisateur valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        detailsPanel.add(borrowButton);

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
        disponibleCheckBox = new JCheckBox("Disponible");
        imageUrlField = new JTextField();

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

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.add(new JLabel("URL de l'image :"));
        imagePanel.add(imageUrlField);

        JButton chooseImageButton = new JButton("Choisir image du livre");
        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(addDialog);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                imageUrlField.setText(selectedFilePath);
            }
        });
        imagePanel.add(chooseImageButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        addDialog.add(imagePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        addDialog.add(new JLabel("Disponible :"), gbc);
        gbc.gridx = 1;
        addDialog.add(disponibleCheckBox, gbc);

        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> addLivre(addDialog));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        addDialog.add(addButton, gbc);

        addDialog.setSize(400, 400);
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
            boolean disponible = disponibleCheckBox.isSelected();

            Livre livre = new Livre(livreController.getNextLivreId(), titre, auteur, genre, annee, imageUrl, disponible);
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
        updateDialog.setLayout(new GridLayout(0, 2, 10, 10));

        titreField = new JTextField(livre.getTitre());
        auteurField = new JTextField(livre.getAuteur());
        anneeField = new JTextField(String.valueOf(livre.getAnneePublication()));
        genreComboBox = new JComboBox<>(new String[]{"Science Fiction", "Histoire", "Roman", "Aventure"});
        genreComboBox.setSelectedItem(livre.getGenre());
        imageUrlField = new JTextField(livre.getImageUrl());
        disponibleCheckBox = new JCheckBox("Disponible", livre.isDisponible());

        updateDialog.add(new JLabel("Titre :"));
        updateDialog.add(titreField);
        updateDialog.add(new JLabel("Auteur :"));
        updateDialog.add(auteurField);
        updateDialog.add(new JLabel("Année de publication :"));
        updateDialog.add(anneeField);
        updateDialog.add(new JLabel("Genre :"));
        updateDialog.add(genreComboBox);
        updateDialog.add(new JLabel("URL de l'image :"));
        updateDialog.add(imageUrlField);
        updateDialog.add(new JLabel("Disponible :"));
        updateDialog.add(disponibleCheckBox);

        JButton updateButton = new JButton("Mettre à jour");
        updateButton.addActionListener(e -> updateLivre(livre, updateDialog));
        updateDialog.add(updateButton);

        updateDialog.setSize(400, 400);
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
            boolean disponible = disponibleCheckBox.isSelected();

            livre.setTitre(titre);
            livre.setAuteur(auteur);
            livre.setAnneePublication(annee);
            livre.setGenre(genre);
            livre.setImageUrl(imageUrl);
            livre.setDisponible(disponible);

            livreController.updateLivre(livre);

            dialog.dispose();
            displayAllLivres();
            livreController.showMessage("Livre mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            livreController.showMessage("Veuillez entrer une année valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            livreController.showMessage("Erreur dans les champs de saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}