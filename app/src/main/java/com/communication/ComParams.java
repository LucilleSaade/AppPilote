package com.communication;

import com.message.Informations;
import com.message.typeContenu;

/**
 * Created by lucille on 15/04/15.
 */
public class ComParams {

    private UDPSender sender;
    private typeContenu contenu;
    private Informations info;
    private FacadeCom com;


    public ComParams(FacadeCom com, typeContenu cont, Informations info) {
        this.com = com;

        this.sender = this.com.getSender();
        this.contenu = cont;
        this.info = info;
    }

    public ComParams(UDPSender send, typeContenu cont) {
        this.sender = send;
        this.contenu = cont;
        this.info = null;
    }


    public UDPSender getSender() {
        return sender;
    }

    public typeContenu getContenu() {
        return contenu;
    }

    public Informations getInfo() {
        return info;
    }

    public FacadeCom getCom() {
        return com;
    }

}
