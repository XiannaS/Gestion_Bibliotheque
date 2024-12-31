package test.java;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controllers.EmpruntController;
import exception.UserException;
import model.Emprunt;
import model.EmpruntDAO;
import model.Livre;
import model.LivreDAO;
import model.Role;
import model.User;
import model.UserDAO;
import vue.EmpruntView;

public class EmpruntControllerTest {
    private EmpruntController empruntController;
    private EmpruntDAO empruntDAO;
    private UserDAO userDAO;
    private LivreDAO livreDAO;
    private EmpruntView empruntView;

    @Before
    public void setUp() {
        // Initialisation des DAO et de la vue
        empruntView = new EmpruntView();
        empruntDAO = new EmpruntDAO("src/resources/emprunt.csv");
        userDAO = new UserDAO("src/resources/users.csv");
        livreDAO = new LivreDAO("src/resources/books.csv");

        // Initialisation de l'EmpruntController
        empruntController = new EmpruntController(empruntView, "src/resources/emprunt.csv", "src/resources/books.csv", "src/resources/users.csv");
    }

    @Test
    public void testAjouterEmprunt() {
        // Créer un livre et un utilisateur pour le test
        Livre livre = new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943, "", "9780156012195", "Un conte philosophique", "Gallimard", 5);
        User user = new User("001", "Alice", "Dupont", "alice@example.com", "1234567890", "password", Role.MEMBRE, true);

        // Ajouter le livre et l'utilisateur dans les DAO
        livreDAO.addLivre(livre);
        // Ajouter l'utilisateur dans le DAO avec gestion d'exception
        try {
            userDAO.addUser (user);
        } catch (UserException e) {
            fail("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

        // Créer un emprunt
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), LocalDate.now().plusDays(14), null, false, 0);

        // Ajouter l'emprunt
        empruntController.ajouterEmprunt(emprunt);

        // Vérifier que l'emprunt a été ajouté
        List<Emprunt> emprunts = empruntDAO.listerEmprunts();
        assertEquals(1, emprunts.size());
        assertEquals(emprunt.getUserId(), emprunts.get(0).getUserId());
    }

    @Test
    public void testRetournerLivre() {
        // Créer un livre et un utilisateur pour le test
        Livre livre = new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943, "", "9780156012195", "Un conte philosophique", "Gallimard", 5);
        User user = new User("001", "Alice", "Dupont", "alice@example.com", "1234567890", "password", Role.MEMBRE, true);

        // Ajouter le livre et l'utilisateur dans les DAO
        livreDAO.addLivre(livre);
        // Ajouter l'utilisateur dans le DAO avec gestion d'exception
        try {
            userDAO.addUser (user);
        } catch (UserException e) {
            fail("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

        // Créer un emprunt
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), LocalDate.now().plusDays(14), null, false, 0);
        empruntController.ajouterEmprunt(emprunt);

        // Retourner le livre
        empruntController.retournerLivre(emprunt.getId());

        // Vérifier que le livre a été retourné
        Emprunt returnedEmprunt = empruntDAO.getEmpruntById(emprunt.getId());
        assertTrue(returnedEmprunt.isRendu());
    }

    @Test
    public void testRenouvelerEmprunt() {
        // Créer un livre et un utilisateur pour le test
        Livre livre = new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943, "", "9780156012195", "Un conte philosophique", "Gallimard", 5);
        User user = new User("001", "Alice", "Dupont", "alice@example.com", "1234567890", "password", Role.MEMBRE, true);

        // Ajouter le livre et l'utilisateur dans les DAO
        livreDAO.addLivre(livre);
        // Ajouter l'utilisateur dans le DAO avec gestion d'exception
        try {
            userDAO.addUser (user);
        } catch (UserException e) {
            fail("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

        // Créer un emprunt
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), LocalDate.now().plusDays(14), null, false, 0);
        empruntController.ajouterEmprunt(emprunt);

        // Renouveler l'emprunt
        empruntController.renouvelerEmprunt(emprunt.getId());

        // Vérifier que la date de retour prévue a été mise à jour
        Emprunt renewedEmprunt = empruntDAO.getEmpruntById(emprunt.getId());
        assertEquals(LocalDate.now().plusDays(28), renewedEmprunt.getDateRetourPrevue());
        assertEquals(1, renewedEmprunt.getNombreRenouvellements());
    }

    @Test
    public void testHasActiveEmprunts() {
        // Créer un livre et un utilisateur pour le test
        Livre livre = new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943, "", "9780156012195", "Un conte philosophique", "Gallimard", 5);
        User user = new User("001", "Alice", "Dupont", "alice@example.com", "1234567890", "password", Role.MEMBRE, true);

        // Ajouter le livre et l'utilisateur dans les DAO
        livreDAO.addLivre(livre);
         
        // Ajouter l'utilisateur dans le DAO avec gestion d'exception
        try {
            userDAO.addUser (user);
        } catch (UserException e) {
            fail("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

        // Créer un emprunt
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), LocalDate.now().plusDays(14), null, false, 0);
        empruntController.ajouterEmprunt(emprunt);

        // Vérifier que l'utilisateur a des emprunts actifs
        assertTrue(empruntController.hasActiveEmprunts(user.getId()));
    }

    @Test
    public void testSupprimerEmprunt() {
        // Créer un livre et un utilisateur pour le test
        Livre livre = new Livre(1, "Le Petit Prince", "Antoine de Saint-Exupéry", "Conte", 1943, "", "9780156012195", "Un conte philosophique", "Gallimard", 5);
        User user = new User("001", "Alice", "Dupont", "alice@example.com", "1234567890", "password", Role.MEMBRE, true);

        // Ajouter le livre et l'utilisateur dans les DAO
        livreDAO.addLivre(livre);
        // Ajouter l'utilisateur dans le DAO avec gestion d'exception
        try {
            userDAO.addUser (user);
        } catch (UserException e) {
            fail("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }

        // Créer un emprunt
        Emprunt emprunt = new Emprunt(0, livre.getId(), user.getId(), LocalDate.now(), LocalDate.now().plusDays(14), null, false, 0);
        empruntController.ajouterEmprunt(emprunt);

        // Supprimer l'emprunt
        empruntController.supprimerEmprunt(emprunt.getId());

        // Vérifier que l'emprunt a été supprimé
        assertNull(empruntDAO.getEmpruntById(emprunt.getId()));
    }
}