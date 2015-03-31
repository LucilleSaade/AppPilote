package com.communication;

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
        this.bos = new ByteArrayOutputStream(5000);
        this.soc = soc;
        this.destPort = port;
    }


    /**
     * sendTo() : if the AbstractMessage is a Hello or a GoodBye, send on Broadcast the AbstractMessage after having serialized it
     * if not send only to the recipient.
     * @param obj : AbstractMessage
     * @param address : InetAddress
     */
    private void sendTo(Informations obj, InetAddress address) {
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


    public void connecter(Informations obj) {
        InetAddress address;
        try {
            // Broadcast pour la connection
            address = InetAddress.getByName("255.255.255.255");
            sendTo(obj, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Informations obj) {
        try {
            System.out.println("Envoie d'un message à l'autre");
           sendTo(obj, this.addrDist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

