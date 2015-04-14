package com.communication;

import com.interfaceApp.FacadeInterface;
import com.interfaceApp.typeUser;
import com.message.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;

import static java.lang.Thread.*;

/**
 * Created by lucille on 24/03/15.
 */
public class FacadeCom {

    private DatagramSocket daSocket;
    private int port;
    private UDPSender sender;
    private UDPReceiver receiver;
    private typeUser nom;
    private InetAddress addrDist;
    private etatCom etat;
    private FacadeInterface inter;


    public FacadeCom (typeUser nom, FacadeInterface inter) {
        try {
            this.nom = nom;
            this.port = 1234;
            this.daSocket = new DatagramSocket(this.port);
            this.daSocket.setBroadcast(true);
            this.sender = new UDPSender(this.port, this.daSocket);
            this.receiver = new UDPReceiver(this, this.daSocket, this.nom);
            this.receiver.start();
            this.etat=etatCom.Deconnecte;
            this.inter = inter;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // pilote
    public void demandeConnect() {
        this.etat=etatCom.EnConnexion;
        while(this.etat==etatCom.EnConnexion) {
            this.sender.connecter();
            try {
                currentThread().sleep(200);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }


    // pilote
    public void demandeDeconnect() {
       int compteur=0;
        this.etat=etatCom.Fin_Wait1;
        while(this.etat==etatCom.Fin_Wait1 && compteur <3){
            this.sender.envoiGoodbye();
            compteur ++;
            try {
                currentThread().sleep(200);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        this.etat=etatCom.Deconnecte;
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    // drone normalement
    public void processHello(InetAddress addr){
        this.addrDist = addr;
        this.etat=etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        this.sender.envoiHelloAck();
        if (this.nom == typeUser.DRONE) {
            this.inter.printTxt("Reception d'un hello");
        }
    }

    public void processHelloAck(InetAddress addr){
        this.addrDist = addr;
        this.etat=etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        // appeler fonction connexion réussi de l'interface
        if (this.nom == typeUser.DRONE) {
            this.inter.printTxt("Reception d'un hello ack");
        }
    }

    public void processGoodbye(){
        if(this.etat==etatCom.Fin_Wait1){
            this.etat=etatCom.Deconnecte;
            //fonction déconnexion réussie
        }
        else{
            this.sender.envoiGoodbye();
            if (this.nom == typeUser.DRONE) {
                this.inter.printTxt("Reception d'un goodbye");
            }
            this.etat=etatCom.Deconnecte;
            if (this.nom == typeUser.DRONE) {
                this.inter.printTxt("Envoie d'un goodbye");
            }
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
