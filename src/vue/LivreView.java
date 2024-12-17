package vue;

import javax.swing.*;
import controllers.LivreController;
import model.Livre;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LivreView extends JPanel {
    private JTextField searchField, titreField, auteurField, anneeField, imageUrlField;
    private JCheckBox disponibleCheckBox;
    private JComboBox<String> criteriaComboBox, genreComboBox;
    private JPanel popularPanel;
    private LivreController livreController;

    public LivreView(LivreController livreController) {
        this.livreController = livreController;
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
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            String selectedCriteria = (String) criteriaComboBox.getSelectedItem();
            boolean isAvailable = disponibleCheckBox.isSelected();
            searchLivres(searchTerm, selectedCriteria, isAvailable);
        });

        // Panneau des livres
        popularPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 4 colonnes

        JScrollPane bookScrollPane = new JScrollPane(popularPanel);
        add(bookScrollPane, BorderLayout.CENTER);

        // Bouton Ajouter Livre
        JButton addButton = new JButton("Ajouter un Livre");
        addButton.addActionListener(e -> showAddLivreForm());
        add(addButton, BorderLayout.SOUTH);

        // Affichage de tous les livres au démarrage
        displayAllLivres();
    }

    public void searchLivres(String searchTerm, String criteria, boolean isAvailable) {
        List<Livre> resultats = livreController.searchLivres(searchTerm, criteria, isAvailable);
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(150, 200)); // Ajustez la taille ici
        
        // Vérifiez si l'URL de l'image est valide
        ImageIcon imageIcon = new ImageIcon(livre.getImageUrl());
        if (imageIcon.getIconWidth() == -1) {
            imageIcon = new ImageIcon("C:\\Eclipse\\gestionbibli\\src\\main\\resources\\ressources\\default-book.jpeg"); // Image par défaut
        }
        
        JLabel imageLabel = new JLabel(imageIcon);
        JLabel titleLabel = new JLabel(livre.getTitre());
        JLabel descriptionLabel = new JLabel("Description...");

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(descriptionLabel, BorderLayout.CENTER);

        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showLivreOptions(livre);
            }
        });

        return panel;
    }

    private void showLivreOptions(Livre livre ) {
        int option = JOptionPane.showOptionDialog(this, "Que voulez-vous faire ?", "Options",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"Modifier", "Supprimer"}, null);

        if (option == 0) {
            updateLivreForm(livre);
        } else if (option == 1) {
            int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce livre ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                livreController.deleteLivre(livre.getId());
                displayAllLivres();
            }
        }
    }

    private void showAddLivreForm() {
        JDialog addDialog = new JDialog((Frame) null, "Ajouter un Livre", true);
        addDialog.setLayout(new GridLayout(0, 2, 10, 10));

        titreField = new JTextField();
        auteurField = new JTextField();
        anneeField = new JTextField();
        genreComboBox = new JComboBox<>(new String[]{"Science Fiction", "Histoire", "Roman", "Aventure"});
        imageUrlField = new JTextField();
        disponibleCheckBox = new JCheckBox("Disponible");

        addDialog.add(new JLabel("Titre :"));
        addDialog.add(titreField);
        addDialog.add(new JLabel("Auteur :"));
        addDialog.add(auteurField);
        addDialog.add(new JLabel("Année de publication :"));
        addDialog.add(anneeField);
        addDialog.add(new JLabel("Genre :"));
        addDialog.add(genreComboBox);
        addDialog.add(new JLabel("URL de l'image :"));
        addDialog.add(imageUrlField);
        addDialog.add(new JLabel("Disponible :"));
        addDialog.add(disponibleCheckBox);

        // Bouton pour choisir l'image
        JButton chooseImageButton = new JButton("Choisir une image");
        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(addDialog);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                imageUrlField.setText(selectedFilePath); // Mettre à jour le champ avec le chemin de l'image
            }
        });
        addDialog.add(chooseImageButton);

        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> addLivre(addDialog));
        addDialog.add(addButton);

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

            // Vérifiez si le livre existe déjà
            if (livreController.livreExists(titre, auteur, annee)) {
                JOptionPane.showMessageDialog(this, "Le livre existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return; // Ne pas ajouter le livre
            }

            Livre livre = new Livre(livreController.getNextLivreId(), titre, auteur, genre, annee, imageUrl, disponible);
            livreController.addLivre(livre);

            dialog.dispose();
            displayAllLivres();
            JOptionPane.showMessageDialog(this, "Livre ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une année valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur dans les champs de saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Livre mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une année valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur dans les champs de saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}