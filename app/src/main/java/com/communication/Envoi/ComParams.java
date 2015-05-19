package com.communication.Envoi;

import com.communication.FacadeCom;
import com.message.Informations;
import com.message.Photo;
import com.message.typeContenu;

/**
 * Created by lucille on 15/04/15.
 */
public class ComParams {

    private UDPSender sender;
    private typeContenu contenu;
    private Informations info;
    private Photo photo;
    private FacadeCom com;
    private String affiche;
    private boolean drone;


    public ComParams(FacadeCom com, typeContenu cont, Informations info, boolean dr) {
        this.com = com;
        this.sender = this.com.getSender();
        this.contenu = cont;
        this.info = info;
        this.drone = dr;
        this.affiche="";
    }


    public ComParams(FacadeCom com, typeContenu cont, boolean dr) {
        this.com = com;
        this.sender = this.com.getSender();
        this.contenu = cont;
        this.info = null;
        this.drone = dr;
    }



    public ComParams(FacadeCom com, boolean dr, String msg) {
        this.com = com;
        this.sender = this.com.getSender();
        this.contenu = typeContenu.PRINT;
        this.info = null;
        this.drone = dr;
        this.affiche = msg;
    }

    public ComParams(FacadeCom com, typeContenu type, Photo photo, boolean dr) {
        this.com = com;
        this.sender = this.com.getSender();
        this.contenu = typeContenu.PHOTO;
        this.photo=photo;
        this.info = null;
        this.drone = dr;
        this.affiche = "";
    }

    public String getAffiche() {
        return affiche;
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

    public Photo getPhoto() {
        return photo;
    }


}
