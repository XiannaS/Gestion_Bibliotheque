package vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Emprunt;
import model.EmpruntDAO;
import model.Livre;
import model.LivreDAO;
import model.User;
import model.UserDAO;

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
    private LivreDAO livreDAO;
    private UserDAO userDAO;
    private JTable livreTable; // Exemple de tableau de livres
    private JTable userTable;  // Exemple de tableau d'utilisateurs
    private JList<Emprunt> empruntsList;
    
public EmpruntView() {
    setLayout(new BorderLayout());
    livreDAO = new LivreDAO(   "src/ressources/books.csv");  // Assurez-vous que le chemin est correct

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

    // Exemple d'appel pour peupler le tableau avec des données d'emprunts
    EmpruntDAO empruntDAO = new EmpruntDAO("C:/Eclipse/gestionbibli/src/main/resources/ressources/emprunt.csv");
    updateEmpruntsTable(empruntDAO.listerEmprunts()); // Charge les emprunts depuis le DAO
}

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }
    public void updateEmpruntsTable(List<Emprunt> emprunts) {
        // Vider le tableau existant
        tableModel.setRowCount(0); // Efface toutes les lignes existantes

        // Ajouter chaque emprunt au tableau
        for (Emprunt emprunt : emprunts) {
            ajouterLigneEmprunt(emprunt);  // Appel de la méthode qui ajoute chaque emprunt avec son titre de livre
        }
    }


    // Méthodes pour récupérer les actions de l'utilisateur
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

    // Méthode pour mettre à jour la table avec les emprunts
    public void mettreAJourTableau(List<Emprunt> emprunts) {
        tableModel.setRowCount(0);  // Réinitialiser la table
        for (Emprunt emprunt : emprunts) {
            ajouterLigneEmprunt(emprunt);
        }
        empruntTable.revalidate();
        empruntTable.repaint();
    }
    public int getSelectedEmpruntId() {
        // Supposons que vous ayez un tableau ou une liste de emprunts dans la vue
        // Par exemple, si vous avez un JTable :
        int selectedRow = empruntTable.getSelectedRow(); // empruntTable est le JTable contenant les emprunts
        if (selectedRow == -1) {
            throw new IllegalStateException("Aucun emprunt sélectionné");
        }
        return (int) empruntTable.getValueAt(selectedRow, 0); // La première colonne contient l'ID de l'emprunt
    }

    public Livre getSelectedLivre() {
        int selectedRow = livreTable.getSelectedRow(); // livreTable est un JTable contenant les livres
        if (selectedRow == -1) {
            throw new IllegalStateException("Aucun livre sélectionné");
        }
        int livreId = (int) livreTable.getValueAt(selectedRow, 0); // La première colonne contient l'ID du livre
        return livreDAO.rechercherParID(livreId); // Chercher le livre dans le modèle (livreDAO)
    }
    public User getSelectedUser() {
        int selectedRow = userTable.getSelectedRow(); // userTable est un JTable contenant les utilisateurs
        if (selectedRow == -1) {
            throw new IllegalStateException("Aucun utilisateur sélectionné");
        }
        String userId = (String) userTable.getValueAt(selectedRow, 0); // La première colonne contient l'ID de l'utilisateur
        return userDAO.rechercherParID(userId); // Chercher l'utilisateur dans le modèle (userDAO)
    }

    private void ajouterLigneEmprunt(Emprunt emprunt) {
        // Récupérer le livre en fonction de l'ID du livre dans l'emprunt
        Livre livre = livreDAO.rechercherParID(emprunt.getLivreId());  // Cherche le livre par son ID
        String livreNom = livre != null ? livre.getTitre() : "Livre non trouvé";  // Si le livre existe, afficher son titre, sinon "Livre non trouvé"

        // Créer la ligne à ajouter au tableau
        Object[] row = {
            emprunt.getId(),
            livreNom,  // Afficher le nom du livre (titre)
            emprunt.getUserId(),   // Afficher l'ID de l'utilisateur (vous pouvez éventuellement remplacer cela par le nom de l'utilisateur)
            emprunt.getDateEmprunt(),
            emprunt.getDateRetourPrevue(),
            emprunt.getDateRetourEffective(),
            emprunt.isRendu() ? "Oui" : "Non",  // Afficher "Oui" ou "Non" en fonction de la valeur de rendu
            emprunt.getPenalite()  // Afficher la pénalité
        };

        // Ajouter la ligne au modèle du tableau
        tableModel.addRow(row);
    }



}
