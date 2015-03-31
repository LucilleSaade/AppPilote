package com.communication;

import com.message.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private InetAddress addrDist;



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


    public void demandeConnect() {
        this.sender.connecter();

        // gestion de perte
    }

    public void demandeDeconnect() {
        this.sender.envoiGoodbye();
        // gestion de perte : envoi jusqu'à trois fois goodbye si on ne recoit pas de goodbye de l'autre, après ça on se considére deconnecté.
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    public void processHello(InetAddress addr){
        this.addrDist = addr;
        this.sender.setAddrDist(this.addrDist);
        this.sender.envoiHelloAck();
    }

    public void processHelloAck(InetAddress addr){
        this.addrDist = addr;
        this.sender.setAddrDist(this.addrDist);
        // appeler fonction connexion réussi de l'interface
    }

    public void processGoodbye(){
        // appeler fonction deconnexion réussi de l'interface
        // gestion de ack : envoie goobye
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    public void processInfo(Informations infos){

    }

    public void setAddrDist(InetAddress addr) {
        this.addrDist = addr;
    }

    public InetAddress getAddrDist() {
        return this.addrDist;
    }

}
