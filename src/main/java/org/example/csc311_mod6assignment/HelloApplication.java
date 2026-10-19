
package org.example.csc311_mod6assignment;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlLocation = HelloApplication.class.getResource(
                "/org/example/csc311_mod6assignment/dashboard-view.fxml"
        );

        if (fxmlLocation == null) {
            throw new IllegalStateException(
                    "Cannot find dashboard-view.fxml. Check its location under src/main/resources."
            );
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Weather Data Analyzer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
