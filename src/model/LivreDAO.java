package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {
    private String filePath;

    public LivreDAO(String filePath) {
        this.filePath = filePath;
        afficherLivresDisponibles();
    }
    public List<Livre> getAllLivres() {
        List<Livre> livres = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");  // Supposons que les données sont séparées par des points-virgules

                // Vérifiez que le nombre de champs est correct
                if (fields.length < 10) {
                    System.out.println("Ligne ignorée, nombre de champs insuffisant : " + line);
                    continue;  // Ignore cette ligne si le nombre de champs est insuffisant
                }

                try {
                    int id = Integer.parseInt(fields[0].trim()); // ID
                    String titre = fields[1].trim();  // Titre
                    String auteur = fields[2].trim(); // Auteur
                    String genre = fields[3].trim();  // Genre
                    int anneePublication = Integer.parseInt(fields[4].trim()); // Année de publication
                    String imageUrl = fields[5].trim(); // URL de l'image
                    String isbn = fields[6].trim(); // ISBN
                    String description = fields[7].trim(); // Description
                    String editeur = fields[8].trim(); // Éditeur
                    int totalExemplaires = Integer.parseInt(fields[9].trim()); // Nombre total d'exemplaires

                    // Ajout du livre à la liste si tous les champs sont valides
                    Livre livre = new Livre(id, titre, auteur, genre, anneePublication, imageUrl, isbn, description, editeur, totalExemplaires);
                    livres.add(livre);

                } catch (NumberFormatException e) {
                    System.out.println("Erreur de format dans la ligne : " + line);
                    continue;  // Ignore cette ligne si l'année ou l'ID est invalide
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return livres;
    }
    public void addLivre(Livre livre) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(livre.toString() + ";" + livre.isDisponible());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLivre(Livre livre) {
        List<Livre> livres = getAllLivres();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Livre l : livres) {
                if (l.getId() == livre.getId()) {
                    bw.write(livre.toString() + ";" + livre.isDisponible());
                } else {
                    bw.write(l.toString() + ";" + l.isDisponible());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLivre(int id) {
        List<Livre> livres = getAllLivres();  // Récupère tous les livres
        livres.removeIf(livre -> livre.getId() == id);  // Filtre les livres à supprimer

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Livre livre : livres) {
                bw.write(livre.toString() + ";" + livre.isDisponible());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Livre rechercherParID(int id) {
        List<Livre> livres = getAllLivres();
        for (Livre livre : livres) {
            if (livre.getId() == id) {
                return livre; // Retourne le livre si l'ID correspond
            }
        }
        return null; // Retourne null si aucun livre n'est trouvé
    }

    public void afficherLivres() {
        List<Livre> livres = getAllLivres();
        for (Livre livre : livres) {
            System.out.println("ID: " + livre.getId() + ", Titre: " + livre.getTitre());
        }
    }
    public void afficherLivresDisponibles() {
        List<Livre> livres = getAllLivres();
        System.out.println("Livres disponibles à l'emprunt :");
        for (Livre livre : livres) {
            if (livre.isDisponible()) { // Vérifie si le livre est disponible
                System.out.println("ID: " + livre.getId() + ", Titre: " + livre.getTitre());
            }
        }
    }
}
