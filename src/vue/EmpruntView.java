package vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Emprunt;

import java.awt.*;
import java.util.List;

public class EmpruntView extends JPanel {
    private JTable empruntTable;
    private DefaultTableModel tableModel;
    private JButton emprunterButton;
    private JButton retournerButton;
    private JButton supprimerButton;
    private JButton renouvelerButton;
    private JComboBox<String> triComboBox;
    private JPanel actionPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> searchTypeComboBox;

    public EmpruntView() {
        setLayout(new BorderLayout());

        // Table des emprunts
        tableModel = new DefaultTableModel(new String[]{"ID", "Livre", "Utilisateur", "Date Emprunt", "Date Retour Prévue", "Retour Effective", "Rendu", "Pénalité"}, 0);
        empruntTable = new JTable(tableModel);
        empruntTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        empruntTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        empruntTable.setRowHeight(30);
        add(new JScrollPane(empruntTable), BorderLayout.CENTER);

        // Panneau des actions et autres éléments
        actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());

        // Panneau de recherche
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchButton.setBackground(new Color(60, 179, 113));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        String[] searchTypes = {"ID Emprunt", "ID Livre", "ID Utilisateur", "Date"};
        searchTypeComboBox = new JComboBox<>(searchTypes);

        searchPanel.add(new JLabel("Recherche :"));
        searchPanel.add(searchField);
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchButton);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        retournerButton = new JButton("Retourner Livre");
        supprimerButton = new JButton("Supprimer Emprunt");
        renouvelerButton = new JButton("Prolonger Emprunt");

        styleButton(retournerButton);
        styleButton(supprimerButton);
        styleButton(renouvelerButton);

        buttonPanel.add(retournerButton);
        buttonPanel.add(supprimerButton);
        buttonPanel.add(renouvelerButton);

        // Panneau de tri
        JPanel triPanel = new JPanel();
        triPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        triPanel.setBackground(new Color(245, 245, 245));

        triPanel.add(new JLabel("Trier par :"));
        triComboBox = new JComboBox<>(new String[]{"Tous", "En cours", "Historique", "Par pénalités"});
        triPanel.add(triComboBox);

        // Ajouter tous les panneaux
        actionPanel.add(searchPanel, BorderLayout.NORTH);
        actionPanel.add(buttonPanel, BorderLayout.CENTER);
        actionPanel.add(triPanel, BorderLayout.SOUTH);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }

    public void updateEmpruntsTable(List<Emprunt> emprunts) {
        tableModel.setRowCount(0); // Efface toutes les lignes existantes
        for (Emprunt emprunt : emprunts) {
            Object[] row = {
                emprunt.getId(),
                emprunt.getLivreId(),  // Remplacez par le titre du livre si nécessaire
                emprunt.getUserId(),
                emprunt.getDateEmprunt(),
                emprunt.getDateRetourPrevue(),
                emprunt.getDateRetourEffective(),
                emprunt.isRendu() ? "Oui" : "Non",
                emprunt.getPenalite()
            };
            tableModel.addRow(row);
        }
    }

    public JButton getEmprunterButton() {
        return emprunterButton;
    }

    public JButton getRetournerButton() {
        return retournerButton;
    }

    public JButton getSupprimerButton() {
        return supprimerButton;
    }

    public JButton getRenouvelerButton() {
        return renouvelerButton;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JComboBox<String> getSearchTypeComboBox() {
        return searchTypeComboBox;
    }

    public JComboBox<String> getTriComboBox() {
        return triComboBox;
    }

    public JTable getEmpruntTable() {
        return empruntTable;
    }
}