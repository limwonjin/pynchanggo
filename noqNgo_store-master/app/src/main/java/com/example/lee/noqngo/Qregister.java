package com.example.lee.noqngo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Lee on 2017-05-14.
 */
@IgnoreExtraProperties
public class Qregister{
    private int keynum;
    private String name;
    private String email;

    public Qregister(){}

    public Qregister(String name, int keynum, String email){
        this.keynum=keynum;
        this.name=name;
        this.email=email;
    }

    public String getName() {
        return name;
    }
    public int getkeynum() {return keynum;}
    public String getemail(){return email;}
}
