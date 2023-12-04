package com.example.chatting2;

import com.example.Alerter;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientController {

    private DatagramSocket socket;
    private String username;
    private String password;
    private String localIp;
    private int localPort;
    private String remoteIp;
    private int remotePort;
    private InetAddress remoteIpAddress;
    private byte[] sBuffer;
    private DatagramPacket sendPacket;
    private byte[] rBuffer;
    private DatagramPacket receivePacket;
    private boolean conn = false;
    private boolean loggedIn = false;

    private DefaultListModel<String> dlm;
    private java.io.DataInputStream dataFromServer;
    private DataInputStream DataInputStream;
    private DataOutputStream dataToServer;
    private Socket serverSocket;
    private Read r;
    private ReceiveThread channel;
    private boolean contReceiving = false;
    private boolean receiveFromServer = false;
    private TriRunnable<String, Integer, String> onReceive;

    public ClientController() {
        username = "";
        localIp = "";
        localPort = 0;
        remoteIp = "";
        remotePort = 0;
        rBuffer = new byte[50];
        receivePacket = new DatagramPacket(rBuffer, rBuffer.length);
        password = "";
        dlm = new DefaultListModel<>();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isConnected() {
        return conn;
    }

    public String getUsername() {
        return username;
    }

    public DefaultListModel<String> getDlm() {
        return dlm;
    }

    public void login(String username, String password, String serverIp, int serverPort, String localIp, int localPort, TriRunnable<String, Integer, String> onReceive) {
        this.username = username;
        this.password = password;
        this.onReceive = onReceive;
        if (username.equalsIgnoreCase("ali") && password.equals("1234")
                || username.equalsIgnoreCase("saly") && password.equals("A20B")
                || username.equalsIgnoreCase("aws") && password.equals("ABcd")
                || username.equalsIgnoreCase("adam") && password.equals("1Cb2")) {
            conn = true;
            try {
                socket = new DatagramSocket(localPort);
            } catch (SocketException ex) {
                Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                serverSocket = new Socket(InetAddress.getByName(serverIp), serverPort, InetAddress.getByName(localIp), localPort);
                dataFromServer = new DataInputStream(serverSocket.getInputStream());
                dataToServer = new DataOutputStream(serverSocket.getOutputStream());
                dataToServer.writeUTF(username);

                String s;
                DataInputStream = new DataInputStream(serverSocket.getInputStream());
                s = DataInputStream.readUTF();
                if (s.equals("founded")) {
                    Alerter.showError("You are already login!");
                    return;
                } else if (s.equals("accept")) {
                    dlm.clear();
                    r = new Read(username);
                    r.start();
                }

                receiveFromServer = true;
                channel = new ReceiveThread(this);
                channel.start();
                contReceiving = true;
                Alerter.showMessage("You logged in successfully");
                loggedIn = true;
            } catch (IOException ex) {
                Alerter.showError("The local port is used");
            }
        } else {
            Alerter.showError("not valid user name or pass");
        }
    }

    public void logout() {
        if (!loggedIn) {
            Alerter.showMessage("You are already logged out!");
            return;
        }

        loggedIn = false;
        contReceiving = false;
        receiveFromServer = false;

        sendToServer("logout");
        Alerter.showMessage("You logged out successfully");
    }

    public void send(String message, int remotePort, String remoteIp) {
        try {
            this.sBuffer = message.getBytes();
            this.remoteIp = remoteIp;
            this.remotePort = remotePort;
            this.remoteIpAddress = InetAddress.getByName(remoteIp);
            sendPacket = new DatagramPacket(sBuffer, sBuffer.length, remoteIpAddress, remotePort);
            socket.send(sendPacket);
        } catch (IOException e) {
            Alerter.showError("Could not send message!");
        }
    }

    public void sendToServer(String message) {
        try {
            dataToServer.writeUTF(message);
            socket.close();
            serverSocket.close();
            dataFromServer.close();
            dataToServer.close();
        } catch (IOException e) {
            Alerter.showError("Could not send to server!");
        }
    }

    class Read extends Thread {

        String userName;

        public Read(String userName) {
            this.userName = userName;
        }

        @Override
        public void run() {
            while (receiveFromServer) {
                try {
                    String inputData = dataFromServer.readUTF();
                    if (inputData.equals("logout")) {
                        break;
                    }
                    if (inputData.contains("add to list")) {
                        inputData = inputData.substring(11);
                        dlm.clear();
                        StringTokenizer st = new StringTokenizer(inputData, "&?");
                        while (st.hasMoreTokens()) {
                            String line = st.nextToken();
                            String[] tokens = line.split(",");
                            if (!tokens[0].equals(userName)) {
                                String element = tokens[0] + "," + tokens[2] + "," + tokens[1];
                                dlm.addElement(element);
                            }
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    void receive() {
        try {
            if (contReceiving) {
                socket.receive(receivePacket);
                String msg = new String(rBuffer, 0, receivePacket.getLength());
                if (msg.equals("logout")) return;
                onReceive.apply(msg, receivePacket.getPort(), receivePacket.getAddress().getHostAddress());
            }
        } catch (IOException ignored) {
        }
    }
}
