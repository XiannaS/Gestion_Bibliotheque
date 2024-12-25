package vue;

import controllers.UserController;
import model.Role;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserView extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField nomField, prenomField, emailField, numeroTelField;
    private JComboBox<Role> roleComboBox;
    private JCheckBox statutCheckBox;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserController userController;
    private JTextField searchField;
    private JComboBox<Role> searchRoleComboBox;

    public UserView(UserController userController) {
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Formulaire de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(15);
        searchRoleComboBox = new JComboBox<>(new Role[]{
            null, // Option pour tous les rôles
            Role.MEMBRE,
            Role.BIBLIOTHECAIRE
        });
        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> searchUsers());

        searchPanel.add(new JLabel("Nom ou ID :"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Rôle :"));
        searchPanel.add(searchRoleComboBox);
        searchPanel.add(searchButton);

        // Ajoutez le panneau de recherche au haut de votre interface
        add(searchPanel, BorderLayout.NORTH);

        // Formulaire d'ajout / modification d'utilisateur
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        nomField = new JTextField();
        prenomField = new JTextField();
        emailField = new JTextField();
        numeroTelField = new JTextField();

        // Rôle : exclure ADMINISTRATEUR
        roleComboBox = new JComboBox<>(new Role[]{
            Role.MEMBRE,
            Role.BIBLIOTHECAIRE // Ajoutez d'autres rôles que vous souhaitez afficher
        });
        statutCheckBox = new JCheckBox("Actif");

        formPanel.add(new JLabel("Nom :"));
        formPanel.add(nomField);
        formPanel.add(new JLabel("Prénom :"));
        formPanel.add(prenomField);
        formPanel.add(new JLabel("Email :"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Numéro de téléphone :"));
        formPanel.add(numeroTelField);
        formPanel.add(new JLabel("Rôle :"));
        formPanel.add(roleComboBox);
        formPanel.add(new JLabel("Statut :"));
        formPanel.add(statutCheckBox);

        JButton addButton = new JButton("Ajouter Utilisateur");
        addButton.addActionListener(e -> addUser ());

        JButton updateButton = new JButton("Modifier Utilisateur");
        updateButton.addActionListener(e -> updateUser ());

        JButton deleteButton = new JButton("Supprimer Utilisateur");
        deleteButton.addActionListener(e -> deleteUser ());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Table pour afficher les utilisateurs
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Numéro de téléphone", "Rôle", "Statut"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> loadSelectedUser ());

        JScrollPane tableScrollPane = new JScrollPane(userTable);

        // Ajoutez le panneau de formulaire et le panneau de boutons
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(tableScrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER); // Ajoutez le panneau central au centre

        displayUsers(); // Affiche les utilisateurs au démarrage

        // Écouteur pour le champ de recherche
        searchField.addActionListener(e -> searchUsers());
    }

    private void addUser () {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String numeroTel = numeroTelField.getText().trim();
        Role role = (Role) roleComboBox.getSelectedItem();
        boolean statut = statutCheckBox.isSelected();

        // Validation des champs
        StringBuilder errors = new StringBuilder();
        if (nom.isEmpty()) errors.append("Le nom est obligatoire.\n");
        if (prenom.isEmpty()) errors.append("Le prénom est obligatoire.\n");
        if (email.isEmpty() || !isValidEmail(email)) errors.append("L'email est obligatoire et doit être valide.\n");
        if (!numeroTel.isEmpty() && !isValidPhoneNumber(numeroTel)) errors.append("Le numéro de téléphone doit contenir uniquement des chiffres.\n");
        if (role == null) errors.append("Le rôle est obligatoire.\n");

        // Affiche les erreurs s'il y en a
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, errors.toString(), "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un nouvel utilisateur
        String id = String.valueOf(System.currentTimeMillis()); // Générer un ID unique
        User user = new User(id, nom, prenom, email, numeroTel, "", role, statut);
        
        try {
            userController.addUser (user);
            JOptionPane.showMessageDialog(this, "Utilisateur ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            displayUsers();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d+"); // Vérifie que le numéro de téléphone ne contient que des chiffres
    }

    private void updateUser () {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String numeroTel = numeroTelField.getText();
        Role role = (Role) roleComboBox.getSelectedItem();
        boolean statut = statutCheckBox.isSelected();

        User user = new User(id, nom, prenom, email, numeroTel, "", role, statut);
        userController.updateUser (user);
        JOptionPane.showMessageDialog(this, "Utilisateur mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        displayUsers();
    }

    private void deleteUser () {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);

        // Boîte de dialogue de confirmation
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet utilisateur ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userController.deleteUser (id);
            JOptionPane.showMessageDialog(this, "Utilisateur supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            displayUsers();
        }
    }

    private void loadSelectedUser () {
        int selectedRow = userTable.getSelectedRow();
 if (selectedRow != -1) {
            nomField.setText((String) tableModel.getValueAt(selectedRow, 1));
            prenomField.setText((String) tableModel.getValueAt(selectedRow, 2));
            emailField.setText((String) tableModel.getValueAt(selectedRow, 3));
            numeroTelField.setText((String) tableModel.getValueAt(selectedRow, 4));
            roleComboBox.setSelectedItem(Role.fromLabel((String) tableModel.getValueAt(selectedRow, 5)));
            statutCheckBox.setSelected((Boolean) tableModel.getValueAt(selectedRow, 6));
        }
    }

    private void displayUsers() {
        tableModel.setRowCount(0); // Efface les utilisateurs précédemment affichés
        List<User> users = userController.getAllUsers();

        for (User  user : users) {
            Object[] rowData = {
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getNumeroTel(),
                user.getRole().getLabel(),
                user.isStatut()
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        numeroTelField.setText("");
        roleComboBox.setSelectedIndex(0); // Réinitialise le rôle à la première option
        statutCheckBox.setSelected(false); // Réinitialise le statut
        userTable.clearSelection(); // Efface la sélection dans le tableau
    }

    private void searchUsers() {
        String searchText = searchField.getText().trim();
        Role selectedRole = (Role) searchRoleComboBox.getSelectedItem();

        List<User> users = userController.getAllUsers();
        List<User> filteredUsers = new ArrayList<>();

        for (User  user : users) {
            boolean matches = true;

            // Vérifiez si le nom ou l'ID correspond
            if (!searchText.isEmpty()) {
                matches = user.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                          user.getId().toLowerCase().contains(searchText.toLowerCase());
            }

            // Vérifiez si le rôle correspond
            if (selectedRole != null && !user.getRole().equals(selectedRole)) {
                matches = false;
            }

            if (matches) {
                filteredUsers.add(user);
            }
        }

        // Mettez à jour le tableau avec les utilisateurs filtrés
        updateUserTable(filteredUsers);
    }

    private void updateUserTable(List<User> users) {
        tableModel.setRowCount(0); // Efface les utilisateurs précédemment affichés

        for (User  user : users) {
            Object[] rowData = {
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getNumeroTel(),
                user.getRole().getLabel(),
                user.isStatut()
            };
            tableModel.addRow(rowData);
        }
    }
} 