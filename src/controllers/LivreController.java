package controllers;

import model.Livre;
import model.LivreDAO;
 
import exception.LivreException;

import javax.swing.JOptionPane;

import java.net.MalformedURLException;
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

    public void addLivre(Livre livre) throws LivreException {
        if (livreExists(livre.getId())) {
            throw new LivreException("Un livre avec cet ID existe déjà.");
        }
        if (livreExists(livre.getTitre(), livre.getAuteur(), livre.getAnneePublication(), livre.getIsbn())) {
            throw new LivreException("Un livre avec le même titre, auteur, année et ISBN existe déjà.");
        }
        livreDAO.addLivre(livre); // Ajoutez le livre au DAO
    }

    public boolean livreExists(int id) {
        return getAllLivres().stream().anyMatch(livre -> livre.getId() == id);
    }
    public void updateLivre(Livre livre, String titre, String auteur, int annee, String genre, String imageUrl, 
            String isbn, String description, String editeur, int totalExemplaires) {
	try {
	// Validation des données
	if (!isValidString(titre) || !isValidString(auteur) || !isValidString(genre) || !isValidString(isbn) 
	|| !isValidString(description) || !isValidString(editeur)) {
	throw new IllegalArgumentException("Tous les champs doivent être remplis.");
	}
	
	if (annee <= 0 || totalExemplaires <= 0) {
	throw new IllegalArgumentException("L'année et le nombre d'exemplaires doivent être positifs.");
	}
	
	if (!isValidImageUrl(imageUrl)) {
	throw new IllegalArgumentException("L'URL de l'image n'est pas valide.");
	}
	
	// Mise à jour du livre dans le modèle
	livre.setTitre(titre);
	livre.setAuteur(auteur);
	livre.setAnneePublication(annee);
	livre.setGenre(genre);
	livre.setImageUrl(imageUrl);
	livre.setIsbn(isbn);
	livre.setDescription(description);
	livre.setEditeur(editeur);
	livre.setTotalExemplaires(totalExemplaires);
	
	// Mise à jour du livre dans la base de données ou le modèle
	livreDAO.updateLivre(livre);
	
	} catch (IllegalArgumentException e) {
	showMessage(e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	} catch (Exception e) {
	showMessage("Une erreur s'est produite lors de la mise à jour du livre.", "Erreur", JOptionPane.ERROR_MESSAGE);
	}
	}
	
	//Méthode pour valider une chaîne de caractères non vide
	private boolean isValidString(String str) {
	return str != null && !str.trim().isEmpty();
	}

	//Méthode pour valider l'URL de l'image
	public boolean isValidImageUrl(String url) {
	    try {
	        new java.net.URL(url); // Cela peut lancer MalformedURLException
	        return true;  // URL valide
	    } catch (MalformedURLException e) {
	        return false; // URL invalide
	    }
	}
	 

    public int getNextLivreId() {
        List<Livre> livres = getAllLivres();
        if (livres.isEmpty()) {
            return 1; // Si aucun livre n'existe, commencez à 1
        }
        return livres.stream().mapToInt(Livre::getId).max().orElse(0) + 1; // Incrémentez le plus grand ID
    }

    public List<Livre> searchLivres(String searchTerm, String criteria, boolean isAvailable) throws LivreException {
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
                    throw new LivreException("Le terme de recherche pour l'année doit être un nombre valide.");
                }
                break;
            default:
                throw new LivreException("Critère de recherche invalide. Veuillez choisir parmi : titre, auteur, annee.");
        }

        // Filtrer par disponibilité si nécessaire
        if (isAvailable) {
            resultats = resultats.stream()
                    .filter(Livre::isDisponible)
                    .collect(Collectors.toList());
        }

        return resultats;
    }

    public List<Livre> searchByTitre(String titre) {
        return getAllLivres().stream()
                .filter(livre -> livre.getTitre().toLowerCase().contains(titre.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livre> searchByAuteur(String auteur) {
        return getAllLivres().stream()
                .filter(livre -> livre.getAuteur().toLowerCase().contains(auteur.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livre> searchByAnnee(int annee) {
        return getAllLivres().stream()
                .filter(livre -> livre.getAnneePublication() == annee)
                .collect(Collectors.toList());
    }

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

    public void deleteLivre(int id) throws LivreException {
        if (!livreExists(id)) {
            throw new LivreException("Le livre à supprimer n'existe pas.");
        }
        livreDAO.deleteLivre(id); // Appel à la méthode de suppression dans le DAO
    }

    public boolean livreExists(String titre, String auteur, int annee, String isbn) {
        return getAllLivres().stream()
                .anyMatch(livre -> livre.getTitre().equalsIgnoreCase(titre) &&
                                   livre.getAuteur().equalsIgnoreCase(auteur) &&
                                   livre.getAnneePublication() == annee &&
                                   livre.getIsbn().equals(isbn));
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }
 
}
