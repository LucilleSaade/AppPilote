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
    private InetAddress addrLoc;
    private etatCom etat;
    private FacadeInterface inter;


    /**
     * *****
     * On recoit son propre Hello !!
     * Gestion de l'envoie et reception des goodbye pas fait lors de l'appel retour
     * ********
     */


    public FacadeCom(typeUser nom, FacadeInterface inter) {
        try {
            this.nom = nom;
            this.port = 1234;
            this.daSocket = new DatagramSocket(this.port);
            this.daSocket.setBroadcast(true);
            this.sender = new UDPSender(this.port, this.daSocket);
            System.out.println("On enregistre l'adresse ipLocal");
            this.addrLoc = IPAddress.getIp();
            this.receiver = new UDPReceiver(this, this.daSocket, this.nom, this.addrLoc);
            this.receiver.start();
            this.addrLoc = IPAddress.getIp();
            this.etat = etatCom.Deconnecte;
            this.inter = inter;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // pilote
    public void demandeConnect() {
        this.etat = etatCom.EnConnexion;
        while (this.etat == etatCom.EnConnexion) {

            ComParams params = new ComParams(this, typeContenu.HELLO, false);
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);

            try {
                currentThread().sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    // pilote
    public void demandeDeconnect() {
        int compteur = 0;
        this.etat = etatCom.Fin_Wait1;
        while (this.etat == etatCom.Fin_Wait1 && compteur < 3) {
            ComParams params = new ComParams(this, typeContenu.GOODBYE, false);
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);
            compteur++;
            try {
                currentThread().sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.etat = etatCom.Deconnecte;
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    // drone normalement
    public void processHello(InetAddress addr) {
        this.addrDist = addr;
        this.etat = etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        ComParams params = new ComParams(this, typeContenu.HELLOACK, true);
        UDPAsyncTask task = new UDPAsyncTask();
        task.execute(params);
        if (this.nom == typeUser.DRONE) {
//            this.inter.printTxt("Reception d'un hello");
        }
    }

    public void processHelloAck(InetAddress addr) {
        this.addrDist = addr;
        this.etat = etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        // appeler fonction connexion réussi de l'interface
    }

    public void processGoodbye() {
        if (this.etat == etatCom.Fin_Wait1) {
            this.etat = etatCom.Deconnecte;
            //fonction déconnexion réussie
        } else {
            ComParams params;
            if (this.nom == typeUser.DRONE) {
                params = new ComParams(this, typeContenu.GOODBYE, true);
            } else {
                params = new ComParams(this, typeContenu.GOODBYE, false);
            }
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);
            this.etat = etatCom.Deconnecte;
        }
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    public void processInfo(Informations infos) {
    }

    public void printDrone(String msg) {
        this.inter.printTxt(msg);
    }

    public void setAddrDist(InetAddress addr) {
        this.addrDist = addr;
    }

    public InetAddress getAddrDist() {
        return this.addrDist;
    }

    public UDPSender getSender() {
        return sender;
    }


}
