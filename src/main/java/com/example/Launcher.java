package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * A class representing the Launcher for the Client Chat application.
 *
 * <p>
 * This class extends the JavaFX Application class and provides the entry point for the application.
 * It defines the start method, which is called when the application is launched.
 * </p>
 *
 * <p>
 * The Launcher initializes the JavaFX stage, loads the FXML file for the client chat user interface,
 * sets up the stage with the loaded scene, and displays the stage.
 * </p>
 *
 * <p>
 * Usage:
 * To launch the Client Chat application, call the main method of the Launcher class.
 * The start method is then invoked, which performs the necessary setup and displays the user interface.
 * </p>
 *
 * @see Application
 */
public class Launcher extends Application {

    /**
     * This method is called when the application is starting up.
     * It sets up the main stage, loads the fxml layout file, and displays the stage.
     *
     * @param stage The main stage of the application
     * @throws IOException If there is an error loading the fxml file
     */
    @Override
    public void start(Stage stage) throws IOException {
        var loader = new FXMLLoader(Launcher.class.getResource("fxml/client_chat.fxml"));
        var scene = new Scene(loader.load());
        stage.setTitle("Client Chat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}