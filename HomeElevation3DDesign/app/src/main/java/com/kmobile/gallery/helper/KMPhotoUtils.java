package com.kmobile.gallery.helper;

import com.kmobile.gallery.model.KMWallpaper;

import java.util.List;

/**
 * Created by root on 10/3/15.
 */
public class KMPhotoUtils {
    private static KMPhotoUtils obj = null;
    List<KMWallpaper> photolist;

    public static KMPhotoUtils getIntance(){
        if(obj == null)
            obj = new KMPhotoUtils();
        return obj;
    }

    public void set(List<KMWallpaper> data){
        photolist = data;
    }

    public List<KMWallpaper> get(){
        return photolist;
    }
}
