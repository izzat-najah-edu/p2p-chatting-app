package com.example.net;

import com.example.Alerter;

import java.io.IOException;
import java.net.*;

/**
 * A utility class for network-related operations.
 */
public interface NetworkUtility {

    /**
     * Retrieves the local IP address of the machine.
     *
     * @return the local IP address as a String, or null if unable to retrieve the IP address
     */
    static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            Alerter.showError(e.getMessage());
            return null;
        }
    }

    /**
     * Checks whether a port is available for use.
     *
     * @param port the port number to check
     * @return true if the port is available, false otherwise
     */
    static boolean isPortAvailable(int port) {
        try (var ignored = new Socket("localhost", port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * This method is used to validate the format of an IP address string.
     *
     * @param ip A string that represents an IP address.
     * @return boolean Returns true if the IP address is valid and false otherwise.
     */
    static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        var ipRegex = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        return ip.matches(ipRegex);
    }

    /**
     * This method is used to validate if a provided port number is within the valid range for TCP/IP ports.
     *
     * @param port The port number to be validated.
     * @return boolean Returns true if the port number is valid and false otherwise.
     */
    static boolean isValidPort(int port) {
        return port >= 1024 && port <= 65535;
    }
}
