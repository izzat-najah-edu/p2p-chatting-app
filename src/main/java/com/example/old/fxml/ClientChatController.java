package com.example.old.fxml;

import com.example.Alerter;
import com.example.old.Styler;
import com.example.old.net.ChatListener;
import com.example.Message;
import com.example.old.net.NetworkUtility;
import com.example.old.net.UDPChatController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The ClientChatController class is responsible for managing the user interface and handling user interactions
 * in the client chat application.
 */
public class ClientChatController
        implements Initializable, ChatListener {

    @FXML
    private AnchorPane clientChatRoot;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private ComboBox<?> availableInterfacesComboBox;

    @FXML
    private ListView<Label> messagesList;

    @FXML
    private ListView<String> onlineUsersList;

    @FXML
    private TextField
            usernameTextField,
            statusTextField,
            serverIPTextField,
            serverPortTextField,
            localIPTextField,
            localPortTextField,
            remoteIPTextField,
            remotePortTextField;

    @FXML
    private Button
            loginButton,
            logoutButton,
            sendButton,
            testButton,
            useLocalPortButton,
            useRemoteIPButton,
            useRemotePortButton;

    UDPChatController udpChatController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusTextField.setEditable(false);
        localIPTextField.setEditable(false);
        useLocalPortButton.setOnAction(e -> useLocalPort());
        useRemoteIPButton.setOnAction(e -> useRemoteIP());
        useRemotePortButton.setOnAction(e -> useRemotePort());
        sendButton.setOnAction(e -> send());
        testButton.setOnAction(e -> test());
        udpChatController = UDPChatController.getInstance();
        udpChatController.subscribe(this);
        initLocalIP();
    }

    private void initLocalIP() {
        var ip = NetworkUtility.getLocalIP();
        if (ip == null) Platform.exit();
        try {
            udpChatController.setLocalIP(ip);
            localIPTextField.setText(udpChatController.getLocalIP());
            Styler.setTextColorGreen(localIPTextField);
        } catch (IllegalArgumentException e) {
            Alerter.showError(e.getMessage());
        }
    }

    private void useLocalPort() {
        var port = localPortTextField.getText();
        int num;
        try {
            num = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            Alerter.showError("Local port is not a number");
            return;
        }
        try {
            udpChatController.setLocalPort(num);
            Styler.setTextColorGreen(localPortTextField);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Styler.setTextColorRed(localPortTextField);
            Alerter.showError(e.getMessage());
        }
    }

    private void useRemoteIP() {
        var ip = remoteIPTextField.getText();
        try {
            udpChatController.setRemoteIP(ip);
            Styler.setTextColorGreen(remoteIPTextField);
        } catch (IllegalArgumentException e) {
            Styler.setTextColorRed(remoteIPTextField);
            Alerter.showError(e.getMessage());
        }
    }

    private void useRemotePort() {
        var port = remotePortTextField.getText();
        int num;
        try {
            num = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            Alerter.showError("Remote port is not a number");
            return;
        }
        try {
            udpChatController.setRemotePort(num);
            Styler.setTextColorGreen(remotePortTextField);
        } catch (IllegalArgumentException e) {
            Styler.setTextColorRed(remotePortTextField);
            Alerter.showError(e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        Platform.runLater(() -> {
            var label = new Label(message.address().getHostName() + ": " + message.content());
            Styler.setTextColorGreen(label);
            messagesList.getItems().add(label);
        });
    }

    @Override
    public void onMessageSent(Message message) {
        Platform.runLater(() -> {
            var label = new Label("Me: " + message.content());
            Styler.setTextColorRed(label);
            messagesList.getItems().add(label);
        });
    }

    private void test() {
        try {
            UDPChatController.getInstance().testChat();
            Alerter.showMessage("Chat was created");
        } catch (IOException | IllegalStateException e) {
            Alerter.showError("Could not create chat: " + e.getMessage());
        }
    }

    private void send() {
        var message = messageTextArea.getText();
        if (message.isEmpty()) {
            Alerter.showError("Message area is empty");
            return;
        }
        try {
            UDPChatController.getInstance().sendMessage(message);
            messageTextArea.clear();
        } catch (IOException | IllegalStateException e) {
            Alerter.showError("Could not send message: " + e.getMessage());
        }
    }
}
