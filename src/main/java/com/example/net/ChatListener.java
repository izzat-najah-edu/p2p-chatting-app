package com.example.net;

/**
 * This interface represents a listener for chat messages.
 */
public interface ChatListener {
    default void onMessageReceived(Message message) {
    }

    default void onMessageSent(Message message) {
    }
}
