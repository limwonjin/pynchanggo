package pus.pynchanggo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by 임원진 on 2017-05-02.
 */
public class QMarker implements Serializable,ClusterItem {
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
    @Override
    public LatLng getPosition() {
        return new LatLng(lat,lng);
    }
}
