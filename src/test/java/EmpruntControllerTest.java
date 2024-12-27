package test.java;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controllers.EmpruntController;
import exception.EmpruntException;
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
    private String empruntFilePath = "C:/Eclipse/gestionbibli/src/main/resources/ressources/emprunt.csv";
    private String livreFilePath = "C:/Eclipse/gestionbibli/src/main/resources/ressources/books.csv";
    private String userFilePath = "C:/Eclipse/gestionbibli/src/main/resources/ressources/users.csv";

 
    @Before
    public void setUp() {
        EmpruntView empruntView = new EmpruntView(); // Créez une instance de la vue (ou mock si nécessaire)
        empruntController = new EmpruntController(empruntView, empruntFilePath, livreFilePath, userFilePath);
    }

    @Test
    public void testEmprunterLivre() {
        Livre livre = new Livre(1, "Titre", "Auteur", "Genre", 2021, "url", "isbn", "description", "editeur", 5);
        User user = new User("1", "Nom", "Prenom", "email", "numeroTel", "motDePasse", Role.MEMBRE, true);

        empruntController.emprunterLivre(livre, user);
        
        // Vérifiez que l'emprunt a été ajouté
        List<Emprunt> emprunts = empruntController.listerEmprunts();
        assertFalse(emprunts.isEmpty());
        assertEquals(1, emprunts.size());
        assertEquals(livre.getId(), emprunts.get(0).getLivreId());
        assertEquals(user.getId(), emprunts.get(0).getUserId());
    }

    @Test
    public void testRetournerLivre() {
        // Ajoutez un emprunt pour le test
        Livre livre = new Livre(1, "Titre", "Auteur", "Genre", 2021, "url", "isbn", "description", "editeur", 5);
        User user = new User("1", "Nom", "Prenom", "email", "numeroTel", "motDePasse", Role.MEMBRE, true);
        empruntController.emprunterLivre(livre, user);
        
        // Retournez le livre
        Emprunt emprunt = empruntController.listerEmprunts().get(0);
        empruntController.retournerLivre(emprunt.getId());
        
        // Vérifiez que l'emprunt a été marqué comme retourné
        Emprunt empruntRetourne = empruntController.getEntityById(String.valueOf(emprunt.getId()), "Emprunt");
        assertTrue(empruntRetourne.isRendu());
    }

    @Test
    public void testRenouvelerEmpruntDejaRetourne() throws EmpruntException {
        // Créer un emprunt et le retourner
        Livre livre = new Livre(1, "Titre", "Auteur", "Genre", 2021, "url", "isbn", "description", "editeur", 5);
        User user = new User("1", "Nom", "Prenom", "email", "numeroTel", "motDePasse", Role.MEMBRE, true);
        empruntController.emprunterLivre(livre, user);
        Emprunt emprunt = empruntController.getLastEmprunt();
        empruntController.retournerLivre(emprunt.getId());

        // Essayer de renouveler l'emprunt retourné
        Exception exception = assertThrows(EmpruntException.EmpruntDejaRetourneException.class, () -> {
            empruntController.renouvelerEmprunt(emprunt.getId());
        });

        String expectedMessage = "L'emprunt a déjà été retourné.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

  

    @Test
    public void testEmprunterAvecPenalite() {
        // Créer un emprunt avec une pénalité
        Livre livre = new Livre(1, "Titre", "Auteur", "Genre", 2021, "url", "isbn", "description", "editeur", 5);
        User user = new User("1", "Nom", "Prenom", "email", "numeroTel", "motDePasse", Role.MEMBRE, true);
        empruntController.emprunterLivre(livre, user);
        Emprunt emprunt = empruntController.getLastEmprunt();
        emprunt.retournerLivre(); // Simuler le retour
        emprunt.setPenalite(5); // Ajouter une pénalité

        // Essayer d'emprunter à nouveau
        Exception exception = assertThrows(Exception.class, () -> {
            empruntController.emprunterLivre(livre, user);
        });

        String expectedMessage = "L'utilisateur a des pénalités. Emprunt impossible.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}