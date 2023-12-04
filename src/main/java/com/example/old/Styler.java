package com.example.old;

import javafx.scene.Node;

/**
 * Interface for adding styles to JavaFX nodes.
 */
public interface Styler {

    private static void addStyle(Node n, String style) {
        n.setStyle(n.getStyle() + ";" + style);
    }

    static void setTextColorRed(Node n) {
        addStyle(n, "-fx-text-fill: red");
    }

    static void setTextColorGreen(Node n) {
        addStyle(n, "-fx-text-fill: green");
    }

    static void setTextColorBlue(Node n) {
        addStyle(n, "-fx-text-fill: blue");
    }

    static void setTextColorToResult(Node n, boolean success) {
        if (success) setTextColorGreen(n);
        else setTextColorRed(n);
    }

}
