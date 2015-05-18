package com.message;

/**
 * Created by sony on 17/05/2015.
 */
public class Photo extends AbstractMessage {
    private byte[] image;
    public Photo(byte[]data){
        this.setImage(data);
        this.type=typeContenu.PHOTO;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
