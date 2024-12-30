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
    }
    // Ajout du setter pour l'ID
    public void setId(int id) {
        this.id = id;
    }
    public int getNombreRenouvellements() {
        return nombreRenouvellements;
    }

    public void setNombreRenouvellements(int nombreRenouvellements) {
        this.nombreRenouvellements = nombreRenouvellements;
    }

    // Méthode pour vérifier si l'emprunt a déjà été renouvelé une fois
    public boolean peutEtreRenouvele() {
        return nombreRenouvellements < 1;
    }
    public boolean isReturned() {
        return rendu; // Retourne true si le livre a été retourné, false sinon
    }

    public void setReturned(boolean returned) {
        this.rendu = returned; // Setter pour le statut de retour
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

    public void retournerLivre() {
        this.dateRetourEffective = LocalDate.now();
        this.rendu = true;

        long retard = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourEffective);
        if (retard > 0) {
            this.penalite = (int) retard * 3;
        } else {
            this.penalite = 0;
        }
    }
    
    public int calculerPenalite() {
        if (dateRetourEffective != null && dateRetourEffective.isAfter(dateRetourPrevue)) {
        	long joursRetard = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourEffective);
        	penalite = (int) (joursRetard * 1); // 1   par jour de retard
  
        }
        return penalite;
    }

    
    public boolean isRetard() {
        return dateRetourEffective != null && dateRetourEffective.isAfter(dateRetourPrevue);
    }

    public String toCSV() {
        return id + ";" + livreId + ";" + userId + ";" + dateEmprunt + ";" +
               dateRetourPrevue + ";" + (dateRetourEffective != null ? dateRetourEffective : "") + ";" +
               rendu + ";" + penalite;
    }


    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }
	public void setPenalite(int penalite) {
		   this.penalite = penalite;
		
	}
}
