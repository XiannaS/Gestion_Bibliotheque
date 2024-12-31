package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import exception.UserException;
public class UserDAO {
    private String filePath;
    private List<User> users;

    public UserDAO(String filePath) {
        this.filePath = filePath;
        this.users = new ArrayList<>();
    }

    public void addUser(User user) throws UserException {
        if (user.getNom().isEmpty() || user.getPrenom().isEmpty() || user.getEmail().isEmpty() || user.getNumeroTel().isEmpty()) {
            throw new UserException("Tous les champs doivent être remplis.");
        }

        if (getUserById(user.getId()) != null) {
            throw new UserException("L'ID de l'utilisateur existe déjà.");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new UserException("L'email n'est pas valide.");
        }

        if (getUserByEmail(user.getEmail()) != null) {
            throw new UserException("L'email est déjà utilisé.");
        }

        if (!isValidPhoneNumber(user.getNumeroTel())) {
            throw new UserException("Le numéro de téléphone n'est pas valide.");
        }

        if (getUserByPhoneNumber(user.getNumeroTel()) != null) {
            throw new UserException("Le numéro de téléphone est déjà utilisé.");
        }

        users.add(user);
        saveUserToFile(user);
    }

    private void saveUserToFile(User user) throws UserException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            if (user.getRole() == null) {
                throw new UserException("Le rôle de l'utilisateur est invalide.");
            }
            bw.write(String.join(",", user.getId(), user.getNom(), user.getPrenom(), user.getEmail(),
                    user.getNumeroTel(), user.getMotDePasse() == null ? "" : user.getMotDePasse(),
                    user.getRole().name(), String.valueOf(user.isStatut())));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour un utilisateur
    public void updateUser(User user) throws UserException {
        List<User> users = getAllUsers();
        boolean userFound = false;

        // Trouver et mettre à jour l'utilisateur dans la liste
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);  // Remplacer l'ancien utilisateur par le nouveau
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            throw new UserException("Utilisateur non trouvé dans le fichier.");
        }

        // Mettre à jour le fichier avec les utilisateurs modifiés
        try {
            updateFileWithUsers(users);  // Cette méthode met à jour tout le fichier
        } catch (IOException e) {
            throw new UserException("Erreur lors de la mise à jour du fichier : " + e.getMessage());
        }
    }


    public void deleteUser(String id) throws IOException {
        List<User> users = getAllUsers();
        users.removeIf(user -> user.getId().equals(id));
        updateFileWithUsers(users);
    }

    private void updateFileWithUsers(List<User> users) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (User  u : users) {
                bw.write(String.join(",", u.getId(), u.getNom(), u.getPrenom(), u.getEmail(),
                        u.getNumeroTel(), u.getMotDePasse(), u.getRole().name(), String.valueOf(u.isStatut())));
                bw.newLine();
            }
        
    }
    }

    public User rechercherParID(String id) {
        return getAllUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    User user = new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5],
                            Role.valueOf(parts[6]), Boolean.parseBoolean(parts[7]));
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        return phoneNumber.matches(phoneRegex);
    }

    private User getUserByEmail(String email) {
        return getAllUsers().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
 // Dans la classe UserDAO

    public List<User> rechercherParCritere(String query) {
        List<User> filteredUsers = new ArrayList<>();
        
        // Supposons que vous ayez une liste d'utilisateurs en mémoire ou une base de données
        // Vous pouvez filtrer en fonction du nom, prénom, email, etc.
        for (User user : users) {
            // Exemple de recherche dans les champs nom, prénom, email
            if (user.getNom().toLowerCase().contains(query.toLowerCase()) ||
                user.getPrenom().toLowerCase().contains(query.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        
        return filteredUsers;
    }

    private User getUserByPhoneNumber(String phoneNumber) {
        return getAllUsers().stream()
                .filter(user -> user.getNumeroTel().equals(phoneNumber))
                .findFirst()
                .orElse(null);
    }

    public User getUserById(String id) {
        return getAllUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
