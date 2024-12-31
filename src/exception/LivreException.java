package exception;

public class LivreException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public LivreException(String message) {
        super(message);
    }

  

    // Exception pour l'année de publication invalide
    public static class InvalidYearException extends LivreException {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InvalidYearException(String message) {
            super(message);
        }
    }

 

    // Exception pour les livres non disponibles
    public static class LivreNonDisponibleException extends LivreException {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LivreNonDisponibleException(String message) {
            super(message);
        }
    }
}
