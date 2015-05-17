package com.communication.Envoi;

import android.os.AsyncTask;

import com.communication.FacadeCom;
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
        String result = "!!!!!! N'est pas passé dans le switch d'envoi";

        for (i = 0; i < params.length; i++) {

            typeContenu cont = params[i].getContenu();
            UDPSender send = params[i].getSender();
            this.com =  params[i].getCom();
            this.drone = params[i].isDrone();
            String msg = params[i].getAffiche();

            switch (cont) {
                case HELLO:
                    send.connecter();
                    result = "** Envoi de Hello";
                    break;
                case HELLOACK:
                    send.envoiHelloAck();
                    result = "** Envoi de HelloAck";
                    break;
                case GOODBYE:
                    send.envoiGoodbye();
                    result = "** Envoi de Goodbye";
                    break;
                case INFORMATIONS:
                    send.sendMessage(params[i].getInfo());
                    result = "** Envoi d'information, Lat. : " + params[i].getInfo().getLatitude() + ", Long. :" + params[i].getInfo().getLongitude() + ", bat. :" + params[i].getInfo().getBattery_level();
                    break;
                case PRINT:
                    result = "** " + msg;
                    break;
                default:
                    result = "!!!!!! Erreur d'envoi, mauvais typeContenu";
                    break;
            }
        }
        return result;
    }


    public void onPostExecute(String result) {
        if (drone) {
            this.com.getInter().printTxt(result);
        } else
            System.out.println("Youhou");
    }

}
