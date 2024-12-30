package exception;

public class LivreException extends Exception {
    public LivreException(String message) {
        super(message);
    }

  

    // Exception pour l'année de publication invalide
    public static class InvalidYearException extends LivreException {
        public InvalidYearException(String message) {
            super(message);
        }
    }

 

    // Exception pour les livres non disponibles
    public static class LivreNonDisponibleException extends LivreException {
        public LivreNonDisponibleException(String message) {
            super(message);
        }
    }
}
