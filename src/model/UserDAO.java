package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private String filePath;

    public UserDAO(String filePath) {
        this.filePath = filePath;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Vérifiez que nous avons au moins 7 colonnes
                 if (data.length >= 7) {
                    String id = data[0];
                    String nom = data.length > 1 ? data[1] : ""; // Valeur par défaut si manquante
                    String prenom = data.length > 2 ? data[2] : ""; // Valeur par défaut si manquante
                    String email = data.length > 3 ? data[3] : ""; // Valeur par défaut si manquante
                    String numeroTel = data.length > 4 ? data[4] : ""; // Valeur par défaut si manquante
                    String motDePasse = data.length > 5 ? data[5] : ""; // Valeur par défaut si manquante
                    Role role = data.length > 6 ? Role.valueOf(data[6].toUpperCase()) : Role.MEMBRE; // Valeur par défaut si manquante
                    boolean statut = data.length > 7 && Boolean.parseBoolean(data[7]); // Valeur par défaut si manquante
                    users.add(new User(id, nom, prenom, email, numeroTel, motDePasse, role, statut));
                } else {
                    System.err.println("Ligne ignorée (nombre de colonnes incorrect) : " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void addUser (User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = String.join(",", user.getId(), user.getNom(), user.getPrenom(), user.getEmail(),
                    user.getNumeroTel(), user.getMotDePasse(), user.getRole().name(), String.valueOf(user.isStatut()));
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUser (User user) {
        List<User> users = getAllUsers();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User  u : users) {
                if (u.getId().equals(user.getId())) {
                    bw.write(String.join(",", user.getId(), user.getNom(), user.getPrenom(), user.getEmail(),
                            user.getNumeroTel(), user.getMotDePasse(), user.getRole().name(), String.valueOf(user.isStatut())));
                } else {
                    bw.write(String.join(",", u.getId(), u.getNom(), u.getPrenom(), u.getEmail(),
                            u.getNumeroTel(), u.getMotDePasse(), u.getRole().name(), String.valueOf(u.isStatut())));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser (String id) {
        List<User> users = getAllUsers();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User  u : users) {
                if (!u.getId().equals(id)) {
                    bw.write(String.join(",", u.getId(), u.getNom(), u.getPrenom(), u.getEmail(),
                            u.getNumeroTel(), u.getMotDePasse(), u.getRole().name(), String.valueOf(u.isStatut())));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User rechercherParID(String id) {
        return getAllUsers().stream()
                .filter(user -> user.getId().equals(id)) // Comparaison des chaînes
                .findFirst()
                .orElse(null);
    }

}