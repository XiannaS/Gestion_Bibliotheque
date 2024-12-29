package test;

import exception.InvalidUserDataException;
import exception.UserNotFoundException;
import model.User;
import model.Usermodel;
import org.junit.*;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class TestUser {
    private Usermodel userModel;
    private final String testCsvFile = "test_users.csv";

    @Before
    public void setUp() {
        // Initialisation du modèle avec un fichier de test
        userModel = new Usermodel(testCsvFile);
    }

    @After
    public void tearDown() {
        // Supprimer le fichier de test après les tests
        File file = new File(testCsvFile);
        if (file.exists()) {
            assertTrue("Échec de la suppression du fichier de test.", file.delete());
        }
    }

    @Test
    public void testAjouterUser_Success() throws InvalidUserDataException {
        User user = new User("1", "Alice", "alice@example.com");
        userModel.ajouterUser(user);

        List<User> users = userModel.getAllUsers();
        assertEquals("La taille de la liste des utilisateurs doit être 1.", 1, users.size());
        assertEquals("Le nom de l'utilisateur ajouté doit être Alice.", "Alice", users.get(0).getNom());
    }

    @Test(expected = InvalidUserDataException.class)
    public void testAjouterUser_DuplicateID_ThrowsException() throws InvalidUserDataException {
        User user = new User("1", "Bob", "bob@example.com");
        userModel.ajouterUser(user);
    }

    @Test
    public void testModifierUser_Success() throws InvalidUserDataException, UserNotFoundException {
        User user = new User("2", "Charlie", "charlie@example.com");
        userModel.ajouterUser(user);

        userModel.modifierUser("2", "Charlie Updated", "charlie.updated@example.com");
        User updatedUser = userModel.rechercherParID("2");

        assertEquals("Le nom de l'utilisateur doit être mis à jour.", "Charlie Updated", updatedUser.getNom());
        assertEquals("L'email de l'utilisateur doit être mis à jour.", "charlie.updated@example.com", updatedUser.getEmail());
    }

    @Test(expected = UserNotFoundException.class)
    public void testModifierUser_UserNotFound_ThrowsException() throws UserNotFoundException {
        userModel.modifierUser("99", "NonExistant", "nonexistent@example.com");
    }

    @Test
    public void testSupprimerUser_Success() throws InvalidUserDataException, UserNotFoundException {
        User user = new User("3", "Dave", "dave@example.com");
        userModel.ajouterUser(user);

        userModel.supprimerUser("3");
        try {
            userModel.rechercherParID("3");
            fail("Une exception UserNotFoundException aurait dû être levée.");
        } catch (UserNotFoundException e) {
            // Attendu
        }
    }

    @Test(expected = UserNotFoundException.class)
    public void testSupprimerUser_UserNotFound_ThrowsException() throws UserNotFoundException {
        userModel.supprimerUser("99");
    }

    @Test
    public void testTrierListeUsersParNom() throws InvalidUserDataException {
        User user1 = new User("4", "Eve", "eve@example.com");
        User user2 = new User("5", "Adam", "adam@example.com");
        userModel.ajouterUser(user1);
        userModel.ajouterUser(user2);

        userModel.trierListeUsersParNom();
        List<User> users = userModel.getAllUsers();

        assertEquals("Le premier utilisateur trié doit être Adam.", "Adam", users.get(0).getNom());
        assertEquals("Le deuxième utilisateur trié doit être Eve.", "Eve", users.get(1).getNom());
    }
}
