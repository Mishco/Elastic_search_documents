package main;

import javafx.stage.Stage;

public class SavedStage {

    private Stage primaryStage;

    private SavedStage() {
    }

    private static class LazyHolder {
        static final SavedStage INSTANCE_STAGE = new SavedStage();
    }

    public static SavedStage getInstance() {
        return LazyHolder.INSTANCE_STAGE;
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }
}
