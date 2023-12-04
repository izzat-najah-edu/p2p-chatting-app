package com.example.client;

import com.example.Alerter;
import com.example.Message;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientServer extends JFrame
        implements ReceiveListener {

    private javax.swing.JTextField localIpField;
    private javax.swing.JTextField localPortField;
    private javax.swing.JTextField remoteIpField;
    private javax.swing.JTextField remotePortField;
    private javax.swing.JTextArea inArea;
    private javax.swing.JList<String> onlineUsersList;
    private javax.swing.JTextField serverIpField;
    private javax.swing.JTextField serverPortField;
    private javax.swing.JTextField statusField;
    private javax.swing.JTextPane textPaneArea;
    private javax.swing.JTextField usernameField;
    private javax.swing.JTextField passwordField;
    private Map<String, float[]> colorsMap;

    private final ClientController controller;

    public ClientServer(ClientController controller) {
        this.controller = controller;
        controller.subscribe(this);
        initComponents();
        textPaneArea.setEditable(false);
        remoteIpField.setEditable(false);
        remotePortField.setEditable(false);
        inArea.setForeground(Color.GRAY);
        inArea.setText("enter text here");
        setSize(1300, 575);
    }

    private void loginActionPerformed(ActionEvent evt) throws IOException {
        if (usernameField.getText().isEmpty()
                || passwordField.getText().isEmpty()
                || serverIpField.getText().isEmpty()
                || localIpField.getText().isEmpty()
                || localPortField.getText().isEmpty()
                || serverPortField.getText().isEmpty()) {
            Alerter.showError("You should enter the following (TCP Port&IP, local Port&IP and your name and pass)");
            return;
        }

        if (controller.isLoggedIn()) {
            Alerter.showError("You are already logged in.");
        } else try {
            controller.login(
                    usernameField.getText().trim(),
                    passwordField.getText().trim(),
                    serverIpField.getText().trim(),
                    Integer.parseInt(serverPortField.getText().trim()),
                    localIpField.getText().trim(),
                    Integer.parseInt(localPortField.getText().trim())
            );
            onlineUsersList.setModel(controller.getDlm());
        } catch (NumberFormatException e) {
            Alerter.showError("Invalid port number!");
        } catch (Exception e) {
            Alerter.showError("Invalid inputs!");
        }
    }

    private void serIpActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void serPortActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void Local_IPActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void Local_PortActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void Remot_IPActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void Remot_PortActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void sendActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (!controller.isLoggedIn()) {
                Alerter.showError("You can't send, please Login first");
            } else if (remoteIpField.getText().isEmpty() || remotePortField.getText().isEmpty()) {
                Alerter.showError("You should select a user from the online user list");
            } else if (inArea.getText().isEmpty() || inArea.getText().equals("enter text here")) {
                Alerter.showError("You can't send empty message");
            } else {
                var msg = inArea.getText();
                var username = controller.getUsername();
                var formattedMsg = generateMessage(msg, username);

                var remoteIp = remoteIpField.getText();
                int remotePort = Integer.parseInt(remotePortField.getText());
                controller.send(formattedMsg, remotePort, remoteIp);
                inArea.setText("");
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendToAllActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (!controller.isLoggedIn()) {
                Alerter.showError("You can't send, please Login first");
            } else if (inArea.getText().isEmpty() || inArea.getText().equals("enter text here")) {
                Alerter.showError("You can't send empty message");
            } else {
                var msg = inArea.getText();
                var username = controller.getUsername();
                var formattedMsg = generateMessage(msg, username);

                for (int i = 0; i < onlineUsersList.getModel().getSize(); i++) {
                    String s = onlineUsersList.getModel().getElementAt(i);
                    String[] userInfo = s.split(",");
                    var remoteIp = userInfo[1];
                    var remotePort = Integer.parseInt(userInfo[2]);
                    controller.send(formattedMsg, remotePort, remoteIp);
                }
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String generateMessage(String msg, String username) throws BadLocationException {
        var C = new GregorianCalendar();
        int sec = C.get(Calendar.SECOND);
        int min = C.get(Calendar.MINUTE);
        int hr = C.get(Calendar.HOUR);
        StyledDocument doc = textPaneArea.getStyledDocument();
        Style style = textPaneArea.addStyle("", null);
        StyleConstants.setForeground(style, Color.RED);
        StyleConstants.setBackground(style, Color.white);
        String s1 = hr + ":" + min + ":" + sec + " ME: " + msg + "\n";
        doc.insertString(doc.getLength(), s1, style);
        msg = hr + ":" + min + ":" + sec + " " + username + ": " + msg;
        return msg;
    }


    private void statusActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void inAreaFocusGained(java.awt.event.FocusEvent evt) {
        if (inArea.getText().equals("enter text here")) {
            inArea.setText("");
            inArea.setForeground(Color.BLACK);
        }
    }

    private void inAreaFocusLost(java.awt.event.FocusEvent evt) {
        if (inArea.getText().isEmpty()) {
            inArea.setForeground(Color.GRAY);
            inArea.setText("enter text here");
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        controller.logout();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {
    }

    private void online_userValueChanged(javax.swing.event.ListSelectionEvent evt) {
        try {
            int x = onlineUsersList.getModel().getSize();
            if (!evt.getValueIsAdjusting() && x != 0) {
                String s = onlineUsersList.getSelectedValue();
                String[] tokens = s.split(",");
                remoteIpField.setText(tokens[1]);
                remotePortField.setText(tokens[2]);
            }
        } catch (Exception ignored) {
        }
    }


    @Override
    public void onReceive(Message message) {
        int fromPort = message.port();
        String fromIp = message.address().getHostAddress();

        StyledDocument doc = textPaneArea.getStyledDocument();
        Style style = textPaneArea.addStyle("", null);

        String[] msgMap = message.content().split(" ");
        float[] values;
        if (colorsMap.containsKey(msgMap[1])) {
            values = colorsMap.get(msgMap[1]);
        } else {
            values = new float[]{1, 1, 1};
            values[0] = (float) Math.random();
            values[1] = (float) Math.random();
            values[2] = (float) Math.random();
            colorsMap.put(msgMap[1], values);
        }

        StyleConstants.setForeground(style, new Color(values[0], values[1], values[2]));
        StyleConstants.setBackground(style, Color.white);
        String s1 = message.content() + "\n";
        try {
            doc.insertString(doc.getLength(), s1, style);
        } catch (BadLocationException e) {
            Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, e);
        }
        statusField.setText("Received From IP= " + fromIp + ", port: " + fromPort);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel88 = new JLabel();

        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JTextField();

        javax.swing.JButton login = new javax.swing.JButton();
        javax.swing.JButton jButton2 = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        inArea = new javax.swing.JTextArea();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel3 = new JLabel();
        serverIpField = new javax.swing.JTextField();
        serverPortField = new javax.swing.JTextField();
        JLabel jLabel4 = new JLabel();
        javax.swing.JComboBox<String> jComboBox1 = new javax.swing.JComboBox<>();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        localIpField = new javax.swing.JTextField();
        localPortField = new javax.swing.JTextField();
        remoteIpField = new javax.swing.JTextField();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel8 = new JLabel();
        remotePortField = new javax.swing.JTextField();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        onlineUsersList = new javax.swing.JList<>();
        JLabel jLabel9 = new JLabel();
        javax.swing.JButton send = new javax.swing.JButton();
        javax.swing.JButton sendToAll = new javax.swing.JButton();
        JLabel jLabel10 = new JLabel();
        statusField = new javax.swing.JTextField();
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        textPaneArea = new javax.swing.JTextPane();
        colorsMap = new HashMap<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }

            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(220, 220, 220));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Username :");
        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel88.setText("password :");

        usernameField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        usernameField.setToolTipText("");
        usernameField.setPreferredSize(new java.awt.Dimension(7, 28));

        passwordField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        passwordField.setToolTipText("");
        passwordField.setPreferredSize(new java.awt.Dimension(7, 28));

        login.setBackground(new java.awt.Color(179, 209, 255));
        login.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        login.setText("Login");
        login.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, new java.awt.Color(204, 204, 204)));
        login.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        login.addActionListener(evt -> {
            try {
                loginActionPerformed(evt);
            } catch (IOException e) {
                Alerter.showError(e.getMessage());
            }
        });

        jButton2.setBackground(new java.awt.Color(179, 209, 255));
        jButton2.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jButton2.setText("Logout");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, new java.awt.Color(204, 204, 204)));
        jButton2.addActionListener(this::jButton2ActionPerformed);

        inArea.setColumns(20);
        inArea.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        inArea.setLineWrap(true);
        inArea.setRows(5);
        inArea.setWrapStyleWord(true);
        inArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                inAreaFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                inAreaFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(inArea);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TCP Server Port :");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Avilable Interfaces");

        serverIpField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        serverIpField.setToolTipText("");
        serverIpField.setPreferredSize(new java.awt.Dimension(7, 28));
        serverIpField.addActionListener(this::serIpActionPerformed);

        serverPortField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        serverPortField.setToolTipText("");
        serverPortField.setPreferredSize(new java.awt.Dimension(7, 28));
        serverPortField.addActionListener(this::serPortActionPerformed);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("TCP Server IP :");

        jComboBox1.setBackground(new java.awt.Color(233, 237, 251));
        jComboBox1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        try {
            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"WI-FI:" + InetAddress.getLocalHost().getHostAddress(), "Ethernet:169.254.49.56", "Loopback Pseudo-Interface 1:127.0.0.1"}));
        } catch (UnknownHostException e) {
            Alerter.showError(e.getMessage());
        }

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("  Local Port :");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("  Local IP :");

        localIpField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        localIpField.setToolTipText("");
        localIpField.setPreferredSize(new java.awt.Dimension(7, 28));
        localIpField.addActionListener(this::Local_IPActionPerformed);

        localPortField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        localPortField.setToolTipText("");
        localPortField.setPreferredSize(new java.awt.Dimension(7, 28));
        localPortField.addActionListener(this::Local_PortActionPerformed);

        remoteIpField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        remoteIpField.setToolTipText("");
        remoteIpField.setPreferredSize(new java.awt.Dimension(7, 28));
        remoteIpField.addActionListener(this::Remot_IPActionPerformed);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("  Remote IP :");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("  Remote Port :");

        remotePortField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        remotePortField.setToolTipText("");
        remotePortField.setPreferredSize(new java.awt.Dimension(7, 28));
        remotePortField.addActionListener(this::Remot_PortActionPerformed);

        onlineUsersList.addListSelectionListener(this::online_userValueChanged);
        jScrollPane3.setViewportView(onlineUsersList);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Online Users");

        send.setBackground(new java.awt.Color(179, 209, 255));
        send.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        send.setText("Send");
        send.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, new java.awt.Color(204, 204, 204)));
        send.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        send.addActionListener(this::sendActionPerformed);

        sendToAll.setBackground(new java.awt.Color(179, 209, 255));
        sendToAll.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        sendToAll.setText("Send to All");
        sendToAll.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, null, new java.awt.Color(204, 204, 204)));
        sendToAll.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sendToAll.addActionListener(this::sendToAllActionPerformed);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Status :");

        statusField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        statusField.setToolTipText("");
        statusField.setPreferredSize(new java.awt.Dimension(7, 28));
        statusField.addActionListener(this::statusActionPerformed);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        textPaneArea.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        textPaneArea.setFocusCycleRoot(false);
        jScrollPane4.setViewportView(textPaneArea);


        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)

                                                                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)

                                                                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                                                        .addComponent(jScrollPane4))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(43, 43, 43)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(serverIpField, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                                                        .addComponent(serverPortField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                        .addGroup(jPanel1Layout.createParallelGroup()
                                                                .addGap(30, 30, 30)
                                                                .addComponent(send, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(sendToAll, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))

                                                        .addGroup(jPanel1Layout.createSequentialGroup()


                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                                                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                                                        .addComponent(localIpField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(remoteIpField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(remotePortField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(44, 44, 44)
                                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(statusField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(76, 76, 76)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                .addComponent(send, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(sendToAll, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(18, 18, 18)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)

                                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)

                                                                        .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)


                                                                        .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)

                                                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jScrollPane4)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(37, 37, 37)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(28, 28, 28))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(serverIpField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(15, 15, 15))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(serverPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(32, 32, 32)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(localIpField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(28, 28, 28)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(remoteIpField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(remotePortField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }
}
