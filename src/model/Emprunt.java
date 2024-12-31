package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {
    private int id;
    private int livreId;
    private String userId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private boolean rendu;
    private int penalite;
    private int nombreRenouvellements;

    // Constructeur
    public Emprunt(int id, int livreId, String userId, LocalDate dateEmprunt, LocalDate dateRetourPrevue, LocalDate dateRetourEffective, boolean rendu, int penalite) {
        this.id = id;
        this.livreId = livreId;
        this.userId = userId;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.rendu = rendu;
        this.penalite = penalite;
        this.nombreRenouvellements = 0;  // Initialiser à zéro
    }

    // Getter pour le nombre de renouvellements
    public int getNombreRenouvellements() {
        return nombreRenouvellements;
    }

    // Setter pour le nombre de renouvellements
    public void setNombreRenouvellements(int nombreRenouvellements) {
        this.nombreRenouvellements = nombreRenouvellements;
    }

    // Méthode pour vérifier si l'emprunt a déjà été renouvelé une fois
    public boolean peutEtreRenouvele() {
        return nombreRenouvellements < 1;
    }

    // Méthode pour retourner le livre et calculer la pénalité en cas de retard
    public void retournerLivre() {
        this.dateRetourEffective = LocalDate.now();
        this.rendu = true;
        // Calcul du retard si le livre est retourné en retard
        long retard = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourEffective);
        if (retard > 0) {
            this.penalite = (int) retard * 3;
        } else {
            this.penalite = 0;
        }
    }

    // Calculer le nombre de jours de retard
    public long getJoursDeRetard() {
        if (dateRetourEffective != null && dateRetourEffective.isAfter(dateRetourPrevue)) {
            return ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourEffective);
        }
        return 0;
    }

    // Méthode pour calculer la pénalité
    public int calculerPenalite() {
        long joursRetard = getJoursDeRetard();
        if (joursRetard > 0) {
            penalite = (int) joursRetard * 3;  // Calculer la pénalité en fonction des jours de retard
        } else {
            penalite = 0;  // Aucune pénalité si pas de retard
        }
        return penalite;
    }


    // Vérifier si l'emprunt est en retard
    public boolean isRetard() {
        return dateRetourEffective != null && dateRetourEffective.isAfter(dateRetourPrevue);
    }

    // Convertir l'objet en format CSV
    public String toCSV() {
        return id + ";" + livreId + ";" + userId + ";" + dateEmprunt + ";" +
               dateRetourPrevue + ";" + (dateRetourEffective != null ? dateRetourEffective : "") + ";" +
               rendu + ";" + penalite + ";" + nombreRenouvellements;  // Ajout du nombre de renouvellements
    }


    // Getters et setters existants
    public int getId() { return id; }
    public int getLivreId() { return livreId; }
    public String getUserId() { return userId; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public boolean isRendu() { return rendu; }
    public int getPenalite() { return penalite; }

    // Setters
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public void setPenalite(int penalite) {
        this.penalite = penalite;
    }

	public void setId(int id) {
		 this.id = id;
		
	}

	public void incrementerNombreRenouvellements() {
	    this.nombreRenouvellements++;
	}

	 
}
