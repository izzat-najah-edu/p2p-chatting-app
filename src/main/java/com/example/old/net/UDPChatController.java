package com.example.old.net;

import com.example.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class UDPChatController {

    private UDPChatController() {
    }

    private static UDPChatController instance = null;

    public static UDPChatController getInstance() {
        if (instance == null) instance = new UDPChatController();
        return instance;
    }

    private String localIP = null, remoteIP = null;

    private Integer localPort = null, remotePort = null;

    private final ArrayList<ChatListener> chatListeners = new ArrayList<>();

    private UDPChat chat = null;

    public void subscribe(ChatListener listener) {
        chatListeners.add(listener);
    }

    public void unsubscribe(ChatListener listener) {
        chatListeners.remove(listener);
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) throws IllegalArgumentException {
        evaluate(localIP, NetworkUtility::isValidIP, () -> new IllegalArgumentException("Local IP is not valid"));
        this.localIP = localIP;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) throws IllegalArgumentException {
        evaluate(remoteIP, NetworkUtility::isValidIP, () -> new IllegalArgumentException("Remote IP is not valid"));
        this.remoteIP = remoteIP;
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) throws IllegalArgumentException, IllegalStateException {
        evaluate(localPort, NetworkUtility::isValidPort, () -> new IllegalArgumentException("Local port is not valid"));
        evaluate(localPort, NetworkUtility::isPortAvailable, () -> new IllegalStateException("Local port is not available"));
        this.localPort = localPort;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        evaluate(remotePort, NetworkUtility::isValidPort, () -> new IllegalArgumentException("Remote port is not valid"));
        this.remotePort = remotePort;
    }

    private <T, U extends Throwable> void evaluate(T value, Predicate<T> validator, Supplier<U> exceptionSupplier) throws U {
        if (!validator.test(value)) throw exceptionSupplier.get();
    }

    private DatagramSocket createSocket() throws IOException, IllegalStateException {
        if (localIP == null || localPort == null)
            throw new IllegalStateException("Not all variables are set");
        return new DatagramSocket(localPort, InetAddress.getByName(localIP));
    }

    private Message createMessage(String content) throws UnknownHostException, IllegalStateException {
        if (remoteIP == null || remotePort == null)
            throw new IllegalStateException("Not all variables are set");
        return new Message(InetAddress.getByName(remoteIP), remotePort, content);
    }

    private void createChat() throws IOException, IllegalStateException {
        var socket = createSocket();
        chat = new UDPChat(socket, chatListeners);
        new Thread(chat).start();
    }

    public void testChat() throws IOException, IllegalStateException {
        if (chat == null || chat.isClosed()) createChat();
    }

    public void sendMessage(String content) throws IOException, IllegalStateException {
        testChat();
        var message = createMessage(content);
        chat.send(message);
    }

    public void closeChat() {
        if (chat != null) chat.close();
    }
}
