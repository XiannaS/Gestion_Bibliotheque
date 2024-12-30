 package model;

public class User {
    private String id; // Identifiant unique
    private String nom;
    private String prenom;
    private String email;
    private String numeroTel; // Nouveau champ pour le numéro de téléphone
    private String motDePasse; // Peut être vide ou nul pour les membres
    private Role role;
    private boolean statut; // Nouveau champ pour le statut

    // Constructeur
    public User(String id, String nom, String prenom, String email, String numeroTel, String motDePasse, Role role, boolean statut) {
        this.id = id; // Assurez-vous de l'initialiser
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numeroTel = numeroTel; // Assurez-vous de l'initialiser
        this.motDePasse = motDePasse; // Peut être vide ou nul
        this.role = role;
        this.statut = statut;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }
    public boolean isActive() {
        return statut; // Retourne true si l'utilisateur est actif, false sinon
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }
 
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStatut() {
        return statut; // Retourner le statut
    }

    public void setStatut(boolean statut) {
        this.statut = statut; // Setter pour le statut
    }

    // Méthode pour afficher une représentation lisible de l'utilisateur
    @Override
    public String toString() {
        return "User  {" +
                "nom='" + (nom != null ? nom : "N/A") + '\'' +
                ", prenom='" + (prenom != null ? prenom : "N/A") + '\'' +
                ", email='" + (email != null ? email : "N/A") + '\'' +
                ", numeroTel='" + (numeroTel != null ? numeroTel : "N/A") + '\'' +
                ", role=" + (role != null ? role : "N/A") +
                ", statut=" + statut + // statut est un booléen, donc il n'y a pas de problème à l'afficher
                '}';
    }

 

}