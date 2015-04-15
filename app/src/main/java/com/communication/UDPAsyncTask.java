package com.communication;

import android.os.AsyncTask;

import com.interfaceApp.FacadeInterface;
import com.message.typeContenu;

/**
 * Created by lucille on 14/04/15.
 */
public class UDPAsyncTask extends AsyncTask<ComParams, String, Void> {


    @Override
    // On peut y mettre plusieurs params dans la fonction de base mais dans notre cas il faut en mettre un seul
    protected Void doInBackground(ComParams... params) {
        int i;
        for (i = 0; i < params.length; i++) {

            typeContenu cont = params[i].getContenu();
            UDPSender send = params[i].getSender();
            FacadeCom com =  params[i].getCom();

            switch (cont) {
                case HELLO:
                    send.connecter();
                    break;
                case HELLOACK:
                    send.envoiHelloAck();
                    break;
                case GOODBYE:
                    send.envoiGoodbye();
                    break;
                case INFORMATIONS:
                    send.sendMessage(params[i].getInfo());
                    break;
                default:
                    com.erreurArg();
                    break;
            }
        }
        return null;
    }


}
