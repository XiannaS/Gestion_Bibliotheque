package vue;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.Livre;

import java.awt.*;
import java.util.List;

public class LivreView extends JPanel {
    private JTable livresTable;
    private DefaultTableModel tableModel;
    private JButton ajouterButton;
    private JButton modifierButton;
    private JButton supprimerButton;
    private JButton emprunterButton;
    private JTextField titreField;
    private JTextField auteurField;
    private JTextField genreField;
    private JTextField anneePublicationField;
    private JTextField isbnField;
    private JTextField descriptionField;
    private JTextField editeurField;
    private JTextField exemplairesField;

    public LivreView() {
        setLayout(new BorderLayout());

        // Table des livres
     // Modifie la ligne qui initialise ta table pour ajouter une colonne "Exemplaires Restants"
        tableModel = new DefaultTableModel(new String[]{"ID", "Titre", "Auteur", "Genre", "Année", "ISBN", "Description", "Éditeur", "Exemplaires", "Exemplaires Restants"}, 0);
        livresTable = new JTable(tableModel);
        livresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        livresTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        livresTable.setRowHeight(30);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Liste des Livres"));
        tableCard.setBackground(null); // Pas de fond
        tableCard.add(new JScrollPane(livresTable), BorderLayout.CENTER);

        // Panneau des détails du livre
        JPanel detailsCard = new JPanel(new GridBagLayout());
        detailsCard.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Détails du Livre"));
        detailsCard.setBackground(null); // Pas de fond

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsCard.add(new JLabel("Titre :"), gbc);

        gbc.gridx = 1;
        titreField = new JTextField(20); // Taille fixe
        detailsCard.add(titreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsCard.add(new JLabel("Auteur :"), gbc);

        gbc.gridx = 1;
        auteurField = new JTextField(20); // Taille fixe
        detailsCard.add(auteurField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsCard.add(new JLabel("Genre :"), gbc);

        gbc.gridx = 1;
        genreField = new JTextField(20); // Taille fixe
        detailsCard.add(genreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsCard.add(new JLabel("Année de publication :"), gbc);

        gbc.gridx = 1;
        anneePublicationField = new JTextField(20); // Taille fixe
        detailsCard.add(anneePublicationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsCard.add(new JLabel("ISBN :"), gbc);

        gbc.gridx = 1;
        isbnField = new JTextField(20); // Taille fixe
        detailsCard.add(isbnField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        detailsCard.add(new JLabel("Description :"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(20); // Taille fixe
        detailsCard.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        detailsCard.add(new JLabel("Éditeur :"), gbc);

        gbc.gridx = 1;
        editeurField = new JTextField(20); // Taille fixe
        detailsCard.add(editeurField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        detailsCard.add(new JLabel("Exemplaires :"), gbc);

        gbc.gridx = 1;
        exemplairesField = new JTextField(20); // Taille fixe
        detailsCard.add(exemplairesField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Centré avec espacement vertical réduit
        buttonPanel.setBackground(null); // Pas de fond
        ajouterButton = new JButton("Ajouter Livre");
        modifierButton = new JButton("Modifier Livre");
        supprimerButton = new JButton("Supprimer Livre");
        emprunterButton = new JButton("Emprunter Livre");

        ajouterButton.setFocusPainted(false);
        modifierButton.setFocusPainted(false);
        supprimerButton.setFocusPainted(false);
        emprunterButton.setFocusPainted(false);

        ajouterButton.setBackground(new Color(60, 179, 113)); // Couleur personnalisée
        modifierButton.setBackground(new Color(255, 165, 0)); // Couleur personnalisée
        supprimerButton.setBackground(new Color(255, 69, 58)); // Couleur personnalisée
        emprunterButton.setBackground(new Color(100, 149, 237)); // Couleur personnalisée

        buttonPanel.add(ajouterButton);
        buttonPanel.add(modifierButton);
        buttonPanel.add(supprimerButton);
        buttonPanel.add(emprunterButton);

        JPanel detailsContainer = new JPanel(new BorderLayout());
        detailsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsContainer.setBackground(null); // Pas de fond
        detailsContainer.add(detailsCard, BorderLayout.CENTER);
        detailsContainer.add(buttonPanel, BorderLayout.NORTH); // Les boutons en haut

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marge autour des cartes
        centerPanel.setBackground(null); // Pas de fond

        // Réduire la taille des cartes
        JPanel detailsCardWrapper = new JPanel(new BorderLayout());
        detailsCardWrapper.add(detailsContainer, BorderLayout.CENTER);
        detailsCardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsCardWrapper.setPreferredSize(new Dimension(300, 300)); // Taille réduite

        JPanel tableCardWrapper = new JPanel(new BorderLayout());
        tableCardWrapper.add(tableCard, BorderLayout.CENTER);
        tableCardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableCardWrapper.setPreferredSize(new Dimension(300, 300)); // Taille réduite

        centerPanel.add(detailsCardWrapper);
        centerPanel.add(tableCardWrapper);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateLivresTable(List<Livre> livres) {
        tableModel.setRowCount(0); // Efface toutes les lignes existantes
        for (Livre livre : livres) {
            // Ajoute le nombre d'exemplaires restants (exemplaires disponibles)
            Object[] row = {
                livre.getId(),
                livre.getTitre(),
                livre.getAuteur(),
                livre.getGenre(),
                livre.getAnneePublication(),
                livre.getIsbn(),
                livre.getDescription(),
                livre.getEditeur(),
                livre.getTotalExemplaires(),
                livre.getExemplairesDisponibles() // Utilise la méthode qui retourne le nombre d'exemplaires disponibles
            };
            tableModel.addRow(row);
        }
    }

    public JTable getLivresTable() {
        return livresTable;
    }

    public JButton getAjouterButton() {
        return ajouterButton;
    }

    public JButton getModifierButton() {
        return modifierButton;
    }

    public JButton getSupprimerButton() {
        return supprimerButton;
    }

    public JButton getEmprunterButton() {
        return emprunterButton;
    }

    public String getTitre() {
        return titreField.getText();
    }

    public String getAuteur() {
        return auteurField.getText();
    }

    public String getGenre() {
        return genreField.getText();
    }

    public int getAnneePublication() {
        return Integer.parseInt(anneePublicationField.getText());
    }

    public String getIsbn() {
        return isbnField.getText();
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public String getEditeur() {
        return editeurField.getText();
    }

    public int getExemplaires() {
        return Integer.parseInt(exemplairesField.getText());
    }

    public void setDetails(Livre livre) {
        titreField.setText(livre.getTitre());
        auteurField.setText(livre.getAuteur());
        genreField.setText(livre.getGenre());
        anneePublicationField.setText(String.valueOf(livre.getAnneePublication()));
        isbnField.setText(livre.getIsbn());
        descriptionField.setText(livre.getDescription());
        editeurField.setText(livre.getEditeur());
        exemplairesField.setText(String.valueOf(livre.getTotalExemplaires()));
    }

    public void clearFields() {
        titreField.setText("");
        auteurField.setText("");
        genreField.setText("");
        anneePublicationField.setText("");
        isbnField.setText("");
        descriptionField.setText("");
        editeurField.setText("");
        exemplairesField.setText("");
    }
}
