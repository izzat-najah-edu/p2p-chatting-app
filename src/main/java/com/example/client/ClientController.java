package com.example.client;

import com.example.Alerter;
import com.example.Authenticator;
import com.example.Message;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientController {

    private DatagramSocket socket;
    private String username;

    private final DefaultListModel<String> dlm;
    private final byte[] rBuffer = new byte[50];
    private final DatagramPacket receivePacket;

    private boolean loggedIn = false;
    private boolean established = false;

    private DataInputStream dataFromServer;
    private DataOutputStream dataToServer;
    private Socket serverSocket;

    private String remoteIp;
    private int remotePort;
    private BufferedReader inFromUser;
    private Socket clientSocket;
    private DataOutputStream outToUser;

    private final ArrayList<ReceiveListener> listeners = new ArrayList<>();

    public ClientController() {
        username = "";
        receivePacket = new DatagramPacket(rBuffer, rBuffer.length);
        dlm = new DefaultListModel<>();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public DefaultListModel<String> getDlm() {
        return dlm;
    }

    public void subscribe(ReceiveListener listener) {
        listeners.add(listener);
    }

    public void login(String username, String password, String serverIp, int serverPort, String localIp, int localPort) {
        this.username = username;
        if (Authenticator.isValid(username, password)) {
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
                java.io.DataInputStream dataInputStream = new DataInputStream(serverSocket.getInputStream());
                s = dataInputStream.readUTF();
                if (s.equals("founded")) {
                    Alerter.showError("Username is already logged in!");
                    return;
                }

                loggedIn = true;
                new Thread(readFromServerTask).start();
                new Thread(receiveMessagesTask).start();
                Alerter.showMessage("You logged in successfully");

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
        established = false;
        sendToServer("logout");
        Alerter.showMessage("You logged out successfully");
    }

    public void establish(String remoteIp, int remotePort) {
        if (established) {
            Alerter.showMessage("A connection is already established.");
            return;
        }
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        new Thread(connectionRunnable).start();
    }

    public void send(String message, int remotePort, String remoteIp) {
        try {
            if (established) { // TCP
                outToUser.writeUTF(message);
                return;
            }
            // UDP
            byte[] sBuffer = message.getBytes();
            InetAddress remoteIpAddress = InetAddress.getByName(remoteIp);
            DatagramPacket sendPacket = new DatagramPacket(sBuffer, sBuffer.length, remoteIpAddress, remotePort);
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

    private final Runnable readFromServerTask = new Runnable() {
        @Override
        public void run() {
            while (loggedIn) {
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
                            if (!tokens[0].equals(username)) {
                                String element = tokens[0] + "," + tokens[2] + "," + tokens[1];
                                dlm.addElement(element);
                            }
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        }
    };

    private final Runnable receiveMessagesTask = new Runnable() {
        @Override
        public void run() {
            while (loggedIn) {
                try {
                    socket.receive(receivePacket);
                    String msg = new String(rBuffer, 0, receivePacket.getLength());
                    if (msg.equals("logout")) return;
                    listeners.forEach(i -> i.onReceive(new Message(receivePacket.getAddress(), receivePacket.getPort(), msg)));
                } catch (IOException ignored) {
                }
            }
        }
    };

    private final Runnable receiveMessagesFromUser = new Runnable() {
        @Override
        public void run() {
            while (established) {
                try {
                    String msg = inFromUser.readLine();
                    var address = InetAddress.getByName(remoteIp);
                    listeners.forEach(i -> i.onReceive(new Message(address, remotePort, msg)));
                } catch (IOException ignored) {
                }
            }
        }
    };

    private final Runnable connectionRunnable = new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;

            while (elapsedTime < 1000) {
                try {
                    clientSocket = new Socket(remoteIp, remotePort);
                    outToUser = new DataOutputStream(clientSocket.getOutputStream());
                    inFromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    Alerter.showMessage("Connection Established!");
                    established = true;
                    break;
                } catch (IOException e) {
                    Alerter.showMessage("Trying to establish connection...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                elapsedTime = System.currentTimeMillis() - startTime;
            }

            if (elapsedTime >= 1000) {
                // Alerter.showError("Could not establish connection in 5 seconds!");
                established = false;
            }
        }
    };
}
