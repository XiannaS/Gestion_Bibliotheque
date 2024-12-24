package model;

import controllers.LivreController;
import controllers.UserController;
import controllers.EmpruntController;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dashboard {
    private LivreController livreController;
    private UserController userController;
    private EmpruntController empruntController;

    public Dashboard(LivreController livreController, UserController userController, EmpruntController empruntController) {
        this.livreController = livreController;
        this.userController = userController;
        this.empruntController = empruntController;
    }

    public void afficherStatistiques() {
        System.out.println("=== Tableau de Bord ===");
        System.out.println("Total des livres : " + getTotalLivres());
        System.out.println("Total des utilisateurs : " + getTotalUtilisateurs());
        System.out.println("Total des emprunts : " + getTotalEmprunts());
        System.out.println("Livres disponibles : " + getLivresDisponibles());
        System.out.println("Livres non disponibles : " + getLivresNonDisponibles());
        System.out.println("Livres les plus empruntés : " + getLivresLesPlusEmpruntes());
        System.out.println("Utilisateurs les plus actifs : " + getUtilisateursLesPlusActifs());
        System.out.println("Statistiques par genre : " + getStatistiquesParGenre());
    }

    private int getTotalLivres() {
        return livreController.getAllLivres().size();
    }

    private int getTotalUtilisateurs() {
        return userController.getAllUsers().size();
    }

    private int getTotalEmprunts() {
        return empruntController.listerEmprunts().size();
    }

    private int getLivresDisponibles() {
        return (int) livreController.getAllLivres().stream().filter(Livre::isDisponible).count();
    }

    private int getLivresNonDisponibles() {
        return (int) livreController.getAllLivres().stream().filter(livre -> !livre.isDisponible()).count();
    }

    private List<Livre> getLivresLesPlusEmpruntes() {
        // Implémentez la logique pour récupérer les livres les plus empruntés
        return empruntController.listerEmprunts().stream()
                .collect(Collectors.groupingBy(Emprunt::getLivreId, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(e -> empruntController.getLivreById(e.getKey()))
                .collect(Collectors.toList());
    }

    private List<User> getUtilisateursLesPlusActifs() {
        // Implémentez la logique pour récupérer les utilisateurs les plus actifs
        return empruntController.listerEmprunts().stream()
                .collect(Collectors.groupingBy(Emprunt::getUserId, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(e -> empruntController.getUserById(e.getKey()))
                .collect(Collectors.toList());
    }

    private Map<String, Long> getStatistiquesParGenre() {
        return livreController.getAllLivres().stream()
                .collect(Collectors.groupingBy(Livre::getGenre, Collectors.counting()));
    }
}