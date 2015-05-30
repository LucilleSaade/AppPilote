package com.communication.TCP;

import android.util.Log;

import com.communication.FacadeCom;
import com.message.Photo;
import com.message.PhotoSize;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Created by Emma on 29/05/2015.
 */

public class TCPSender extends Thread {
    private String user;
    private ByteArrayOutputStream bos;
    private FacadeCom com;
    private PhotoSize size;
    private Photo photo;
    private int port;
    private InetAddress addrDist;


    /**
     * TCPSender constructor :
     * instantiate the user field, the dest field, the bos field, the port, field, the file field,
     * the fmsg field (FileMessage object), and the Nickname of the fmsg field.
     * @param dest : ArrayList<String>
     * @param photo : Photo
     * @param port: int
     */
    public TCPSender(InetAddress dest, PhotoSize size, Photo photo, int port, FacadeCom com){
        this.addrDist = dest;
        this.size = size;
        this.bos = new ByteArrayOutputStream((int)this.size.getSize());
        this.port = port;
        this.photo = photo;
        this.com = com;
    }


    /**
     * run() : for each recipient, serialise the fmsg and send it.
     */
    public void run() {
        byte[] bufOut = new byte[(int) this.size.getSize()];
        Socket soc;
        try {
            System.out.println("debut tcp sender");

            soc = new Socket(this.addrDist, this.port);
            //Preparation des objets necessaires pour l'envoie des file et filemsg
            OutputStream os = soc.getOutputStream();
            ObjectOutputStream oos= new ObjectOutputStream(bos);
            oos.flush();

            System.out.println("envoi de la taille");

            // Ecriture dans le flux de sortie, envoie du file message ne contenant que le nom et la taille du fichier
            oos.writeObject(this.size);
            bufOut = this.bos.toByteArray();
            os.write(bufOut);

            DataOutputStream dos = new DataOutputStream(os);
            System.out.println("envoi d'une photo");
            Log.d("TCP sender :", "envoi 'une photo");
            dos.writeInt(this.size.getSize());
            if (this.size.getSize() > 0) {
                dos.write(this.photo.getImage(), 0, this.size.getSize());
            }
            this.com.printDrone("Envoi photo");
            // Vide le tampon
            oos.flush();

            dos.close();
            oos.close();
            os.close();
            soc.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * GETTERS AND SETTERS
     */

    public InetAddress getAddrDist() {
        return addrDist;
    }

    public void setAddrDist(InetAddress addrDist) {
        this.addrDist = addrDist;
    }

    /**
     * getCom()
     * @return com : FacadeCom
     */
    public FacadeCom getCom() {
        return com;
    }

    /**
     * setCom()
     * @param com : FacadeCom
     */
    public void setNi(FacadeCom com) {
        this.com = com;
    }

    /**
     * getBos()
     * @return bos : ByteArrayOutputStream
     */
    public ByteArrayOutputStream getBos() {
        return bos;
    }

    /**
     * setBos()
     * @param bos : ByteArrayOutputStream
     */
    public void setBos(ByteArrayOutputStream bos) {
        this.bos = bos;
    }

}

