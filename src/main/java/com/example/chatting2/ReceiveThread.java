package com.example.chatting2;

public class ReceiveThread implements Runnable {
    Client client;

    ReceiveThread(Client client) {
        this.client = client;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            client.receive();
        }
    }
}
