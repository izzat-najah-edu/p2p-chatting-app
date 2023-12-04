package com.example.chatting2;

import com.example.chatting2.client.ClientController;
import com.example.chatting2.client.ClientServer;
import com.example.chatting2.server.Server;

import javax.swing.JFrame;

public class Application {
    public static void main(String[] args) {
        ClientServer clientServer1 = new ClientServer(new ClientController());
        clientServer1.setVisible(true);
        clientServer1.setSize(1300, 575);
        clientServer1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ClientServer clientServer2 = new ClientServer(new ClientController());
        clientServer2.setVisible(true);
        clientServer2.setSize(1300, 575);
        clientServer2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Server Server = new Server();
        Server.setVisible(true);
        Server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
