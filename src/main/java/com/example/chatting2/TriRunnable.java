package com.example.chatting2;

@FunctionalInterface
public interface TriRunnable<A, B, C> {
    void apply(A a, B b, C c);
}