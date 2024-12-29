package model;

import exception.UserNotFoundException;
import exception.InvalidUserDataException;

import java.io.*;
import java.util.*;

public class Usermodel implements UsermodelInterface {
    private static final String Prénom = null;
	private static final String Numéro = null;
	private List<User> liste = new ArrayList<>();
    private String csvFileName;

    public Usermodel() {
        super();
    }

    public Usermodel(String csvFileName) {
        super();
        this.csvFileName = csvFileName;
        this.lireCSV();
    }

    @Override
    public void ajouterUser(User user) throws InvalidUserDataException {
        if (user.getId() == null || user.getId().isEmpty()) {
            throw new InvalidUserDataException("L'ID de l'utilisateur est invalide.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidUserDataException("L'email de l'utilisateur est invalide.");
        }
        try {
			if (rechercherParID(user.getId()) != null) {
			    throw new InvalidUserDataException("Un utilisateur avec cet ID existe déjà.");
			}
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidUserDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        liste.add(user);
        this.sauvegarderCSV();
    }

    @Override
    public void modifierUser(String id, String nouveauNom, String nouvelEmail) throws UserNotFoundException, InvalidUserDataException {
        User user = rechercherParID(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        if (nouveauNom == null || nouveauNom.isEmpty()) {
            throw new InvalidUserDataException("Le nom ne peut pas être vide.");
        }
        if (nouvelEmail == null || !nouvelEmail.contains("@")) {
            throw new InvalidUserDataException("L'email est invalide.");
        }
        user.setNom(nouveauNom);
        user.setEmail(nouvelEmail);
        this.sauvegarderCSV();
    }

    @Override
    public void supprimerUser(String id) throws UserNotFoundException {
        User user = rechercherParID(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        liste.remove(user);
        this.sauvegarderCSV();
    }

    @Override
    public void trierListeUsersParNom() {
        liste.sort(Comparator.comparing(User::getNom));
    }

    @Override
    public User rechercherParID(String id) throws UserNotFoundException {
        return liste.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(liste);
    }

    public void sauvegarderCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFileName))) {
            bw.write("id;nom;email");
            for (User user : liste) {
                bw.newLine();
                bw.write(user.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lireCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            br.readLine(); // Ignorer le header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String id = parts[0];
                String nom = parts[1];
                String email = parts[2];
                User user = new User(id, nom, email );
                liste.add(user);
            }
        } catch (IOException e) {
            System.err.println("Le fichier CSV n'existe pas ou est invalide.");
        }
    }
}

