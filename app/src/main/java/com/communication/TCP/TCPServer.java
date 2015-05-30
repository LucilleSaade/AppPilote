package com.communication.TCP;

import com.communication.FacadeCom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Emma on 29/05/2015.
 */

public class TCPServer extends Thread {
    private ServerSocket server;
    private FacadeCom com;

    /**
     * This class allows to create a TCPServer to receive files from remote users
     */

    /**
     * TCPServer constructor
     * instantiate the ServerSocket and the chatNI
     */

    public TCPServer (ServerSocket server, FacadeCom com) {
        this.server = server;
        this.com = com;
        System.out.println("Serveur cree");
    }

    /**
     * run() : thread method
     * Creates a new TCPReceiver() when the ServerSocket accepts a connexion
     */

    public void run() {
        Socket soc;
        try {
            while(true) {

                System.out.println("Creation nouveau socket");
                soc = server.accept();
                TCPReceiver receiver = new TCPReceiver(soc, this.com);
                System.out.println("lancement du receiver");
                receiver.start();
            }

        } catch (SocketException e) {
            this.interrupt();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //////////////////////////////////////////
    //         GETTER ET SETTER             //
    //////////////////////////////////////////

    /**
     * getServer()
     * @return ServerSocket server
     */
    public ServerSocket getServer() {
        return server;
    }

}

