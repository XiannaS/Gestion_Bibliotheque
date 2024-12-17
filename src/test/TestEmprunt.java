package test;

import controllers.EmpruntController;
import controllers.LivreController;
import controllers.UserController;
import model.Livre;
import model.Role;
import model.User;
import model.Emprunt;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestEmprunt {
    private static List<Emprunt> emprunts = new ArrayList<>();

    public static void main(String[] args) {
        // Chemins vers vos fichiers CSV
        String csvFileEmprunts = "C:/Eclipse/gestionbibli/src/main/resources/ressources/emprunt.csv"; 
        String csvFileLivres = "C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv"; 
        String csvFileUsers = "C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv"; 

        // Créer les contrôleurs
        UserController userController = new UserController(csvFileUsers);
        LivreController livreController = new LivreController(csvFileLivres);
        EmpruntController empruntController = new EmpruntController(csvFileEmprunts, csvFileLivres, csvFileUsers);

        // Ajouter des livres et utilisateurs pour les tests
        ajouterLivresTest(livreController);
        ajouterUtilisateursTest(userController);
        ajouterEmpruntsTest(); // Si vous avez une méthode pour ajouter des emprunts à l'instance

        // ID de l'utilisateur et du livre
        String userId = "1"; // ID utilisateur
        int livreId = 1; // ID livre

        // Appeler la méthode pour emprunter le livre
        emprunterLivre(empruntController, livreId, userId);
    }

    public static void ajouterLivresTest(LivreController livreController) {
        livreController.addLivre(new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Fiction", 1943, "", true));
        livreController.addLivre(new Livre(2, "1984", "George Orwell", "Dystopie", 1949, "", true));
        livreController.addLivre(new Livre(3, "Moby Dick", "Herman Melville", "Aventure", 1851, "", true));
        livreController.addLivre(new Livre(4, "To Kill a Mockingbird", "Harper Lee", "Fiction", 1960, "", true));
    }

    public static void ajouterUtilisateursTest(UserController userController) {
        userController.addUser (new User("1", "Alice", "Dupont", "alice@example.com", "0123456789", "", Role.MEMBRE, true));
        userController.addUser (new User("2", "Bob", "Martin", "bob@example.com", "0987654321", "",  Role.MEMBRE, true));
        userController.addUser (new User("3", "Charlie", "Durand", "charlie@example.com", "0147258369", "",  Role.MEMBRE, true));
    }

    public static void ajouterEmpruntsTest() {
        emprunts.add(new Emprunt(1, 1, "1734435747957", LocalDate.now(), LocalDate.now().plusDays(14), null, false, 0));
        emprunts.add(new Emprunt(2, 4, "1734435747957", LocalDate.now().minusDays(10), LocalDate.now().minusDays(3), LocalDate.now().minusDays(2), true, 5));
        emprunts.add(new Emprunt(3, 3, "1734346028497", LocalDate.now().minusDays(20), LocalDate.now().minusDays(7), LocalDate.now().minusDays(6), true, 10));
    }

    public static void emprunterLivre(EmpruntController empruntController, int livreId, String userId) {
        // Récupérer le livre par ID
        Livre livre = empruntController.getLivreById(livreId);
        
        // Vérifier si le livre existe
        if (livre == null) {
            System.out.println("Livre introuvable.");
            return;
        }

        // Vérifier si le livre est disponible
        if (!livre.isDisponible()) {
            System.out.println("Le livre est déjà emprunté.");
            return;
        }

        // Récupérer l'utilisateur par ID
        User user = empruntController.getUserById (userId); // Assurez-vous que cette méthode existe

        // Vérifier si l'utilisateur existe
        if (user == null) {
            System.out.println("Utilisateur introuvable.");
            return;
        }

        // Emprunter le livre
        empruntController.emprunterLivre(livre, user); // Passer l'objet User
        System.out.println("Livre emprunté avec succès : " + livre.getTitre());
    }

}