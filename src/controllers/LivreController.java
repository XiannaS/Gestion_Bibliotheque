package controllers;

import model.Livre;
import model.LivreDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LivreController {
    private LivreDAO livreDAO;

    public LivreController(String filePath) {
        this.livreDAO = new LivreDAO(filePath);
    }

    public List<Livre> getAllLivres() {
        return livreDAO.getAllLivres();
    }

    public void addLivre(Livre livre) {
        if (livreExists(livre.getId())) {
            throw new IllegalArgumentException("Un livre avec cet ID existe déjà.");
        }
        livreDAO.addLivre(livre); // Ajoutez le livre au DAO
    }

    public boolean livreExists(int id) {
        return getAllLivres().stream().anyMatch(livre -> livre.getId() == id);
    }

    public void updateLivre(Livre livre) {
        livreDAO.updateLivre(livre);
    }

    public int getNextLivreId() {
        List<Livre> livres = getAllLivres();
        if (livres.isEmpty()) {
            return 1; // Si aucun livre n'existe, commencez à 1
        }
        return livres.stream().mapToInt(Livre::getId).max().orElse(0) + 1; // Incrémentez le plus grand ID
    }

    // Méthode générique pour rechercher des livres par différents critères
    public List<Livre> searchLivres(String searchTerm, String criteria, boolean isAvailable) {
        List<Livre> resultats = new ArrayList<>();

        // Effectuer la recherche en fonction du critère
        switch (criteria.toLowerCase()) {
            case "titre":
                resultats = searchByTitre(searchTerm);
                break;
            case "auteur":
                resultats = searchByAuteur(searchTerm);
                break;
            case "annee":
                try {
                    int annee = Integer.parseInt(searchTerm);
                    resultats = searchByAnnee(annee);
                } catch (NumberFormatException e) {
                    System.out.println("Erreur : Le terme de recherche pour l'année doit être un nombre valide.");
                    return new ArrayList<>();
                }
                break;
            default:
                System.out.println("Critère de recherche invalide. Veuillez choisir parmi : titre, auteur, annee.");
                return new ArrayList<>();
        }

        // Filtrer par disponibilité si nécessaire
        if (isAvailable) {
            resultats = resultats.stream()
                    .filter(Livre::isDisponible)
                    .collect(Collectors.toList());
        }

        return resultats;
    }

    // Recherche spécifique par titre
    public List<Livre> searchByTitre(String titre) {
        return getAllLivres().stream()
                .filter(livre -> livre.getTitre().toLowerCase().contains(titre.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Recherche spécifique par auteur
    public List<Livre> searchByAuteur(String auteur) {
        return getAllLivres().stream()
                .filter(livre -> livre.getAuteur().toLowerCase().contains(auteur.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Recherche spécifique par année
    public List<Livre> searchByAnnee(int annee) {
        return getAllLivres().stream()
                .filter(livre -> livre.getAnneePublication() == annee)
                .collect(Collectors.toList());
    }

    // Recherche spécifique par disponibilité
    public List<Livre> searchByDisponibilite(boolean disponible) {
        return getAllLivres().stream()
                .filter(livre -> livre.isDisponible() == disponible)
                .collect(Collectors.toList());
    }

    public List<Livre> sortLivres(String criteria) {
        List<Livre> livres = getAllLivres();
        switch (criteria.toLowerCase()) {
            case "titre":
                return livres.stream().sorted((l1, l2) -> l1.getTitre().compareTo(l2.getTitre())).collect(Collectors.toList());
            case "auteur":
                return livres.stream().sorted((l1, l2) -> l1.getAuteur().compareTo(l2.getAuteur())).collect(Collectors.toList());
            case "annee":
                return livres.stream().sorted((l1, l2) -> Integer.compare(l1.getAnneePublication(), l2.getAnneePublication())).collect(Collectors.toList());
            default:
                return livres;
        }
    }

    public void deleteLivre(int id) {
        livreDAO.deleteLivre(id); // Appel à la méthode de suppression dans le DAO
    }
    
    public boolean livreExists(String titre, String auteur, int annee) {
        return getAllLivres().stream()
                .anyMatch(livre -> livre.getTitre().equalsIgnoreCase(titre) &&
                                   livre.getAuteur().equalsIgnoreCase(auteur) &&
                                   livre.getAnneePublication() == annee);
    }
}
