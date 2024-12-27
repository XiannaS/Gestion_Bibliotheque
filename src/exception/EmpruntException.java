package exception;

public class EmpruntException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmpruntException(String message) {
        super(message);
    }

    public static class EmpruntDejaRetourneException extends EmpruntException {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EmpruntDejaRetourneException(String message) {
            super(message);
        }
    }
    
    public static class EmpruntAvecPenaliteException extends EmpruntException {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EmpruntAvecPenaliteException(String message) {
            super(message);
        }
    }
}
