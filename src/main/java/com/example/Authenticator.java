package com.example;

import com.example.server.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Authenticator {

    private static final HashMap<String, String> USERS = new HashMap<>();

    static {
        try {
            var file = new Scanner(new File("src/main/java/com/example/server/users.txt"));
            while (file.hasNext()) {
                var line = file.nextLine().split(",");
                USERS.put(line[0].toLowerCase(), line[1].toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValid(String username, String password) {
        return USERS.containsKey(username.toLowerCase())
                && USERS.get(username.toLowerCase()).equals(password.toLowerCase());
    }
}
