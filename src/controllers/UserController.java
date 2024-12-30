package controllers;

import model.Role;
import model.User;
import model.UserDAO;
import vue.UserView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserController {
    private UserView userView;
    private UserDAO userDAO; // Assume you have a UserService to handle data operations

    public UserController(UserView userView, UserDAO userDAO) {
        this.userView = userView;
        this.userDAO = userDAO;

        // Ajouter des écouteurs d'événements
        userView.getAjouterButton().addActionListener(e -> ajouterUtilisateur());
        userView.getModifierButton().addActionListener(e -> modifierUtilisateur());
        userView.getSupprimerButton().addActionListener(e -> supprimerUtilisateur());
        userView.getUserTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    afficherDetailsUtilisateur();
                }
            }
        });
    }

    private void ajouterUtilisateur() {
        User user = new User(userView.getNomField(), userView.getPrenomField(), userView.getEmailField(),
                userView.getNumeroTelField(), "", userView.getSelectedRole(), userView.isStatutChecked());
        try {
            userService.addUser (user);
            JOptionPane.showMessageDialog(userView, "Utilisateur ajouté avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            userView.clearFields();
            displayUsers();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(userView, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierUtilisateur() {
        int selectedRow = userView.getUserTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(userView, "Veuillez sélectionner un utilisateur à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) userView.getUserTable().getValueAt(selectedRow, 0);
        User user = new User(id, userView.getNomField(), userView.getPrenomField(), userView.getEmailField(),
                userView.getNumeroTelField(), "", userView.getSelectedRole(), userView.isStatutChecked());
        userDAO.updateUser (user);
        JOptionPane.showMessageDialog(userView, "Utilisateur mis à jour avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        userView.clearFields();
        displayUsers();
    }

    private void supprimerUtilisateur() {
        int selectedRow = userView.getUserTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(userView, "Veuillez sélectionner un utilisateur à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) userView.getUserTable().getValueAt(selectedRow, 0);
        userDAO.deleteUser (id);
        JOptionPane.showMessageDialog(userView, "Utilisateur supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
        userView.clearFields();
        displayUsers();
    }

    private void afficherDetailsUtilisateur() {
        int selectedRow = userView.getUserTable().getSelectedRow();
        if (selectedRow != -1) {
            userView.getNomField().setText((String) userView.getUserTable().getValueAt(selectedRow, 1));
            userView.getPrenomField().setText((String) userView.getUserTable().getValueAt(selectedRow, 2));
            userView.getEmailField().setText((String) userView.getUserTable().getValueAt(selectedRow, 3));
            userView.getNumeroTelField().setText((String) userView.getUserTable().getValueAt(selectedRow, 4));
            userView.getRoleComboBox().setSelectedItem(Role.fromLabel((String) userView.getUserTable().getValueAt(selectedRow, 5)));
            userView.setStatutChecked((Boolean) userView.getUserTable().getValueAt(selectedRow, 6));
        }
    }

    public void displayUsers() {
        List<User> users = userDAO.getAllUsers();
        userView.displayUsers(users);
    }
}  