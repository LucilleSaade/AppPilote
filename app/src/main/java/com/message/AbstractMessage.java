package com.message;
import java.io.Serializable;


@SuppressWarnings("serial")
public abstract class AbstractMessage implements Serializable {


    // Identifie la nature du message envoye
    protected typeContenu type;




    //////////////////////////////////////////
    //         GETTER ET SETTER             //
    //////////////////////////////////////////

    public typeContenu getTypeContenu(){
        return this.type;
    }

    public void setTypeContenu(typeContenu type){
        this.type = type;
    }
}
