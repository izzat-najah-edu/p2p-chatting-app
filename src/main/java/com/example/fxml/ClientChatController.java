package com.example.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientChatController
        implements Initializable {

    @FXML
    private AnchorPane clientChatRoot;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private ListView<?>
            messagesList,
            onlineUsersList;

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
            testButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusTextField.setEditable(false);
    }
}
