package vue;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Emprunt;
import model.Livre;
import model.LivreDAO;

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

        // Personnaliser le rendu des cellules pour afficher des couleurs différentes
        empruntTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Si c'est une pénalité, affiche en rouge
                if (column == 7 && value instanceof Number && ((Number) value).doubleValue() > 0) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }

                // Appliquer des couleurs alternées pour les lignes
                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 245, 245)); // Couleur de fond pour les lignes paires
                } else {
                    c.setBackground(Color.WHITE); // Couleur de fond pour les lignes impaires
                }

                return c;
            }
        });

        // Carte pour la table des emprunts
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Liste des Emprunts"));
        tableCard.setBackground(null); // Pas de fond
        tableCard.add(new JScrollPane(empruntTable), BorderLayout.CENTER);

        // Panneau de recherche
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Recherche d'Emprunts"));
        searchPanel.setBackground(null); // Pas de fond

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(new JLabel("Recherche :"), gbc);

        gbc.gridx = 1;
        searchField = new JTextField(20); // Taille fixe
        searchPanel.add(searchField, gbc);

        gbc.gridx = 2;
        String[] searchTypes = {"ID Emprunt", "Titre Livre", "ID Utilisateur", "Date"};
        searchTypeComboBox = new JComboBox<>(searchTypes);
        searchPanel.add(searchTypeComboBox, gbc);

        gbc.gridx = 3;
        searchButton = new JButton("Rechercher");
        styleButton(searchButton);
        searchPanel.add(searchButton, gbc);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centré avec espacement horizontal réduit
        buttonPanel.setBackground(null); // Pas de fond

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
        JPanel triPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        triPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Options de Tri"));
        triPanel.setBackground(null); // Pas de fond

        triPanel.add(new JLabel("Trier par :"));
        triComboBox = new JComboBox<>(new String[]{"Tous", "En cours", "Historique", "Par pénalités"});
        triPanel.add(triComboBox);

        // Réduire la taille des cartes
        JPanel searchCardWrapper = new JPanel(new BorderLayout());
        searchCardWrapper.add(searchPanel, BorderLayout.CENTER);
        searchCardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchCardWrapper.setPreferredSize(new Dimension(450, 200)); // Taille préférée

        JPanel tableCardWrapper = new JPanel(new BorderLayout());
        tableCardWrapper.add(tableCard, BorderLayout.CENTER);
        tableCardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableCardWrapper.setPreferredSize(new Dimension(450, 300)); // Taille préférée

        JPanel triCardWrapper = new JPanel(new BorderLayout());
        triCardWrapper.add(triPanel, BorderLayout.CENTER);
        triCardWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        triCardWrapper.setPreferredSize(new Dimension(450, 100)); // Taille préférée

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Disposition en grille avec 2 lignes et 1 colonne
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marge autour des cartes
        centerPanel.setBackground(null); // Pas de fond

        centerPanel.add(searchCardWrapper);
        centerPanel.add(tableCardWrapper);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
        add(triCardWrapper, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }

    public void updateEmpruntsTable(List<Emprunt> emprunts, LivreDAO livreDAO) {
        tableModel.setRowCount(0); // Efface toutes les lignes existantes
        for (Emprunt emprunt : emprunts) {
            // Récupérer le livre correspondant à l'ID de l'emprunt
            Livre livre = livreDAO.rechercherParID(emprunt.getLivreId());
            String titreLivre = (livre != null) ? livre.getTitre() : "Livre non trouvé";

            Object[] row = {
                emprunt.getId(),
                titreLivre, // Utiliser le titre du livre ici
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
