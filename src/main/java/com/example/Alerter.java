package com.example;

import javax.swing.*;

/**
 * An interface for displaying alert messages.
 */
public interface Alerter {

    /**
     * Displays an error alert with the specified content.
     *
     * @param content the content text to be displayed in the error alert
     */
    static void showError(String content) {
        JOptionPane.showMessageDialog(null, content, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an information alert with the specified content.
     *
     * @param content the content text to be displayed in the information alert
     */
    static void showMessage(String content) {
        JOptionPane.showMessageDialog(null, content);
    }
}
