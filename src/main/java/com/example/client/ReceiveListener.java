package com.example.client;

import com.example.Message;

@FunctionalInterface
public interface ReceiveListener {
    void onReceive(Message message);
}
