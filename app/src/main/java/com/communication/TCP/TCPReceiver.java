package com.communication.TCP;

import android.util.Log;

import com.communication.FacadeCom;
import com.message.PhotoSize;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;


/**
 * Created by Emma on 29/05/2015.
 */

public class TCPReceiver extends Thread {

    /**
     * Object use to receive a file.
     */

    private Socket soc;
    private FacadeCom com;


    /**
     * TCPReceiver constructor :
     * instantiate the soc field and the ni field.
     * @param soc : Socket
     * @param com : FacadeCom
     */
    public TCPReceiver (Socket soc,  FacadeCom com) {
        this.soc = soc;
        this.com = com;
    }


    /**
     * run() : receive and manage FileMessage.
     */
    public void run() {
        byte[] bufIn = new byte[1024];

        try {
            InputStream is = (InputStream) soc.getInputStream();
            // Lecture du photosize
            is.read(bufIn);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bufIn);
            ObjectInput oi= new ObjectInputStream(byteIn);
            DataInputStream dis = new DataInputStream(is);

            System.out.println("reception du filmsg");
            // Lecture du photosize
            PhotoSize size = (PhotoSize) oi.readObject();

            System.out.println("Reception du file dans le buffer");

            System.out.println("enregistrement du file sur le disque");
            
            int len = dis.readInt();
            byte[] data = new byte[len];
            if (len > 0) {
                dis.readFully(data);
            }
            Log.d("Reception d'une photo"," : TCP Receiver");
            this.com.photoReceived(data);
            oi.close();
            is.close();
            soc.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * GETTERS AND SETTERS
     */

    /**
     * getSoc()
     * @return soc : Socket
     */
    public Socket getSoc() {
        return soc;
    }

    /**
     * setSoc()
     * @param soc : Socket
     */
    public void setSoc(Socket soc) {
        this.soc = soc;
    }

}
