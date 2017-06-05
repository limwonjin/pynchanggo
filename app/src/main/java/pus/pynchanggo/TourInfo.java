package pus.pynchanggo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

/**
 * Created by 임원진 on 2017-05-28.
 */
public class TourInfo implements Serializable ,ClusterItem{
    private String title;
    private double lat;
    private double lng;
    private String imageUrl;

    public TourInfo(){}

    public TourInfo(String title, double lat, double lng, String imageurl){
        this.title=title;
        this.lat= lat;
        this.lng=lng;
        this.imageUrl=imageurl;
    }

    public String getTitle(){return title;}
    public double getLat(){return lat;}
    public double getLng(){return lng;}
    public String getImageUrl(){return imageUrl;}
    @Override
    public LatLng getPosition() {
        return new LatLng(lng,lat);
    }
}
