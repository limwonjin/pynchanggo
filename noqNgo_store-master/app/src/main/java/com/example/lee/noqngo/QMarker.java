package com.example.lee.noqngo;

import java.io.Serializable;

/**
 * Created by 임원진 on 2017-05-02.
 */
public class QMarker implements Serializable {
    private String info;
    private int keynum;
    private double lat;
    private double lng;
    private String name;

    public QMarker(){}

    public QMarker(String info, int keynum, double lat, double lng, String name){
        this.keynum=keynum;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.info=info;
    }


    public int getKeynum(){
        return keynum;
    }
    public String getName(){
        return name;
    }
    public double getLat(){
        return lat;
    }
    public double getLng(){return lng;}
    public String getInfo(){
        return info;
    }


}
