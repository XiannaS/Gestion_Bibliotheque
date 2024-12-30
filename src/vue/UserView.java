package vue;

import model.Role;
import model.User;

import javax.swing.*;
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
    private JButton ajouterButton, modifierButton, supprimerButton;

    public UserView() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Formulaire de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(15);
        searchRoleComboBox = new JComboBox<>(new Role[] {
        	    null, // Option pour tous les rôles
        	    Role.fromLabel("MEMBRE"),
        null// Assurez-vous que l'étiquette est en majuscules
        	});

        JButton searchButton = new JButton("Rechercher");

        searchPanel.add(new JLabel("Nom ou ID :"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Rôle :"));
        searchPanel.add(searchRoleComboBox);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // Formulaire d'ajout / modification d'utilisateur
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        nomField = new JTextField();
        prenomField = new JTextField();
        emailField = new JTextField();
        numeroTelField = new JTextField();

        roleComboBox = new JComboBox<>(new Role[]{
            Role.MEMBRE,
            Role.BIBLIOTHECAIRE
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

        ajouterButton = new JButton("Ajouter Utilisateur");
        modifierButton = new JButton("Modifier Utilisateur");
        supprimerButton = new JButton("Supprimer Utilisateur");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ajouterButton);
        buttonPanel.add(modifierButton);
        buttonPanel.add(supprimerButton);

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

        JScrollPane tableScrollPane = new JScrollPane(userTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(tableScrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void displayUsers(List<User> users) {
        tableModel.setRowCount(0);
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
            tableModel.addRow(rowData);
        }
    }

    public String getNomField() {
        return nomField.getText().trim();
    }

    public String getPrenomField() {
        return prenomField.getText().trim();
    }

    public String getEmailField() {
        return emailField.getText().trim();
    }

    public String getNumeroTelField() {
        return numeroTelField.getText().trim();
    }

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

    public void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        numeroTelField.setText("");
        roleComboBox.setSelectedIndex(0);
        statutCheckBox.setSelected(false);
        userTable.clearSelection();
    }

    public JComboBox<Role> getRoleComboBox() {
        return roleComboBox; // Assurez-vous que roleComboBox est un JComboBox
    }
    
    public void setStatutChecked(boolean statut) {
        statutCheckBox.setSelected(statut);
    }
     
 
}