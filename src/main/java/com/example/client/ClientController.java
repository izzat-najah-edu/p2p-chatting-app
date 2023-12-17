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
    private PrintWriter outToUser;

    private final ArrayList<ReceiveListener> listeners = new ArrayList<>();
    private int localPort;

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
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void establish(int localPort) {
        if (established) {
            Alerter.showMessage("A connection is already established.");
            return;
        }
        this.localPort = localPort;
        new Thread(serverConnectionRunnable).start();
    }

    public void establish(String remoteIp, int remotePort) {
        if (established) {
            Alerter.showMessage("A connection is already established.");
            return;
        }
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        new Thread(clientConnectionRunnable).start();
    }

    public boolean isEstablished() {
        return established;
    }

    public void send(String message, int remotePort, String remoteIp) {
        try {
            byte[] sBuffer = message.getBytes();
            InetAddress remoteIpAddress = InetAddress.getByName(remoteIp);
            DatagramPacket sendPacket = new DatagramPacket(sBuffer, sBuffer.length, remoteIpAddress, remotePort);
            socket.send(sendPacket);
        } catch (IOException e) {
            Alerter.showError("Could not send message!");
        }
    }

    public void send(String formattedMsg) {
        outToUser.println(formattedMsg);
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
            System.out.println("started");
            while (established) {
                System.out.println("begin");
                try {
                    String msg = inFromUser.readLine();
                    System.out.println("anything");
                    listeners.forEach(i -> i.onReceive(new Message(null, remotePort, msg)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    private final Runnable clientConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;

            while (elapsedTime < 3000) {
                try {
                    clientSocket = new Socket(remoteIp, remotePort);
                    outToUser = new PrintWriter(clientSocket.getOutputStream(), true);
                    inFromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    Alerter.showMessage("Connection Established!");
                    established = true;
                    new Thread(receiveMessagesFromUser).start();
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                elapsedTime = System.currentTimeMillis() - startTime;
            }

            if (elapsedTime >= 3000) {
                // Alerter.showError("Could not establish connection in 5 seconds!");
                established = false;
            }
        }
    };

    private final Runnable serverConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(localPort);
                serverSocket.setSoTimeout(1000); // Set a timeout for accept()

                while (elapsedTime < 3000) {
                    try {
                        clientSocket = serverSocket.accept(); // Wait for client connection
                        outToUser = new PrintWriter(clientSocket.getOutputStream(), true);
                        inFromUser = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        Alerter.showMessage("Connected!");
                        established = true;
                        new Thread(receiveMessagesFromUser).start();
                        break;
                    } catch (SocketTimeoutException ignored) {
                    }

                    elapsedTime = System.currentTimeMillis() - startTime;
                }

                if (elapsedTime >= 3000) {
                    Alerter.showError("Could not connection!");
                }

            } catch (IOException e) {
                Alerter.showError("Server error: " + e.getMessage());
            } finally {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Alerter.showError("Error closing server socket: " + e.getMessage());
                    }
                }
            }
        }
    };
}
