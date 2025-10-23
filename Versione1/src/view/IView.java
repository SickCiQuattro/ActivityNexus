package view;

// Interfaccia per la Vista, per disaccoppiare Controller e ConsoleView
public interface IView {
    String getInput(String prompt);

    String getPasswordInput(String prompt);

    boolean getConfirmation(String prompt);

    int getIntInput(String prompt, int min, int max);

    double getDoubleInput(String prompt, double min, double max);

    void showMessage(String message);

    void showMessageWithDelay(String message, int delay);

    void showErrorMessage(String message);

    void showErrorMessageNoWait(String message);

    void showSuccessMessage(String message);

    void showSuccessMessageNoWait(String message);

    void showInfoMessage(String message);

    void showWarningMessage(String message);

    void showMenu();

    void showMenuConfiguratore();

    void showMenuFruitore();

    void pressEnterToContinue();

    void close();

    void displayShrek();

    void displayAmongUs();
}