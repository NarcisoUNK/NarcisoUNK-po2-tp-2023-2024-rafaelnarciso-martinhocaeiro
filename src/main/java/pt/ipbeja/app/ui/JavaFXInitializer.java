package pt.ipbeja.app.ui;

import javafx.application.Platform;

/**
 * JavaFXInitializer class.
 * Ensures JavaFX is initialized before use.
 *
 * @version 30/05/2024
 * @authors Martinho Caeiro (23917) and Rafael Narciso (24473)
 */
public class JavaFXInitializer {
    private static boolean initialized = false;

    /**
     * Initializes JavaFX if it has not been initialized already.
     */
    public static synchronized void initialize() {
        if (!initialized) {
            Platform.startup(() -> {});
            initialized = true;
        }
    }
}
