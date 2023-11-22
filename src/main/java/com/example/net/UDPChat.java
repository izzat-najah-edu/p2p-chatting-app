package com.example.net;

import com.example.Alerter;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public record UDPChat(
        DatagramSocket socket,
        List<ChatListener> chatListeners
) implements AutoCloseable, Runnable {

    private static final int BUFFER_SIZE = 1024;

    @Override
    public void run() {
        while (!socket.isClosed()) try {
            var message = receive();
            chatListeners.forEach(i -> i.onMessageReceived(message));
        } catch (IOException e) {
            if (socket.isClosed()) break;
            Alerter.showError("Failed to receive message: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void send(Message message) throws IOException {
        var buffer = message.content().getBytes(StandardCharsets.UTF_8);
        var packet = new DatagramPacket(buffer, buffer.length, message.address(), message.port());
        socket.send(packet);
        chatListeners.forEach(i -> i.onMessageSent(message));
    }

    private Message receive() throws IOException {
        var buffer = new byte[BUFFER_SIZE];
        var packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        var content = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
        return new Message(packet.getAddress(), packet.getPort(), content);
    }
}
