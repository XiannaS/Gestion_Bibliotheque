package vue;

import model.Role;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserView extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField nomField, prenomField, emailField, numeroTelField;
    private JComboBox<Role> roleComboBox;
    private JCheckBox statutCheckBox;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Role> searchRoleComboBox;
    private JButton ajouterButton, modifierButton, supprimerButton, searchButton;

    public UserView() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Formulaire de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(null);

        searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchField.setMargin(new Insets(2, 10, 2, 2));

        searchRoleComboBox = new JComboBox<>(new Role[]{
            null, // Option pour tous les rôles
            Role.fromLabel("MEMBRE"),
            Role.fromLabel("Bibliothécaire")
        });

     // Dans la méthode initUI de UserView
        searchButton = new JButton("Rechercher"); // Remarquez ici qu'on utilise le champ d'instance
        searchButton.setFocusPainted(false);
        searchButton.setBackground(new Color(100, 149, 237));
        searchButton.setForeground(Color.WHITE);
 

        searchPanel.add(new JLabel("Nom ou ID :"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Rôle :"));
        searchPanel.add(searchRoleComboBox);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Carte pour le formulaire d'ajout / modification d'utilisateur
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Formulaire Utilisateur"));
        formCard.setBackground(null); // Fond subtil

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formCard.add(new JLabel("Nom :"), gbc);

        gbc.gridx = 1;
        nomField = new JTextField();
        formCard.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formCard.add(new JLabel("Prénom :"), gbc);

        gbc.gridx = 1;
        prenomField = new JTextField();
        formCard.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formCard.add(new JLabel("Email :"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField();
        formCard.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formCard.add(new JLabel("Numéro de téléphone :"), gbc);

        gbc.gridx = 1;
        numeroTelField = new JTextField();
        formCard.add(numeroTelField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formCard.add(new JLabel("Rôle :"), gbc);

        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new Role[]{
            Role.MEMBRE,
            Role.BIBLIOTHECAIRE
        });
        formCard.add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formCard.add(new JLabel("Statut :"), gbc);

        gbc.gridx = 1;
        statutCheckBox = new JCheckBox("Actif");
        formCard.add(statutCheckBox, gbc);

        ajouterButton = new JButton("Ajouter Utilisateur");
        modifierButton = new JButton("Modifier Utilisateur");
        supprimerButton = new JButton("Supprimer Utilisateur");

        // Personnalisation des boutons
        ajouterButton.setFocusPainted(false);
        modifierButton.setFocusPainted(false);
        supprimerButton.setFocusPainted(false);

        ajouterButton.setBackground(new Color(60, 179, 113)); // Couleur personnalisée
        modifierButton.setBackground(new Color(255, 165, 0)); // Couleur personnalisée
        supprimerButton.setBackground(new Color(255, 69, 58)); // Couleur personnalisée

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Centré avec espacement vertical réduit
        buttonPanel.setBackground(null); // Pas de fond
        buttonPanel.add(ajouterButton);
        buttonPanel.add(modifierButton);
        buttonPanel.add(supprimerButton);

        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formContainer.setBackground(null); // Pas de fond
        formContainer.add(formCard, BorderLayout.CENTER);
        formContainer.add(buttonPanel, BorderLayout.NORTH); // Les boutons en haut

        // Carte pour afficher les utilisateurs
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 1, true), "Liste des Utilisateurs"));
        tableCard.setBackground(null); // Fond subtil

        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Numéro de téléphone", "Rôle", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(userTable);

        tableCard.add(tableScrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marge autour des cartes
        centerPanel.setBackground(null); // Pas de fond

        centerPanel.add(formContainer);
        centerPanel.add(tableCard);

        add(centerPanel, BorderLayout.CENTER);
    }

    public JTextField getNomField() { return nomField; }
    public JTextField getPrenomField() { return prenomField; }
    public JTextField getEmailField() { return emailField; }
    public JTextField getNumeroTelField() { return numeroTelField; }
   
    public JCheckBox getStatutCheckBox() { return statutCheckBox; }

    public Role getSelectedRole() {
        return (Role) roleComboBox.getSelectedItem();
    }

    public boolean isStatutChecked() {
        return statutCheckBox.isSelected();
    }

    public JTable getUserTable() {
        return userTable;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JComboBox<Role> getSearchRoleComboBox() {
        return searchRoleComboBox;
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

    // Méthodes pour mettre à jour les champs
    public void setNomField(String nom) {
        nomField.setText(nom);
    }

    public void setPrenomField(String prenom) {
        prenomField.setText(prenom);
    }

    public void setEmailField(String email) {
        emailField.setText(email);
    }

    public void setNumeroTelField(String numeroTel) {
        numeroTelField.setText(numeroTel);
    }

    public void setRoleComboBox(Role role) {
        roleComboBox.setSelectedItem(role);
    }

    // Méthode pour vider les champs
    public void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        numeroTelField.setText("");
        roleComboBox.setSelectedIndex(0);
        statutCheckBox.setSelected(false);
    }

    public void displayUsers(List<User> users) {
        tableModel.setRowCount(0); // Réinitialiser le modèle de table
        for (User user : users) {
            Object[] rowData = {
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getNumeroTel(),
                user.getRole().getLabel(),
                user.isStatut()
            };
            tableModel.addRow(rowData); // Ajouter les données à la table
        }
    }
 
 // Dans la classe UserView

    public void updateUserTable(List<User> users) {
        // Mettre à jour le modèle de la table avec les utilisateurs filtrés
        DefaultTableModel tableModel = (DefaultTableModel) userTable.getModel();
        tableModel.setRowCount(0); // Réinitialiser les lignes existantes de la table
        
        // Ajouter les utilisateurs filtrés à la table
        for (User user : users) {
            Object[] row = {
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getNumeroTel(),
                user.getRole(),
                user.isStatut()
            };
            tableModel.addRow(row);
        }
    }

    public JButton getSearchButton() {
        return searchButton;
    }
    public JComboBox<Role> getRoleComboBox() {
        return roleComboBox; // Assurez-vous que roleComboBox est un JComboBox
    }
 
    public void setStatutChecked(boolean statut) {
        statutCheckBox.setSelected(statut);
    }}
