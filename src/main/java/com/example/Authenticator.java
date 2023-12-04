package com.example;

public interface Authenticator {
    static boolean isValid(String username, String password) {
        return username.equalsIgnoreCase("ali") && password.equals("1234")
                || username.equalsIgnoreCase("saly") && password.equals("A20B")
                || username.equalsIgnoreCase("aws") && password.equals("ABcd")
                || username.equalsIgnoreCase("adam") && password.equals("1Cb2");
    }
}
