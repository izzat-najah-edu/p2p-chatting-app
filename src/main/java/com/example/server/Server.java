package com.example.server;

import com.example.Alerter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends JFrame {

    private JLabel statusField;
    private JTextField tcpPortField;
    private JTextPane textPaneArea;
    private JList<String> usersList;

    private final HashMap<String, Socket> clientsHash = new HashMap<>();

    public Server() {
        initComponents();
        textPaneArea.setEditable(false);
    }

    private void tcpPortActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void startActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (!tcpPortField.getText().isEmpty()) {
                int portNumber = Integer.parseInt(tcpPortField.getText());
                ServerSocket socket = new ServerSocket(portNumber);
                StyledDocument doc = textPaneArea.getStyledDocument();
                Style style = textPaneArea.addStyle("", null);
                StyleConstants.setForeground(style, Color.GREEN);
                StyleConstants.setBackground(style, Color.white);
                String s1 = "Start Listening at port: " + portNumber + "\n";
                doc.insertString(doc.getLength(), s1, style);
                new ClientAccept(socket).start();
                statusField.setText("Address: " + InetAddress.getByName("localhost").getHostAddress() + ", port: " + portNumber);
            } else {
                Alerter.showError("please enter a port number in 'port number' field.");
            }
        } catch (NumberFormatException e) {
            Alerter.showError("please enter only a numbers in 'port number' field.");
        } catch (IOException | BadLocationException ex) {
            Alerter.showError("The port number is used");
        }
    }

    private class ClientAccept extends Thread {

        private final ServerSocket socket;

        public ClientAccept(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket clientSocket = socket.accept();
                    String username = new DataInputStream(clientSocket.getInputStream()).readUTF();
                    DataOutputStream dataOutOfClient = new DataOutputStream(clientSocket.getOutputStream());
                    if (clientsHash.containsKey(username)) {
                        dataOutOfClient.writeUTF("founded");
                    } else {
                        clientsHash.put(username, clientSocket);
                        addTextToArea(username, true);
                        dataOutOfClient.writeUTF("accept");
                        new EndToEndList().start();
                        new ReadMessage(clientSocket, username).start();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientAccept.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadLocationException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class ReadMessage extends Thread {

        Socket s;
        String ID;

        ReadMessage(Socket s, String username) {
            this.s = s;
            this.ID = username;
        }

        public void run() {
            while (!clientsHash.isEmpty() && clientsHash.containsKey(ID)) {
                try {
                    String in = new DataInputStream(s.getInputStream()).readUTF();
                    if (in.contains("logout")) {
                        new DataOutputStream(clientsHash.get(ID).getOutputStream()).writeUTF("logout");
                        clientsHash.remove(ID);
                        addTextToArea(ID, false);
                        new EndToEndList().start();
                    }
                } catch (IOException | BadLocationException ex) {
                    clientsHash.remove(ID);
                    try {
                        addTextToArea(ID, false);
                    } catch (BadLocationException ex1) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    new EndToEndList().start();
                    Alerter.showError(ex.getMessage());
                }
            }
        }
    }


    private class EndToEndList extends Thread {
        DefaultListModel<String> dlm;

        public EndToEndList() {
            dlm = new DefaultListModel<>();
            usersList.setModel(dlm);
        }

        @Override
        public void run() {
            try {
                StringBuilder s = new StringBuilder();
                Set<String> k = clientsHash.keySet();
                Iterator<String> itr = k.iterator();
                dlm.clear();
                while (itr.hasNext()) {
                    String key = itr.next();
                    s.append(key).append(",").append(clientsHash.get(key).getPort()).append(",").append(clientsHash.get(key).getInetAddress().getHostAddress()).append("&?");
                    String ele = clientsHash.get(key).getInetAddress().getHostAddress() + "," + clientsHash.get(key).getPort();
                    dlm.addElement(ele);
                }
                if (!s.isEmpty()) {
                    s = new StringBuilder(s.substring(0, s.length() - 2));
                }
                itr = k.iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    try {
                        new DataOutputStream(clientsHash.get(key).getOutputStream()).writeUTF("add to list" + s);
                    } catch (IOException ex) {
                        clientsHash.remove(key);
                        addTextToArea(key, false);
                    }
                }
            } catch (BadLocationException ignored) {
            }
        }
    }

    public void addTextToArea(String username, boolean color) throws BadLocationException {
        StyledDocument doc = textPaneArea.getStyledDocument();
        Style style = textPaneArea.addStyle("", null);
        if (color) {
            StyleConstants.setForeground(style, Color.BLUE);
            StyleConstants.setBackground(style, Color.white);
            String s1 = username + " login" + "\n";
            doc.insertString(doc.getLength(), s1, style);
        } else {
            StyleConstants.setForeground(style, Color.RED);
            StyleConstants.setBackground(style, Color.white);
            String s1 = username + " logout" + "\n";
            doc.insertString(doc.getLength(), s1, style);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        JPanel jPanel1 = new JPanel();
        JButton start = new JButton();
        tcpPortField = new javax.swing.JTextField();
        JLabel jLabel1 = new JLabel();
        JScrollPane jScrollPane1 = new JScrollPane();
        textPaneArea = new javax.swing.JTextPane();
        JComboBox<String> jComboBox1 = new JComboBox<>();
        JScrollPane jScrollPane2 = new JScrollPane();
        usersList = new javax.swing.JList<>();
        statusField = new javax.swing.JLabel();
        JLabel jLabel11 = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TCP Server");

        jPanel1.setBackground(new java.awt.Color(220, 220, 220));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        start.setFont(new java.awt.Font("Georgia", Font.PLAIN, 14));
        start.setText("Start Listening");
        start.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, new java.awt.Color(204, 204, 204)));
        start.addActionListener(this::startActionPerformed);

        tcpPortField.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 16));
        tcpPortField.setText("8888");
        tcpPortField.addActionListener(this::tcpPortActionPerformed);

        jLabel1.setFont(new java.awt.Font("Georgia", Font.PLAIN, 16));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Port :");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        textPaneArea.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 14));
        jScrollPane1.setViewportView(textPaneArea);

        jComboBox1.setBackground(new java.awt.Color(233, 237, 251));
        jComboBox1.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 18));
        try {
            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{InetAddress.getLocalHost().getHostAddress(), "Loopback Pseudo-Interface 1: 127.0.0.1"}));
        } catch (UnknownHostException e) {
            Alerter.showError(e.getMessage());
        }
        jComboBox1.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(0, 51, 51)));

        usersList.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 16)); // NOI18N
        jScrollPane2.setViewportView(usersList);

        statusField.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 16)); // NOI18N
        statusField.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusField.setText("the State here");

        jLabel11.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Status :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(44, 44, 44)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(start, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(tcpPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(100, 100, 100)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(48, 48, 48)
                                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(start, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(tcpPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }
}
