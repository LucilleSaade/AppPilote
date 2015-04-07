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
    private etatCom etat;


    public FacadeCom (String nom) {
        try {
            this.nom = nom;
            this.port = 1234;
            this.daSocket = new DatagramSocket(this.port);
            this.daSocket.setBroadcast(true);
            this.sender = new UDPSender(this.port, this.daSocket);
            this.receiver = new UDPReceiver(this, this.daSocket, this.nom);
            this.receiver.start();
            this.etat=etatCom.Deconnecte;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void demandeConnect() {
        this.etat=etatCom.EnConnexion;
        while(this.etat==etatCom.EnConnexion) {
            this.sender.connecter();
            try {
                Thread.currentThread().sleep(200);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public void demandeDeconnect() {
       int compteur=0;
        this.etat=etatCom.Fin_Wait1;
        while(this.etat==etatCom.Fin_Wait1 && compteur <3){
            this.sender.envoiGoodbye();
            compteur ++;
            try {
                Thread.currentThread().sleep(200);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        this.etat=etatCom.Deconnecte;
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    public void processHello(InetAddress addr){
        this.addrDist = addr;
        this.etat=etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        this.sender.envoiHelloAck();
    }

    public void processHelloAck(InetAddress addr){
        this.addrDist = addr;
        this.etat=etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        // appeler fonction connexion réussi de l'interface
    }

    public void processGoodbye(){
        if(this.etat==etatCom.Fin_Wait1){
            this.etat=etatCom.Deconnecte;
            //fonction déconnexion réussie
        }
        else{
            this.sender.envoiGoodbye();
            this.etat=etatCom.Deconnecte;
        }
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
