package model;


import exception.UserNotFoundException;
import exception.InvalidUserDataException;
import java.util.List;

public interface UsermodelInterface {

    // Ajoute un utilisateur, avec validation des données
    void ajouterUser(User user) throws InvalidUserDataException;

    // Modifie les données d'un utilisateur existant
    void modifierUser(String id, String nouveauNom, String nouvelEmail) throws UserNotFoundException, InvalidUserDataException;

    // Supprime un utilisateur par ID
    void supprimerUser(String id) throws UserNotFoundException;

    // Trie la liste des utilisateurs par ordre alphabétique de nom
    void trierListeUsersParNom();

    // Recherche un utilisateur par ID
    User rechercherParID(String id) throws UserNotFoundException;

    // Retourne la liste de tous les utilisateurs
    List<User> getAllUsers();
}
