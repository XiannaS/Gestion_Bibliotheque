package vue;

import javax.swing.*;
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
        tableModel = new DefaultTableModel(new String[]{"ID", "Titre", "Auteur", "Genre", "Année", "ISBN", "Description", "Éditeur", "Exemplaires"}, 0);
        livresTable = new JTable(tableModel);
        livresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        livresTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        livresTable.setRowHeight(30);
        add(new JScrollPane(livresTable), BorderLayout.CENTER);

        // Panneau des détails du livre
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(9, 2, 10, 10));

        detailsPanel.add(new JLabel("Titre :"));
        titreField = new JTextField();
        detailsPanel.add(titreField);

        detailsPanel.add(new JLabel("Auteur :"));
        auteurField = new JTextField();
        detailsPanel.add(auteurField);

        detailsPanel.add(new JLabel("Genre :"));
        genreField = new JTextField();
        detailsPanel.add(genreField);

        detailsPanel.add(new JLabel("Année de publication :"));
        anneePublicationField = new JTextField();
        detailsPanel.add(anneePublicationField);

        detailsPanel.add(new JLabel("ISBN :"));
        isbnField = new JTextField();
        detailsPanel.add(isbnField);

        detailsPanel.add(new JLabel("Description :"));
        descriptionField = new JTextField();
        detailsPanel.add(descriptionField);

        detailsPanel.add(new JLabel("Éditeur :"));
        editeurField = new JTextField();
        detailsPanel.add(editeurField);

        detailsPanel.add(new JLabel("Exemplaires :"));
        exemplairesField = new JTextField();
        detailsPanel.add(exemplairesField);

        add(detailsPanel, BorderLayout.EAST);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));

        ajouterButton = new JButton("Ajouter Livre");
        modifierButton = new JButton("Modifier Livre");
        supprimerButton = new JButton("Supprimer Livre");
        emprunterButton = new JButton("Emprunter Livre");

        buttonPanel.add(ajouterButton);
        buttonPanel.add(modifierButton);
        buttonPanel.add(supprimerButton);
        buttonPanel.add(emprunterButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateLivresTable(List<Livre> livres) {
        tableModel.setRowCount(0); // Efface toutes les lignes existantes
        for (Livre livre : livres) {
            Object[] row = {
                livre.getId(),
                livre.getTitre(),
                livre.getAuteur(),
                livre.getGenre(),
                livre.getAnneePublication(),
                livre.getIsbn(),
                livre.getDescription(),
                livre.getEditeur(),
                livre.getTotalExemplaires()
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