package com.communication;

import android.os.Handler;
import android.os.Looper;

import com.communication.Envoi.ComParams;
import com.communication.Envoi.UDPAsyncTask;
import com.communication.Envoi.UDPReceiver;
import com.communication.Envoi.UDPSender;
import com.interfaceApp.FacadeInterface;
import com.interfaceApp.droneInterface.Screen;
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

    private static FacadeCom singleton ;

    private DatagramSocket daSocket;
    private int port;
    private UDPSender sender;
    private UDPReceiver receiver;
    private Screen screen;
    private typeUser nom;
    private InetAddress addrDist;
    private InetAddress addrLoc;
    private etatCom etat;
    private static Informations info;
    private static Photo img;
    private FacadeInterface inter;
    private static boolean drone;
    private Handler infoHandler = new Handler(Looper.getMainLooper());
    private Handler helloHandler = new Handler(Looper.getMainLooper());

    /**
     * *****
     * Gestion de l'envoie et reception des goodbye pas vérifiée
     * ********
     */


    public FacadeCom(typeUser nom, FacadeInterface inter, boolean drone) {
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
            //this.addrLoc = IPAddress.getIp();
            this.etat = etatCom.Deconnecte;
            this.info = new Informations(0.0,0.0,0);
            this.inter = inter;
            this.drone = drone;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Permet de recuperer l'instance de FacadeInterface
     * @return singleton
     */
    public static FacadeCom getInstance(typeUser nom, FacadeInterface inter, boolean drone) {
        if (singleton == null) {
            singleton = new FacadeCom(nom, inter, drone) ;
        }
        return singleton ;
    }

    public static FacadeCom getSingleton() {
        return singleton ;
    }


    // pilote
    public void demandeConnect() {
        this.etat = etatCom.EnConnexion;
        while (this.etat == etatCom.EnConnexion) {

            ComParams params = new ComParams(this, typeContenu.HELLO, this.drone);
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);
            System.out.println("Envoi hello");
            try {
                currentThread().sleep(1000);
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
            ComParams params = new ComParams(this, typeContenu.GOODBYE, this.drone);
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);
            compteur++;
            try {
                currentThread().sleep(1000);
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

        helloHandler.post(new Runnable() {
            private FacadeCom fcom;
            public void run() {
                this.fcom = FacadeCom.getSingleton();
                if (this.fcom.getNom() == typeUser.DRONE) {
                    ComParams params2 = new ComParams(this.fcom, this.fcom.isDrone(), "Reception d'un hello");
                    UDPAsyncTask task2 = new UDPAsyncTask();
                    task2.execute(params2);
                }
            }
        });


        helloHandler.post(new Runnable() {
            private FacadeCom fcom;
            public void run() {
                this.fcom = FacadeCom.getSingleton();
                ComParams params = new ComParams(this.fcom, this.fcom.isDrone(), "Envoi d'un hello ack");
                UDPAsyncTask task = new UDPAsyncTask();
                task.execute(params);
            }
        });

        this.sender.envoiHelloAck();


        if (this.nom == typeUser.DRONE) {
            this.screen.onConnectedState();
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
                params = new ComParams(this, typeContenu.GOODBYE, this.drone);
            } else {
                params = new ComParams(this, typeContenu.GOODBYE, this.drone);
            }
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);
            this.etat = etatCom.Deconnecte;
        }
        this.addrDist = null;
        this.sender.setAddrDist(null);
    }

    public void sendInfo() {

        infoHandler.post(new Runnable() {
            public void run() {
                ComParams params = new ComParams(FacadeCom.getSingleton(),typeContenu.INFORMATIONS, FacadeCom.info, FacadeCom.drone);
                UDPAsyncTask task = new UDPAsyncTask();
                task.execute(params);
            }
        });
        // ...
    }
    //TODO : faire envoi et réception du message début photo et fin photo
    public void sendPhoto(){
        ComParams params = new ComParams(this, typeContenu.PHOTO, img, this.drone);
        UDPAsyncTask task = new UDPAsyncTask();
        task.execute(params);
    }

    public void sendDebutPhoto(){
        this.sender.envoiDebutPhoto();
    }

    public void sendFinPhoto(){
        this.sender.envoiFinPhoto();
    }
    public void processDebutPhoto(){
        //Commencer le début de la prise de vue et de l'envoi de photos
    }
    public void processFinPhoto(){
        //Changer de mode et arrêter prévisualisation et envoi
    }
    public void processPhoto(Photo img){

    }
    public void processInfo(Informations infos) {
        this.inter.processInfo(infos);
    }

    public void printDrone(String msg) {
        ComParams params = new ComParams(this, this.drone, msg);
        UDPAsyncTask task = new UDPAsyncTask();
        task.execute(params);
    }



    /**
     * ***********************************************************
     *                      Getter et setter
     * ***********************************************************
     * */


    public void setAddrDist(InetAddress addr) {
        this.addrDist = addr;
    }

    public InetAddress getAddrDist() {
        return this.addrDist;
    }

    public UDPSender getSender() {
        return sender;
    }


    public void setBattery(float battery) {
        this.info.setBattery_level(battery);
    }

    public void setLong(double longitude) {
        this.info.setLongitude(longitude);
    }

    public void setLat(double latitude) {
        this.info.setLatitude(latitude);
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public FacadeInterface getInter() {
        return inter;
    }

    public typeUser getNom() {
        return nom;
    }

    public static boolean isDrone() {
        return drone;
    }
}
