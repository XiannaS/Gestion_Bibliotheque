package model;

import java.util.List;

public interface LivreInterface {
    void ajouterLivre(Livre livre) throws Exception;
    void supprimerLivre(String titre) throws Exception;
    void modifierLivre(String titre, Livre nouveauLivre) throws Exception;
    List<Livre> getLivres() throws Exception;
}
