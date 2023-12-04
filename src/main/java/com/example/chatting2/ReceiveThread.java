package com.example.chatting2;

public record ReceiveThread(
        Client client
) implements Runnable {

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            client.receive();
        }
    }
}
