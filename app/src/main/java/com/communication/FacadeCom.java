package com.communication;

import com.message.*;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;

/**
 * Created by lucille on 24/03/15.
 */
public class FacadeCom {

    private DatagramSocket daSocket;
    private int port;
    private UDPSender sender;
    private UDPReceiver receiver;
    private String nom;



    public FacadeCom (String nom) {
        try {
            this.nom = nom;
            this.port = 1234;
            this.daSocket = new DatagramSocket(this.port);
            this.daSocket.setBroadcast(true);
            this.sender = new UDPSender(this.port, this.daSocket);
            this.receiver = new UDPReceiver(this, this.daSocket, this.nom);
            this.receiver.start();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void processHello(){

    }

    void processHelloAck(){

    }

    void processGoodbye(){

    }

    void processInfo(Informations infos){

    }
}
