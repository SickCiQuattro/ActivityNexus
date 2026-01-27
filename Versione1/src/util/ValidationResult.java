package util;

/**
 * Rappresenta il risultato di un'operazione di validazione.
 * <p>
 * Questa classe implementa il pattern "Introduce Parameter Object" per
 * incapsulare il risultato di una validazione, combinando:
 * <ul>
 * <li>Un flag booleano che indica se la validazione è passata</li>
 * <li>Un messaggio di errore opzionale in caso di fallimento</li>
 * </ul>
 * <p>
 * <b>REFACTORING APPLICATO:</b> Introduce Parameter Object
 * <p>
 * Invece di restituire un semplice booleano e gestire i messaggi di errore
 * separatamente, questa classe permette di restituire entrambe le informazioni
 * in modo coeso, migliorando la leggibilità e la manutenibilità del codice.
 * <p>
 *
 * @see controller.Controller#validaNomeComprensorio()
 */
public class ValidationResult {

    /** Indica se la validazione è passata con successo */
    private final boolean valid;
    /** Messaggio di errore (null se la validazione è passata) */
    private final String errorMessage;

    /**
     * Costruttore privato per forzare l'uso dei factory method.
     *
     * @param valid        true se la validazione è passata
     * @param errorMessage messaggio di errore (null se valid è true)
     */
    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    /**
     * Crea un risultato di validazione positivo (successo).
     *
     * @return un ValidationResult che indica successo
     *
     * @post result.isValid() == true
     * @post result.getErrorMessage() == null
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    /**
     * Crea un risultato di validazione negativo (errore) con messaggio.
     *
     * @param message il messaggio che descrive l'errore di validazione
     * @return un ValidationResult che indica fallimento con il messaggio
     *         specificato
     *
     * @pre message != null
     * @post result.isValid() == false
     * @post result.getErrorMessage().equals(message)
     */
    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }

    /**
     * Verifica se la validazione è passata con successo.
     *
     * @return true se la validazione è passata, false altrimenti
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Restituisce il messaggio di errore della validazione.
     *
     * @return il messaggio di errore, o null se la validazione è passata
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Restituisce una rappresentazione testuale del risultato.
     *
     * @return stringa che descrive il risultato della validazione
     */
    @Override
    public String toString() {
        if (valid) {
            return "ValidationResult[VALID]";
        } else {
            return "ValidationResult[INVALID: " + errorMessage + "]";
        }
    }
}