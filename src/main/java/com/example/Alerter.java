package com.example;

import javafx.scene.control.Alert;

/**
 * An interface for displaying alert messages.
 */
public interface Alerter {

    /**
     * Displays an alert with the specified content, title, and type.
     *
     * @param content the content text to be displayed in the alert
     * @param title the title text to be displayed in the alert
     * @param type the type of the alert to be displayed (e.g., INFORMATION, WARNING, ERROR)
     */
    private static void show(String content, String title, Alert.AlertType type) {
        var alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an error alert with the specified content.
     *
     * @param content the content text to be displayed in the error alert
     */
    static void showError(String content) {
        show(content, "Error", Alert.AlertType.ERROR);
    }

    /**
     * Displays an information alert with the specified content.
     *
     * @param content the content text to be displayed in the information alert
     */
    static void showMessage(String content) {
        show(content, "Message", Alert.AlertType.INFORMATION);
    }
}
