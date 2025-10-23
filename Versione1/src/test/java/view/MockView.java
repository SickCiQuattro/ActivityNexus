package view;

import java.util.LinkedList;
import java.util.Queue;

// Implementazione FAKE (Mock) della vista per i test automatici
public class MockView implements IView {

    // Coda per simulare gli input dell'utente
    private final Queue<String> inputs = new LinkedList<>();

    // Contatori per verificare che i metodi siano stati chiamati
    public int showMenuConfiguratoreCallCount = 0;
    public int showMenuFruitoreCallCount = 0;
    public String lastErrorMessage = null;
    public String lastSuccessMessage = null;

    // Metodo per "programmare" gli input che simuleremo
    public void addSimulatedInput(String input) {
        this.inputs.add(input);
    }

    @Override
    public String getInput(String prompt) {
        // Restituisce il prossimo input simulato
        String input = inputs.poll();
        // Se non ci sono più input, restituisce "0" per uscire/annullare
        return input != null ? input : "0";
    }

    @Override
    public String getPasswordInput(String prompt) {
        // Per i test, non c'è differenza con getInput
        String input = inputs.poll();
        // Se non ci sono più input, restituisce "0" per uscire/annullare
        return input != null ? input : "0";
    }

    @Override
    public boolean getConfirmation(String prompt) {
        // Simula la conferma (true/false)
        String input = inputs.poll();
        // Se non ci sono più input, restituisce false
        return input != null && "true".equalsIgnoreCase(input);
    }

    @Override
    public int getIntInput(String prompt, int min, int max) {
        // Simula l'input di un intero
        try {
            String input = inputs.poll();
            if (input == null)
                return 0; // Valore di default per uscire
            return Integer.parseInt(input);
        } catch (Exception e) {
            return 0; // Restituisce 0 invece di -1 per comportamento coerente
        }
    }

    @Override
    public double getDoubleInput(String prompt, double min, double max) {
        try {
            String input = inputs.poll();
            if (input == null)
                return 0.0; // Valore di default per uscire
            return Double.parseDouble(input);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ----- Metodi di Output (li registriamo per i test) -----

    @Override
    public void showErrorMessage(String message) {
        this.lastErrorMessage = message;
        // System.out.println("TEST_ERROR: " + message); // Utile per debug
    }

    @Override
    public void showSuccessMessage(String message) {
        this.lastSuccessMessage = message;
        // System.out.println("TEST_SUCCESS: " + message); // Utile per debug
    }

    @Override
    public void showMenuConfiguratore() {
        this.showMenuConfiguratoreCallCount++;
    }

    @Override
    public void showMenuFruitore() {
        this.showMenuFruitoreCallCount++;
    }

    // ----- Metodi che non ci interessano per ora -----
    @Override
    public void showMessage(String message) {
    }

    @Override
    public void showMessageWithDelay(String message, int delay) {
    }

    @Override
    public void showErrorMessageNoWait(String message) {
        this.lastErrorMessage = message;
    }

    @Override
    public void showSuccessMessageNoWait(String message) {
        this.lastSuccessMessage = message;
    }

    @Override
    public void showInfoMessage(String message) {
    }

    @Override
    public void showWarningMessage(String message) {
    }

    @Override
    public void showMenu() {
    }

    @Override
    public void pressEnterToContinue() {
    }

    @Override
    public void close() {
    }

    @Override
    public void displayShrek() {
    }

    @Override
    public void displayAmongUs() {
    }
}