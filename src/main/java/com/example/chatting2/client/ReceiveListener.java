package com.example.chatting2.client;

import com.example.net.Message;

@FunctionalInterface
public interface ReceiveListener {
    void onReceive(Message message);
}
