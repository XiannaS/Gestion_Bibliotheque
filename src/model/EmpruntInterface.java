package model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface EmpruntInterface {

    // Ajouter un nouvel emprunt
    void ajouterEmprunt(Emprunt emprunt);

    // Retourner un livre par ID d'emprunt
    void retournerLivre(int empruntId) throws IllegalArgumentException;

    // Lister tous les emprunts
    List<Emprunt> listerEmprunts();

    // Obtenir un emprunt par son ID
    Emprunt getEmpruntById(int empruntId) throws IllegalArgumentException;

    // Mettre à jour les informations d'un emprunt
    void updateEmprunt(Emprunt emprunt) throws IllegalArgumentException;

    // Supprimer un emprunt par ID
    void supprimerEmprunt(int empruntId) throws IllegalArgumentException;

    // Supprimer tous les emprunts
    void supprimerTousLesEmprunts() throws IOException;

    // Renouveler un emprunt
    void renouvelerEmprunt(int empruntId) throws IllegalArgumentException;

    // Filtrer les emprunts en fonction de critères spécifiques
    List<Emprunt> filtrerEmprunts(String searchTerm, String searchType) throws IllegalArgumentException;

    // Trier les emprunts en fonction de critères spécifiques
    List<Emprunt> trierEmprunts(String criteria);

    // Générer un nouvel ID unique pour un emprunt
    int generateEmpruntId();
}