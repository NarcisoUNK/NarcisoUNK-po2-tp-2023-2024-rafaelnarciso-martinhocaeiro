package pt.ipbeja.app;

import javafx.application.Platform;

public class JavaFXInitializer {
    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (!initialized) {
            Platform.startup(() -> {});
            initialized = true;
        }
    }
}
