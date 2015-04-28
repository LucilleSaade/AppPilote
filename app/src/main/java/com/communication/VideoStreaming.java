package com.communication;
import android.net.rtp.RtpStream;
/**
 * Created by sony on 28/04/2015.
 * TOUJOURS EN COURS, JUSTE UNE EBAUCHE
 */
public class VideoStreaming {
    RtpStream rtps ;

    public VideoStreaming(boolean drone){

        if(drone){
            rtps.setMode(RtpStream.MODE_SEND_ONLY);
        }
        else{
            rtps.setMode(RtpStream.MODE_RECEIVE_ONLY);
        }
    }

}

