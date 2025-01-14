package com.example.vaidjavafx;

import java.net.InetAddress;

public class USBDetection {

    // Define the correct static IP address of the Raspberry Pi
    private static final String RASPBERRY_PI_IP = "192.168.8.10";

    public static boolean isRaspberryPiConnected() {
        try {
            // Ping the Raspberry Pi's static IP address
            InetAddress raspberryPi = InetAddress.getByName(RASPBERRY_PI_IP);
            return raspberryPi.isReachable(2000); // Timeout in milliseconds
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        if (isRaspberryPiConnected()) {
            System.out.println("Raspberry Pi is connected at IP: " + RASPBERRY_PI_IP);
        } else {
            System.out.println("No Raspberry Pi detected at IP: " + RASPBERRY_PI_IP);
        }
    }
}