package exception;

public class EmpruntException extends Exception {

    // Constructeur avec message d'erreur
    public EmpruntException(String message) {
        super(message);
    }

    // Constructeur avec message d'erreur et exception causale
    public EmpruntException(String message, Throwable cause) {
        super(message, cause);
    }

    // Exception spécifique pour un livre indisponible
    public static class LivreIndisponibleException extends EmpruntException {
        public LivreIndisponibleException(String message) {
            super(message);
        }
    }

    // Exception spécifique pour un utilisateur inactif
    public static class UtilisateurInactifException extends EmpruntException {
        public UtilisateurInactifException(String message) {
            super(message);
        }
    }

    // Exception spécifique pour un livre déjà emprunté
    public static class LivreDejaEmprunteException extends EmpruntException {
        public LivreDejaEmprunteException(String message) {
            super(message);
        }
    }

    // Exception spécifique pour un emprunt déjà retourné
    public static class EmpruntDejaRetourneException extends EmpruntException {
        public EmpruntDejaRetourneException(String message) {
            super(message);
        }
    }

    // Exception spécifique pour une pénalité existante lors du renouvellement
    public static class PenaliteExistanteException extends EmpruntException {
        public PenaliteExistanteException(String message) {
            super(message);
        }
    }
    public static class RenouvellementNonAutoriseException extends Exception {
        public RenouvellementNonAutoriseException(String message) {
            super(message);
        }
    }
}
