package com.communication.Envoi;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import com.message.*;

/**
 * Created by lucille on 24/03/15.
 */
public class UDPSender {


    /**
     * Object use to send everything except a file to a remote user.
     */
    private ByteArrayOutputStream bos;
    private DatagramSocket soc;
    private int destPort;
    private InetAddress addrDist;



    /**
     * UDPReceiver constructor :
     * instantiate the hostname field, the bos field (ByteArrayOutputStream), the soc field and the destPort field.
     * @param port : int
     * @param soc : DatagramSocket
     * @throws java.net.SocketException
     */
    public UDPSender(int port, DatagramSocket soc) throws SocketException {
        this.bos = new ByteArrayOutputStream(5000000);
        this.soc = soc;
        this.destPort = port;
        this.addrDist = null;
    }


    /**
     * sendTo() : if the AbstractMessage is a Hello or a GoodBye, send on Broadcast the AbstractMessage after having serialized it
     * if not send only to the recipient.
     * @param obj : AbstractMessage
     * @param address : InetAddress
     */
    private void sendTo(AbstractMessage obj, InetAddress address) {
        ObjectOutput out = null;
        DatagramPacket packet;
        byte[] bufOut;


        try {
            this.bos.reset();
            // Serialisation de l'obj a envoyer
            out = new ObjectOutputStream(this.bos);
            out.flush();
            out.writeObject(obj);
            out.flush();
            bufOut = bos.toByteArray();

            // Transformation en DatagramPacket
            packet = new DatagramPacket(bufOut, bufOut.length, address, this.destPort);

            // Envoie du packet par le socket
            soc.send(packet);
            this.bos.reset();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void connecter() {
        InetAddress address;
        Hello obj = new Hello();
        try {
            // Broadcast pour la connection
            address = InetAddress.getByName("255.255.255.255");

            System.out.println("UDPSender  to " + address);
            sendTo((AbstractMessage) obj, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********************************************
     * Attention catcher exception addr = null; *
     ********************************************/

    public void envoiHelloAck() {
        HelloAck obj = new HelloAck();
        try {
            // Broadcast pour la connection
            System.out.println("UDPSender  to " + this.addrDist);

            sendTo((AbstractMessage) obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void envoiGoodbye() {
        Goodbye obj = new Goodbye();
        try {
            // Broadcast pour la connection
            sendTo((AbstractMessage) obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Informations obj) {
        try {
            System.out.println("Envoi d'un message à l'autre");
            sendTo(obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendPhoto(Photo obj) {
        try {
            System.out.println("Envoi d'une photo à l'autre");
            sendTo(obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void envoiDebutPhoto() {
        DebutPhoto obj = new DebutPhoto();
        try {
            System.out.println("Envoi d'un message à l'autre");
            sendTo(obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void envoiFinPhoto() {
        FinPhoto obj = new FinPhoto();
        try {
            System.out.println("Envoi d'un message à l'autre");
            sendTo(obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAddrDist(InetAddress addr) {
        this.addrDist = addr;
    }
}

