package com.example.lee.noqngo;

import java.util.Comparator;

/**
 * Created by jang on 2017-06-04.
 */

public class Griditem {

    public int num;
    private String token;
    public  Griditem(){
        this.num = 999;
        this.token = "999";
    }
    public Griditem(int num, String token) {
        this.num=num;
        this.token=token;
    }

    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

}

class MemberComparator implements Comparator {
    public int compare(Object arg0, Object arg1) {
        return ((Griditem)arg0).num > ((Griditem)arg1).num ? 1: ((Griditem)arg0).num == ((Griditem)arg1).num  ? 0 : -1; } }