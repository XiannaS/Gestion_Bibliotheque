package model;

public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private String genre;
    private int anneePublication;
    private String imageUrl;
    private boolean disponible; // Indique si le livre est disponible

    // Constructeur
 // Constructeur
    public Livre(int id, String titre, String auteur, String genre, int anneePublication, String imageUrl, boolean disponible) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.genre = genre;
        this.anneePublication = anneePublication;
        this.imageUrl = imageUrl;
        this.disponible = disponible; // Utilisez la valeur passée
    }

    // Getters et Setters
    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
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

    @Override
    public String toString() {
        return id + ";" + titre + ";" + auteur + ";" + genre + ";" + anneePublication + ";" + imageUrl;
    }
}