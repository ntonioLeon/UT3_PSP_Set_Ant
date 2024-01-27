package org.educa;

import org.educa.game.Server;

/**
 * @author Sergio Gonzalez y Antonio Leon
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Server server = new Server();
        server.run();
    }
}