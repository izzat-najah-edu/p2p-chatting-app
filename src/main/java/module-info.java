module p2p.chatting.app {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;

    exports com.example;
    exports com.example.old.fxml;
    opens com.example.old.fxml to javafx.fxml;
    exports com.example.old.net;
    exports com.example.old;
}