package model;
public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private String genre;
    private int anneePublication;
    private String imageUrl;
    private String isbn; // Ajout de l'ISBN
    private String description; // Ajout d'une description
    private String editeur; // Ajout de l'éditeur
    private int totalExemplaires; // Nombre total d'exemplaires
    private int exemplairesDisponibles; // Nombre d'exemplaires disponibles

    // Constructeur
    public Livre(int id, String titre, String auteur, String genre, int anneePublication, String imageUrl, String isbn, String description, String editeur, int totalExemplaires) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.genre = genre;
        this.anneePublication = anneePublication;
        this.imageUrl = imageUrl;
        this.isbn = isbn;
        this.description = description;
        this.editeur = editeur;
        this.totalExemplaires = totalExemplaires;
        this.exemplairesDisponibles = totalExemplaires; // Initialement, tous les exemplaires sont disponibles
    }

    // Getters et Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles;
    }

    public int getTotalExemplaires() {
        return totalExemplaires; // Retourne le nombre total d'exemplaires
    }
    
    public void setTotalExemplaires(int totalExemplaires) {
        this.totalExemplaires = totalExemplaires;
    }
    
    public void emprunter() {
        if (exemplairesDisponibles > 0) {
            exemplairesDisponibles--;
        } else {
            throw new IllegalStateException("Aucun exemplaire disponible pour emprunt.");
        }
    }

    public void retourner() {
        if (exemplairesDisponibles < totalExemplaires) {
            exemplairesDisponibles++; // Incrémenter uniquement les exemplaires disponibles
        }
    }

    public boolean isDisponible() {
        return exemplairesDisponibles > 0; // Un livre est disponible s'il y a des exemplaires disponibles
    }

 
    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setExemplairesDisponibles(int exemplairesDisponibles) {
        this.exemplairesDisponibles = exemplairesDisponibles;
    }
    @Override
    public String toString() {
        return id + ";" + titre + ";" + auteur + ";" + genre + ";" + anneePublication + ";" + imageUrl + ";" + 
               isbn + ";" + description + ";" + editeur + ";" + totalExemplaires;
    }
}