package com.message;

import java.util.ArrayList;

/**
 * Created by Emma on 29/05/2015.
 */
public class PhotoSize extends AbstractMessage{

    private int fileSize;//taille en bytes du fichier


    public PhotoSize(int fileSize){
        this.fileSize=fileSize;
    }

    public int getSize() {
        return fileSize;
    }


}
