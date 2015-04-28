package com.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.interfaceApp.typeUser;
import com.message.*;

/**
 * Created by lucille on 24/03/15.
 */
public class UDPReceiver extends Thread {

    /**
     * Object use to receiver everything except a file to a remote user.
     */

    private FacadeCom fcom; //A IMPLEMENTER
    private DatagramSocket server ;
    private byte[] bufIn;
    private typeUser nom;
    private InetAddress addrLoc;


    /**
     * UDPReceiver constructor :
     * instantiate the ChatNI field, the Datagram socket field, and the byte[] field.
     * @param f : FacadeCom
     * @param soc : DatagramSocket
     * @param nom : String
     * @throws IOException
     */
    public UDPReceiver(FacadeCom f, DatagramSocket soc, typeUser nom, InetAddress addr) throws IOException {
        this.fcom = f;
        this.server = soc;
        bufIn = new byte[5000];
        this.nom = nom;
        this.addrLoc = addr;
    }


    /**
     * run() : instantiate a DatagramPacket object (packet), launch the DatagramSocket's method receive(packet)
     * Manage the AbstractMessage received and depending on the Type if the AbstractMessage (Hello, HelloACK, Goodbye, Message)
     * call the correct method of the ChatNI object.
     */
    public void run() {
        ObjectInput in = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bufIn);
        try {

            while (true) {
                // le socket bloque jusqu'a ce qu'il recoive un DatagramPacket
                DatagramPacket packet = new DatagramPacket(bufIn, bufIn.length);
                this.server.receive(packet);

                // Traitement du packet pour le re-transformer en AbstractMessage
                byteIn.reset();
                in = new ObjectInputStream(byteIn);
                AbstractMessage inMessage = (AbstractMessage) in.readObject();


                if(!(packet.getAddress().equals(this.addrLoc))) {
                    if (inMessage.getTypeContenu() == typeContenu.HELLO) {
                        Hello helloSerialise = (Hello) inMessage;
                        System.out.println(this.nom + " : Je reçois un HELLO de " + packet.getAddress() + " ! ");
                        this.fcom.processHello(packet.getAddress());
                    } else if (inMessage.getTypeContenu() == typeContenu.HELLOACK) {
                        HelloAck helloackSerialise = (HelloAck) inMessage;
                        System.out.println(this.nom + " : Je reçois HELLOACK de " + packet.getAddress() + " ! ");
                        this.fcom.processHelloAck(packet.getAddress());
                    } else if (inMessage.getTypeContenu() == typeContenu.GOODBYE) {
                        Goodbye goodbyeSerialise = (Goodbye) inMessage;
                        System.out.println(this.nom + " : Je reçois un GOODBYE de " + packet.getAddress() + "  ! ");
                        this.fcom.processGoodbye();
                    } else if (inMessage.getTypeContenu() == typeContenu.INFORMATIONS) {
                        Informations msg = (Informations) inMessage;
                        System.out.println(this.nom + ":" + msg.toString());
                        this.fcom.processInfo(msg);
                    }
                } else {
                   System.out.println(this.nom + " : Je reçois mon propre Hello (" + packet.getAddress() + ") je ne le traite pas ! " + " ! ");
                }
            }
        } catch (SocketException e) {
            this.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * GETTERS AND SETTERS
     */

    /**
     * getServer()
     * @return server : DatagramSocket
     */
    public DatagramSocket getServer() {
        return server;
    }

    /**
     * setServer()
     * @param server : DatagramSocket
     */
    public void setServer(DatagramSocket server) {
        this.server = server;
    }

}
