package model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class EmpruntDAO {
    private List<Emprunt> emprunts = new ArrayList<>();
    private String csvFileName;
    private int nextId = 1; // Compteur pour les IDs d'emprunt

    public EmpruntDAO(String csvFileName) {
        this.csvFileName = csvFileName;
        lireCSV();
    }

    // Ajouter un nouvel emprunt
    public void ajouterEmprunt(Emprunt emprunt) {
        emprunt.setId(nextId++);
        emprunts.add(emprunt);
        sauvegarderCSV();
    }

    // Retourner un livre par ID d'emprunt
    public void retournerLivre(int empruntId) {
        for (Emprunt emprunt : emprunts) {
            if (emprunt.getId() == empruntId && !emprunt.isRendu()) {
                emprunt.retournerLivre();
                sauvegarderCSV();
                return;
            }
        }
        throw new IllegalArgumentException("Emprunt non trouvé ou déjà retourné.");
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
    // Lister les emprunts
    public List<Emprunt> listerEmprunts() {
        return new ArrayList<>(emprunts);
    }

    // Lire les emprunts depuis le CSV
    private void lireCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            String line;
            br.readLine(); // Ignorer l'en-tête
            while ((line = br.readLine()) != null) {
                Emprunt emprunt = fromCSV(line);
                if (emprunt != null) { // Ajouter uniquement les emprunts valides
                    emprunts.add(emprunt);
                }
            }
            nextId = emprunts.stream().mapToInt(Emprunt::getId).max().orElse(0) + 1;
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des emprunts : " + e.getMessage());
        }
    }


    // Sauvegarder les emprunts dans le CSV
    private void sauvegarderCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFileName))) {
            bw.write("id;livreId;userId;dateEmprunt;dateRetourPrevue;dateRetourEffective;rendu;penalite");
            bw.newLine();
            for (Emprunt emprunt : emprunts) {
                bw.write(emprunt.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des emprunts : " + e.getMessage());
        }
    }
    public Emprunt getEmpruntById(int empruntId) {
        return emprunts.stream()
                .filter(e -> e.getId() == empruntId)
                .findFirst()
                .orElse(null);
    }
    
    public void updateEmprunt(Emprunt emprunt) {
        for (int i = 0; i < emprunts.size(); i++) {
            if (emprunts.get(i).getId() == emprunt.getId()) {
                emprunts.set(i, emprunt); // Remplace l'emprunt existant
                sauvegarderCSV(); // Sauvegarde les modifications dans le fichier CSV
                return;
            }
        }
        throw new IllegalArgumentException("Emprunt non trouvé pour mise à jour.");
    }
 // Supprimer un emprunt par ID
    public void supprimerEmprunt(int empruntId) {
        emprunts.removeIf(emprunt -> emprunt.getId() == empruntId);
        sauvegarderCSV(); // Sauvegarde les modifications dans le fichier CSV
    }
 // Renouveler un emprunt
    public void renouvelerEmprunt(int empruntId) {
        for (Emprunt emprunt : emprunts) {
            if (emprunt.getId() == empruntId && !emprunt.isRendu()) {
                if (emprunt.getNombreRenouvellements() >= 1) {
                    throw new IllegalArgumentException("Renouvellement déjà effectué.");
                }

                LocalDate nouvelleDateRetour = emprunt.getDateRetourPrevue().plusDays(14); // Renouvelle de 14 jours
                emprunt.setDateRetourPrevue(nouvelleDateRetour);
                emprunt.setNombreRenouvellements(emprunt.getNombreRenouvellements() + 1);
                sauvegarderCSV(); // Sauvegarde les modifications dans le fichier CSV
                return;
            }
        }
        throw new IllegalArgumentException("Emprunt non trouvé ou déjà retourné.");
    }

    public void supprimerTousLesEmprunts() throws IOException {
        int choice = JOptionPane.showConfirmDialog(null, 
            "Êtes-vous sûr de vouloir supprimer tous les emprunts ?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            emprunts.clear();
            nextId = 1; // Réinitialiser l'ID pour le prochain emprunt
            sauvegarderCSV(); // Sauvegarder les modifications dans le fichier CSV
        }
    }

}
