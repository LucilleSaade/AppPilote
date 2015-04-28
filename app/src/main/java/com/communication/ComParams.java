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
    private boolean drone;


    public ComParams(FacadeCom com, typeContenu cont, Informations info, boolean dr) {
        this.com = com;
        this.sender = this.com.getSender();
        this.contenu = cont;
        this.info = info;
        this.drone = dr;
    }

    public ComParams(FacadeCom com, typeContenu cont, boolean dr) {
        this.com = com;
        this.sender = this.com.getSender();
        this.contenu = cont;
        this.info = null;
        this.drone = dr;
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

    public boolean isDrone() {
        return drone;
    }
}