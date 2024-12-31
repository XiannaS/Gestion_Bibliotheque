package test;

import model.*;
import vue.EmpruntView;
import vue.LivreView;
import controllers.*;
import exception.UserException;
import java.time.LocalDate;

public class TestEmpruntAvecPenalites {
    public static void main(String[] args) {
        try {
            // Créer les objets DAO
            LivreDAO livreDAO = new LivreDAO("src/resources/books.csv");
            EmpruntDAO empruntDAO = new EmpruntDAO("src/resources/emprunt.csv");
            UserDAO userDAO = new UserDAO("src/resources/users.csv");

            // Créer les utilisateurs
            User user1 = new User("u001", "Alice", "Dupont", "alice@example.com", "1234567890", "password",Role.MEMBRE, true); // Utilisateur actif
            User user2 = new User("u002", "Bob", "Lemoine", "bob@example.com", "9876543210", "password", Role.MEMBRE, false); // Utilisateur inactif
            User user3 = new User("u003", "Charlie", "Durand", "charlie@example.com", "4561237890", "password", Role.MEMBRE, true); // Utilisateur actif

            // Ajouter les utilisateurs au UserDAO
          //  userDAO.addUser(user1);
           // userDAO.addUser(user2);
          //  userDAO.addUser(user3);

            // Créer les livres et les ajouter à livreDAO
            Livre livre1 = new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943, "", "9780156012195", "Un conte philosophique", "Gallimard", 5);
            livreDAO.addLivre(livre1);

            // Créer les contrôleurs
            LivreController livreController = new LivreController(new LivreView(), "src/resources/books.csv", new EmpruntController(new EmpruntView(), "emprunts.csv", "livres.csv", "users.csv"));

            // 3. Tester l'emprunt de livre avec un utilisateur actif (Alice)
            System.out.println("Tentative d'emprunt de livre...");
            try {
                livreController.emprunterLivre();  // Appel à la méthode dans LivreController pour Alice
                System.out.println("Emprunt effectué avec succès.");
            } catch (Exception e) {
                System.out.println("Erreur lors de l'emprunt du livre : " + e.getMessage());
            }

            // 4. Tentative d'emprunt par un utilisateur inactif (Bob)
            System.out.println("Tentative d'emprunt par un utilisateur inactif (Bob)...");
            try {
                livreController.emprunterLivre();  // Passer l'ID de Bob
            } catch (Exception e) {  // Attraper une exception générale
                System.out.println("Erreur : " + e.getMessage());  // Bob est inactif et donc l'emprunt échoue
            }
            // 5. Tentative d'emprunt par un utilisateur actif (Charlie)
            System.out.println("Tentative d'emprunt de livre par un autre utilisateur actif (Charlie)...");
            try {
                livreController.emprunterLivre();  // Appel à la méthode dans LivreController pour Charlie
                System.out.println("Emprunt effectué avec succès.");
            } catch (Exception e) {
                System.out.println("Erreur lors de l'emprunt du livre : " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

