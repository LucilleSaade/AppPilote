package com.communication;

import android.os.AsyncTask;

import com.interfaceApp.FacadeInterface;
import com.message.typeContenu;

/**
 * Created by lucille on 14/04/15.
 */
public class UDPAsyncTask extends AsyncTask<ComParams, String, String> {

    private FacadeCom com;
    private boolean drone;

    @Override
    // On peut y mettre plusieurs params dans la fonction de base mais dans notre cas il faut en mettre un seul
    protected String doInBackground(ComParams... params) {
        int i;
        String result = "N'est pas passé dans le switch d'envoi";

        for (i = 0; i < params.length; i++) {

            typeContenu cont = params[i].getContenu();
            UDPSender send = params[i].getSender();
            this.com =  params[i].getCom();
            this.drone = params[i].isDrone();

            switch (cont) {
                case HELLO:
                    send.connecter();
                    result = "Envoie de Hello";
                    break;
                case HELLOACK:
                    send.envoiHelloAck();
                    result = "Envoie de HelloAck";
                    break;
                case GOODBYE:
                    send.envoiGoodbye();
                    result = "Envoie de Goodbye";
                    break;
                case INFORMATIONS:
                    send.sendMessage(params[i].getInfo());
                    result = "Envoie d'information";
                    break;
                default:
                    result = "Erreur d'envoi, mauvais typeContenu";
                    break;
            }
        }
        return result;
    }


    public void onPostExecute(String result) {
        if (drone) {
            this.com.printDrone(result);
        }
    }

}
