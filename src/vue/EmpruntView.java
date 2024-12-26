package vue;

import controllers.EmpruntController;
import model.Emprunt;
import model.Livre;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmpruntView extends JPanel {
    private JTable empruntTable;
    private DefaultTableModel tableModel;
    private EmpruntController empruntController;
    private JButton emprunterButton;
    private JButton retournerButton;
    private JButton supprimerButton;
    private JButton renouvelerButton;
    private JComboBox<String> triComboBox;
    private JPanel actionPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> searchTypeComboBox;

    public EmpruntView(EmpruntController empruntController) {
        this.empruntController = empruntController;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Arrière-plan blanc pour un look propre

        // Table des emprunts
        tableModel = new DefaultTableModel(new String[]{"ID", "Livre", "Utilisateur", "Date Emprunt", "Date Retour Prévue", "Retour Effective", "Rendu", "Pénalité"}, 0);
        empruntTable = new JTable(tableModel);
        empruntTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        empruntTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));  // Police moderne et lisible
        empruntTable.setRowHeight(30);  // Hauteur des lignes plus grande pour une meilleure lisibilité
        add(new JScrollPane(empruntTable), BorderLayout.CENTER);

        // Panneau des actions
        actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());

        // Panneau de recherche
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));  // Espace entre les composants

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchButton.setBackground(new Color(60, 179, 113));  // Vert moderne pour le bouton
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        // Menu déroulant pour le type de recherche
        String[] searchTypes = {"ID Emprunt", "ID Livre", "ID Utilisateur", "Date"};
        searchTypeComboBox = new JComboBox<>(searchTypes);

        searchPanel.add(new JLabel("Recherche :"));
        searchPanel.add(searchField);
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchButton);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));  // Disposition en grille pour les boutons
        buttonPanel.setBackground(new Color(245, 245, 245));  // Arrière-plan gris clair pour séparer visuellement

        emprunterButton = new JButton("Emprunter Livre");
        retournerButton = new JButton("Retourner Livre");
        supprimerButton = new JButton("Supprimer Emprunt");
        renouvelerButton = new JButton("Prolonger Emprunt");

        styleButton(emprunterButton);
        styleButton(retournerButton);
        styleButton(supprimerButton);
        styleButton(renouvelerButton);

        buttonPanel.add(emprunterButton);
        buttonPanel.add(retournerButton);
        buttonPanel.add(supprimerButton);
        buttonPanel.add(renouvelerButton);

        // Panneau de tri
        JPanel triPanel = new JPanel();
        triPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Alignement à gauche
        triPanel.setBackground(new Color(245, 245, 245));  // Couleur de fond des filtres

        triPanel.add(new JLabel("Trier par :"));
        triComboBox = new JComboBox<>(new String[]{"Tous", "En cours", "Historique", "Par pénalités"});
        triPanel.add(triComboBox);

        // Ajouter tous les panneaux
        actionPanel.add(searchPanel, BorderLayout.NORTH);
        actionPanel.add(buttonPanel, BorderLayout.CENTER);
        actionPanel.add(triPanel, BorderLayout.SOUTH);

        add(actionPanel, BorderLayout.SOUTH);

        // Charger les emprunts existants
        chargerEmprunts("Tous");
        // Configurer les actions des boutons
        setupListeners();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 179, 113));  // Vert pour un look moderne
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 40));  // Taille uniforme pour les boutons
    }

    private void setupListeners() {
        // Écouteur pour le bouton de recherche
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            String searchType = (String) searchTypeComboBox.getSelectedItem();

            if (!searchText.isEmpty()) {
                List<Emprunt> resultats = empruntController.rechercherEmprunts(searchType, searchText);
                tableModel.setRowCount(0);
                for (Emprunt emprunt : resultats) {
                    ajouterLigneEmprunt(emprunt);
                }
            } else {
                chargerEmprunts((String) triComboBox.getSelectedItem());
            }
        });

        // Écouteur pour le JComboBox de tri
        triComboBox.addActionListener(e -> chargerEmprunts((String) triComboBox.getSelectedItem()));

        // Écouteur pour le bouton "Emprunter Livre"
        emprunterButton.addActionListener(e -> {
            JTextField livreIdField = new JTextField(10);
            JTextField userIdField = new JTextField(10);
            JPanel myPanel = new JPanel(new GridLayout(2, 2));
            myPanel.add(new JLabel("ID Livre:"));
            myPanel.add(livreIdField);
            myPanel.add(new JLabel("ID Utilisateur:"));
            myPanel.add(userIdField);

            int result = JOptionPane.showConfirmDialog(null, myPanel, "Emprunter Livre", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int livreId = Integer.parseInt(livreIdField.getText().trim());
                    String userId = userIdField.getText().trim();

                    Livre livre = empruntController.getEntityById(String.valueOf(livreId), "Livre");
                    User user = empruntController.getEntityById(userId, "User");

                    if (livre != null && user != null) {
                        empruntController.emprunterLivre(livre, user);
                        chargerEmprunts((String) triComboBox.getSelectedItem());
                    } else {
                        JOptionPane.showMessageDialog(this, "Livre ou utilisateur non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer un ID valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Écouteur pour le bouton "Retourner Livre"
        retournerButton.addActionListener(e -> {
            int selectedRow = empruntTable.getSelectedRow();
            if (selectedRow != -1) {
                int idEmprunt = (int) tableModel.getValueAt(selectedRow, 0);
                empruntController.retournerLivre(idEmprunt);
                chargerEmprunts((String) triComboBox.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à retourner.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Écouteur pour le bouton "Prolonger Emprunt"
        renouvelerButton.addActionListener(e -> {
            int selectedRow = empruntTable.getSelectedRow();
            if (selectedRow != -1) {
                int idEmprunt = (int) tableModel.getValueAt(selectedRow, 0);
                Emprunt emprunt = empruntController.getEntityById(String.valueOf(idEmprunt), "Emprunt");
                if (emprunt != null && !emprunt.isRendu() && emprunt.getPenalite() == 0) {
                    empruntController.renouvelerEmprunt(idEmprunt);
                    chargerEmprunts((String) triComboBox.getSelectedItem());
                } else {
                    JOptionPane.showMessageDialog(this, "L'emprunt ne peut pas être prolongé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à prolonger.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void chargerEmprunts(String triOption) {
        List<Emprunt> emprunts = empruntController.chargerEmprunts(triOption);
        tableModel.setRowCount(0);
        for (Emprunt emprunt : emprunts) {
            ajouterLigneEmprunt(emprunt);
        }
        empruntTable.revalidate();
        empruntTable.repaint();
    }

    private void ajouterLigneEmprunt(Emprunt emprunt) {
        // Utilisation de la méthode générique getEntityById pour récupérer le livre et l'utilisateur
        Livre livre = empruntController.getEntityById(String.valueOf(emprunt.getLivreId()), "Livre");
        String livreNom = (livre != null) ? livre.getTitre() : "Livre non trouvé";

        User user = empruntController.getEntityById(String.valueOf(emprunt.getUserId()), "User");
        String userNom = (user != null) ? user.getNom() : "Utilisateur non trouvé";

        // Ajout des données à la table
        tableModel.addRow(new Object[]{
            emprunt.getId(),
            livreNom,
            userNom,
            emprunt.getDateEmprunt(),
            emprunt.getDateRetourPrevue(),
            emprunt.getDateRetourEffective(),
            emprunt.isRendu() ? "Oui" : "Non",
            emprunt.getPenalite()
        });
    }
}
