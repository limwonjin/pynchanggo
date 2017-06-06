package pus.pynchanggo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private SensorManager mSensorManager;
    private boolean mCompassEnabled;
    private  ArrayList<QMarker> qMarkerVector;
    private  ArrayList<TourInfo> qInfoVector;
    Context context;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = getApplicationContext();

        intent = getIntent();
        MapFragment fragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        fragment.getMapAsync(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        startLocationService();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuintent = new Intent(MapsActivity.this, MenuActivitiy.class);
                menuintent.putExtra("Mark",qMarkerVector);
                menuintent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(menuintent);
                overridePendingTransition(R.anim.anim_slide_in_top,R.anim.anim_slide_out_bottom);
            }
        });

    }

    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(false);
        }
        if (mCompassEnabled) {
            mSensorManager.unregisterListener(mListener);
        }
    }


    /** Called when the map is ready. */
    @Override
    public void onMapReady(GoogleMap maps) {
        map = maps;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        if (mCompassEnabled) {
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        }
        qMarkerVector =(ArrayList<QMarker>)intent.getSerializableExtra("Mark");
        qInfoVector =(ArrayList<TourInfo>)intent.getSerializableExtra("info");

        ClusterManager<myMarker> mClusterManager = new ClusterManager<>(this, map);
        map.setOnCameraIdleListener(mClusterManager);
        for(int i=0;i<qInfoVector.size();i++){
            TourInfo ifo = qInfoVector.get(i);
            mClusterManager.addItem(new myMarker(ifo.getLng(),ifo.getLat(),ifo.getTitle(),0,i));
        }

        for(int i = 0;i<qMarkerVector.size();i++){
            QMarker temp = qMarkerVector.get(i);
            mClusterManager.addItem(new myMarker(temp.getLat(),temp.getLng(),temp.getName(),1,i));

        }
        mClusterManager.setOnClusterItemClickListener(mClusterItemClickListener);
        map.setOnMarkerClickListener(mClusterManager);
    }
    public ClusterManager.OnClusterItemClickListener<myMarker> mClusterItemClickListener = new ClusterManager.OnClusterItemClickListener<myMarker>() {
        @Override
        public boolean onClusterItemClick(myMarker item) {
            int tags = item.getTag();
            if(tags==1){
                Intent popup = new Intent(MapsActivity.this, MakerPopUp.class);
                popup.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                QMarker pickq = qMarkerVector.get(item.getNum());
                popup.putExtra("pickm", pickq);
                startActivity(popup);
            }else{
                Intent popup = new Intent(MapsActivity.this, TourInfoPopUp.class);
                popup.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                TourInfo picki = qInfoVector.get(item.getNum());
                popup.putExtra("picki", picki);
                startActivity(popup);
            }

            return true;
        }
    };


    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
        }catch (SecurityException e){
            Log.e("startLocation ERROR", e+"\n");
        }
    }

    private class GPSListener implements LocationListener{
        public void onLocationChanged(Location location){
            Double latitude = location.getLatitude();
            Double longtitude = location.getLongitude();

            String msg = "Latitude : "+latitude + "\nLongtitude:"+ longtitude;
            Log.i("GPSLocationService",msg);

            showCurrentLocation(latitude, longtitude);
        }
        public void onProviderDisabled(String provider){
        }
        public void onProviderEnabled(String provider){
        }
        public void onStatusChanged(String provider, int status, Bundle extracts){}

    }
    private void showCurrentLocation(Double latitude ,Double longtitude){
        LatLng curPoint = new LatLng(latitude, longtitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private final SensorEventListener mListener  = new SensorEventListener() {
        private int iOrientation = -1;
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        public void onSensorChanged(SensorEvent event){
            if(iOrientation<0){
                iOrientation =((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }
        }
    };

    public boolean onCreateOptionMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id =item.getItemId();
        if(id ==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class myMarker implements ClusterItem{
        private LatLng position;
        private String title;
        private int tag;
        private int num;
        public myMarker(double lat, double lng, String titles,int tag ,int num ){
            this.position = new LatLng(lat,lng);
            this.title = titles;
            this.tag= tag;
            this.num = num;
        }
        @Override
        public LatLng getPosition(){ return position; }
        public int getTag(){return tag;}
        public int getNum(){return num;}


    }
}






