package com.communication;

import android.os.Handler;
import android.os.Looper;

import com.communication.Envoi.ComParams;
import com.communication.Envoi.UDPAsyncTask;
import com.communication.Envoi.UDPReceiver;
import com.communication.Envoi.UDPSender;
import com.communication.TCP.TCPSender;
import com.communication.TCP.TCPServer;
import com.interfaceApp.FacadeInterface;
import com.interfaceApp.droneInterface.Screen;
import com.interfaceApp.typeUser;
import com.message.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.io.IOException;


import static java.lang.Thread.*;

/**
 * Created by lucille on 24/03/15.
 */
public class FacadeCom {
    /**
     * private static FacadeCom singleton : singleton de la class FacadeCom
     * private DatagramSocket daSocket : instance du Datagram Socket utilisé pour les UDPsender et UDPreceiver
     * private int port : port d'envoi de l'UDPsender
     * private UDPSender sender : instance d'UDPSender
     * private UDPReceiver receiver : instance d'UDPreceiver
     * private Screen screen : instance de la principale activité de l'application drone
     * private typeUser nom : = PILOTE s'il s'agit de la facadeCom de l'application pilote, = DRONE s'il s'agit de la facadeCom de l'application drone,
     * private InetAddress addrDist : adresse IP de l'application distante
     * private InetAddress addrLoc : adresse locale de l'application locale
     * private etatCom etat : etat de l'application (concernant la connection)
     * private static Informations info : objet envoyé reguliérement du drone au pilote
     * private FacadeInterface inter : instance de la facadeInterface de l'application
     * private static boolean drone : = true s'il s'agit du drone, = false s'il s'agit du pilote
     * private Handler infoHandler : handler utilisé pour l'envoi de l'objet info 
     * private Handler helloHandler : handler utilisé pour l'affichage de message sur l'application drone lors de la reception d'un hello
     **/
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
    private Photo photo;
    private FacadeInterface inter;
    private static boolean drone;
    private TCPServer tcpServer;
    private Handler infoHandler = new Handler(Looper.getMainLooper());
    private Handler helloHandler = new Handler(Looper.getMainLooper());


    private int portTCP;
    /**
     * *****
     * Gestion de l'envoi et reception des goodbye pas vérifiée
     * ********
     */


    public FacadeCom(typeUser nom, FacadeInterface inter, boolean drone) {
        try {
            this.nom = nom;
            this.port = 1234;
            this.portTCP = 7891;
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
            this.tcpServer = new TCPServer(new ServerSocket(this.portTCP),this);
            this.tcpServer.start();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Permet de recuperer l'instance de FacadeInterface avec les paramètres (typeUser nom, FacadeInterface inter, boolean drone)
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


    /**
     * Methiode appelée par l'application pilote lors de la connection du pilote au drone. Cette methode appelle la methode sendHello() du sender.
     **/
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


    /**
     * Methode appelée par l'application pilote lors de la deconnection du pilote au drone. Cette methode va appeler la methode sendGoodbye() du sender. 
     * La methode send goodBye() sera appelé 3 fois maximum ou jusqu'à ce qu'il est recu un ack.
     **/
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

    /**
     * Methode appelée lors de la reception d'un hello. Si le drone recoit un hello, l'application va afficher "reception d'un hello" puis renvoyer 
     * un helloAck. Cette methode prend en paramètre l'addresse IP de l'emetteur du hello. Elle va ensuite l'enregistrer dans son sender. 
     **/
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



    /**
     * Methode appelée lors de la reception d'un helloAck. Cette methode prend en paramètre l'addresse IP de l'emetteur du hello. Elle va ensuite l'enregistrer dans son sender. 
     **/
    public void processHelloAck(InetAddress addr) {
        this.addrDist = addr;
        this.etat = etatCom.Connecte;
        this.sender.setAddrDist(this.addrDist);
        // appeler fonction connexion réussi de l'interface
    }


    /**
     * Methode appelée lors de la reception d'un goodbye. Si cette reception est une réponse à un precedent goodbye envoyé, l'application se considére déconnectée, sinon elle renvoie un goodbye et se considère déconnecté.
     * Dans tous les cas elle met à null l'adresse Ip de l'application distante.
     **/
    public void processGoodbye() {
        if (this.etat == etatCom.Fin_Wait1) {
            this.etat = etatCom.Deconnecte;
            //fonction déconnexion réussie
        } else {
            ComParams params;
            params = new ComParams(this, typeContenu.GOODBYE, this.drone);
            UDPAsyncTask task = new UDPAsyncTask();
            task.execute(params);
            this.etat = etatCom.Deconnecte;
        }
        this.addrDist = null;
        this.sender.setAddrDist(null);
        if (this.drone)
        this.screen.onDeconnectedState();

    }


    /**
     * Methode appelée pour l'envoi périodique de l'objet info.
     **/
    public void sendInfo() {
       infoHandler.post(new Runnable() {
            public void run() {
                ComParams params = new ComParams(FacadeCom.getSingleton(),typeContenu.INFORMATIONS, FacadeCom.info, FacadeCom.drone);
                UDPAsyncTask task = new UDPAsyncTask();
                task.execute(params);
            }
        });
    }



    /**
     * ***********************************************************
     *                      Gestion photo
     * ***********************************************************
     * */
    public void sendPhoto(byte [] data){
        Photo photo = new Photo(data);
        PhotoSize size = new PhotoSize(data.length);
        TCPSender tsender = new TCPSender(this.addrDist, size, photo, this.portTCP, this);
        tsender.start();
       /* ComParams params = new ComParams(this, typeContenu.PHOTO, photo, this.drone);
        UDPAsyncTask task = new UDPAsyncTask();
        task.execute(params);*/
    }

    public void sendDebutPhoto(){
        ComParams params = new ComParams(this, typeContenu.DebutPhoto, this.drone);
        UDPAsyncTask task = new UDPAsyncTask();
        task.execute(params);
    }

    public void sendFinPhoto(){
        ComParams params = new ComParams(this,  typeContenu.FinPhoto, this.drone);
        UDPAsyncTask task = new UDPAsyncTask();
        task.execute(params);
    }

    public void processDebutPhoto(){
        //Commencer le début de la prise de vue et de l'envoi de photos
        this.inter.processDebutPhoto();
    }

    public void processFinPhoto(){
        //Changer de mode et arrêter prévisualisation et envoi
        this.inter.processFinPhoto();
    }

    public void processPhoto(Photo img){
        inter.recupererPhoto(img.getImage());
    }

    public void processInfo(Informations infos) {
        this.inter.processInfo(infos);
    }


    public void photoReceived(byte[] tof) {
        System.out.println("Reception d'une photo : comm");
        this.inter.recupererPhoto(tof);
    }


    /**
     * Methode appelé par l'application drone pour l'affichage de message sur son activité principale.
     **/
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
