package com.example.chatting2;

public record ReceiveThread(
        ClientController clientController
) implements Runnable {

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            clientController.receive();
        }
    }
}
