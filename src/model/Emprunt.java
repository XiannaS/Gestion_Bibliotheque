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

    public String toCSV() {
        return id + ";" + livreId + ";" + userId + ";" + dateEmprunt + ";" +
               dateRetourPrevue + ";" + (dateRetourEffective != null ? dateRetourEffective : "") + ";" +
               rendu + ";" + penalite;
    }

    public static Emprunt fromCSV(String line) {
        String[] parts = line.split(";");
        try {
            int id = Integer.parseInt(parts[0]);
            int livreId = Integer.parseInt(parts[1]);
            String userId = parts[2];
            LocalDate dateEmprunt = LocalDate.parse(parts[3]);
            LocalDate dateRetourPrevue = LocalDate.parse(parts[4]);
            
            LocalDate dateRetourEffective = null;
            boolean rendu = false;
            int penalite = 0;

            if (parts.length > 5 && !parts[5].isEmpty()) {
                dateRetourEffective = LocalDate.parse(parts[5]);
            }
            if (parts.length > 6) {
                rendu = Boolean.parseBoolean(parts[6]);
            }
            if (parts.length > 7) {
                penalite = Integer.parseInt(parts[7]);
            }

            return new Emprunt(id, livreId, userId, dateEmprunt, dateRetourPrevue, dateRetourEffective, rendu, penalite);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            System.err.println("Ligne ignorée en raison d'une erreur de format : " + line);
            return null; // Ignorer les lignes incorrectes
        }
    }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }
	public void setPenalite(int penalite) {
		   this.penalite = penalite;
		
	}
}
