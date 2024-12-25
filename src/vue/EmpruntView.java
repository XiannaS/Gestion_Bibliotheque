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
    private JTextField searchField; // Champ de recherche
    private JButton searchButton; // Bouton de recherche
    private JComboBox<String> searchTypeComboBox; // Menu déroulant pour le type de recherche

public EmpruntView(EmpruntController empruntController) {
    this.empruntController = empruntController;
    setLayout(new BorderLayout()); 
 
    // Table des emprunts
    tableModel = new DefaultTableModel(new String[]{"ID", "Livre", "Utilisateur", "Date Emprunt", "Date Retour Prévue", "Retour Effective", "Rendu", "Pénalité"}, 0);
    empruntTable = new JTable(tableModel);
    add(new JScrollPane(empruntTable), BorderLayout.CENTER);

    // Panneau des actions
    actionPanel = new JPanel(new BorderLayout());

    // Panneau de recherche
    JPanel searchPanel = new JPanel();
    searchField = new JTextField(15);
    searchButton = new JButton("Rechercher");

    // Menu déroulant pour le type de recherche
    String[] searchTypes = {"ID Emprunt", "ID Livre", "ID Utilisateur", "Date"};
    searchTypeComboBox = new JComboBox<>(searchTypes);
    
    searchPanel.add(new JLabel("Recherche :"));
    searchPanel.add(searchField);
    searchPanel.add(searchTypeComboBox);
    searchPanel.add(searchButton);
    actionPanel.add(searchPanel, BorderLayout.NORTH);
 
    // Panneau des boutons
    JPanel buttonPanel = new JPanel();
    emprunterButton = new JButton("Emprunter Livre");
    retournerButton = new JButton("Retourner Livre");
    supprimerButton = new JButton("Supprimer Emprunt");
    renouvelerButton = new JButton("Prolonger Emprunt");

    buttonPanel.add(emprunterButton);
    buttonPanel.add(retournerButton);
    buttonPanel.add(supprimerButton);
    buttonPanel.add(renouvelerButton);

    // Panneau de tri
    JPanel triPanel = new JPanel();
    triPanel.add(new JLabel("Trier par :"));
    triComboBox = new JComboBox<>(new String[]{"Tous", "En cours", "Historique", "Par pénalités"});
    triPanel.add(triComboBox);

    actionPanel.add(buttonPanel, BorderLayout.CENTER);
    actionPanel.add(triPanel, BorderLayout.SOUTH); // Ajout en bas du panneau d'actions
    add(actionPanel, BorderLayout.SOUTH);

    // Charger les emprunts existants
    chargerEmprunts("Tous");
    // Configurer les actions des boutons
    setupListeners();
}

private void setupListeners() {
    // Écouteur pour le bouton de recherche
    searchButton.addActionListener(e -> {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchTypeComboBox.getSelectedItem(); // Récupérer le type de recherche

        if (!searchText.isEmpty()) {
            List<Emprunt> resultats = empruntController.rechercherEmprunts(searchType, searchText); // Passer le type de recherche
            tableModel.setRowCount(0); // Vider le tableau
            for (Emprunt emprunt : resultats) {
                // Récupérer le livre par ID
                Livre livre = empruntController.getLivreById(emprunt.getLivreId());
                String livreNom = (livre != null) ? livre.getTitre() : "Livre non trouvé";

                // Récupérer l'utilisateur par ID
                User user = empruntController.getUserById(String.valueOf(emprunt.getUserId()));
                String userNom = (user != null) ? user.getNom() : "Utilisateur non trouvé";

                // Ajouter une ligne au modèle de table
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
        } else {
            chargerEmprunts((String) triComboBox.getSelectedItem()); // Recharger selon le type de tri sélectionné
        }
    });

    // Écouteur pour le JComboBox de tri
    triComboBox.addActionListener(e -> {
        String selectedOption = (String) triComboBox.getSelectedItem();
        chargerEmprunts(selectedOption); // Charger les emprunts selon l'option sélectionnée
    });

    // Écouteur pour le bouton "Emprunter Livre"
    emprunterButton.addActionListener(e -> {
        // Créer un formulaire pour saisir l'ID du livre et l'ID de l'utilisateur
        JTextField livreIdField = new JTextField(10);
        JTextField userIdField = new JTextField(10);
        
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(2, 2)); // Utiliser un GridLayout pour aligner les champs
        myPanel.add(new JLabel("ID Livre:"));
        myPanel.add(livreIdField);
        myPanel.add(new JLabel("ID Utilisateur:"));
        myPanel.add(userIdField);
        
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Emprunter Livre", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int livreId = Integer.parseInt(livreIdField.getText().trim());
                String userId = userIdField.getText().trim();

                // Récupérer le livre et l'utilisateur
                Livre livre = empruntController.getLivreById(livreId);
                User user = empruntController.getUserById(userId); // Assurez-vous que userId est de type String

                if (livre != null && user != null) {
                
					empruntController.emprunterLivre(livre, user);
                    chargerEmprunts((String) triComboBox.getSelectedItem()); // Recharger les emprunts après emprunt
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
            int idEmprunt = (int) tableModel.getValueAt(selectedRow, 0); // Assurez-vous que c'est un Integer
            empruntController.retournerLivre(idEmprunt);
            chargerEmprunts((String) triComboBox.getSelectedItem()); // Recharger les emprunts après retour
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à retourner.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });

    // Écouteur pour le bouton "Prolonger Emprunt"
    renouvelerButton.addActionListener(e -> {
        int selectedRow = empruntTable.getSelectedRow();
        if (selectedRow != -1) {
            int idEmprunt = (int) tableModel.getValueAt(selectedRow, 0); // Assurez-vous que c'est un Integer
            Emprunt emprunt = empruntController.getEmpruntById(idEmprunt);
            if (emprunt != null && !emprunt.isRendu() && emprunt.getPenalite() == 0) {
                empruntController.renouvelerEmprunt(idEmprunt);
                chargerEmprunts((String) triComboBox.getSelectedItem()); // Recharger les emprunts après renouvellement
            } else {
                JOptionPane.showMessageDialog(this, "L'emprunt ne peut pas être prolongé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à prolonger.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    });
}

public void chargerEmprunts(String triOption) {
    List<Emprunt> emprunts = empruntController.chargerEmprunts(triOption); // Appel à la méthode du contrôleur
    tableModel.setRowCount(0); // Vider le tableau

    for (Emprunt emprunt : emprunts) {
        // Récupérer le livre par ID
        Livre livre = empruntController.getLivreById(emprunt.getLivreId());
        String livreNom = (livre != null) ? livre.getTitre() : "Livre non trouvé";

        // Récupérer l'utilisateur par ID
        User user = empruntController.getUserById(String.valueOf(emprunt.getUserId()));
        String userNom = (user != null) ? user.getNom() : "Utilisateur non trouvé";

        // Ajouter une ligne au modèle de table
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

    // Revalider et repeindre la table
    empruntTable.revalidate();
    empruntTable.repaint();
}
public void chargerEmprunts() {
    chargerEmprunts("Tous"); // Appel à la méthode existante avec "Tous" comme argument par défaut
}
}