package com.example.old.net;

import com.example.Message;

/**
 * This interface represents a listener for chat messages.
 */
public interface ChatListener {
    void onMessageReceived(Message message);

    void onMessageSent(Message message);
}
