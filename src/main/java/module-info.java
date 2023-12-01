module p2p.chatting.app {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;

    exports com.example;
    exports com.example.fxml;
    opens com.example.fxml to javafx.fxml;
    exports com.example.net;
}