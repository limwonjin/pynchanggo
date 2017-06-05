package pus.pynchanggo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.UUID;

public class LoadingActivity extends Activity {

    public final  int MY_PERMISSIONS_REQUEST =1;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private ArrayList<QMarker> q = new ArrayList<QMarker>();
    private ArrayList<TourInfo> ti = new ArrayList<TourInfo>();
    private Intent targetintent;
    private ProgressDialog dialog;
    public static SharedPreferences PUSH;
    public static SharedPreferences.Editor PUSH_EDIT;
    public static SharedPreferences QMAP;
    public static SharedPreferences.Editor QMAP_EDIT;

    protected volatile static UUID uuid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    protected void onResume(){
        super.onResume();
        PUSH = getSharedPreferences("pushed", Activity.MODE_PRIVATE);
        PUSH_EDIT = PUSH.edit();
        QMAP = getSharedPreferences("qmap", Activity.MODE_PRIVATE);
        QMAP_EDIT = QMAP.edit();

        checkPermission();
    }

    private Boolean isNetWork(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
            return true;
        }else{
            Toast.makeText(getApplicationContext(),"네트워크연결 확인해 주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void parseInfo(){
        databaseReference.child("Tourinfo").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            TourInfo qinfo = child.getValue(TourInfo.class);
                            ti.add(qinfo);
                        }
                        parseMark();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }});
    }

    private void parseMark(){
        databaseReference.child("marker").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            QMarker qMarker = child.getValue(QMarker.class);
                            String temp = QMAP.getString(String.valueOf(qMarker.getKeynum()), "no");
                            if (temp.equals("no")) {
                                QMAP_EDIT.putString(String.valueOf(qMarker.getKeynum()), "false");
                            }
                            q.add(qMarker);
                        }
                        QMAP_EDIT.commit();
                        if (PUSH.getBoolean("ispush", false)) {
                            targetintent = new Intent(LoadingActivity.this, PushActivity.class);
                            Intent intent = getIntent();
                            String s = intent.getStringExtra("key");
                            targetintent.putExtra("key", s);
                            LoadingActivity.PUSH_EDIT.putBoolean("ispush", false).commit();
                        } else {
                            targetintent = new Intent(LoadingActivity.this, MapsActivity.class);
                        }
                        targetintent.putExtra("Mark", q);
                        targetintent.putExtra("info", ti);
                        dialog.dismiss();
                        startActivity(targetintent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void parsing(){
        if (isNetWork()) {//network 연결이 되어있을 때
            parseInfo();
        }
    }

    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS를 켜야 합니다. 설정창으로 가시겠습니까?")
                .setCancelable(false)
                                .setPositiveButton("예",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                moveConfigGPS();
                                            }
                                        })
                                        .setNegativeButton("아니오",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    private void isonGps(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
            Toast.makeText(getApplicationContext(), "GPS 를 켜세요,", Toast.LENGTH_SHORT).show();
        }else{// 켜져있는 경우
            dialog = new ProgressDialog(LoadingActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading.. ");
            dialog.show();
            parsing();
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
        }else{
            isonGps();
        }
    }
    //권한 체크관련
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                    isonGps();
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다

                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    Toast.makeText(getApplicationContext(), "권한 없이 해당 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();

                }
                return;
        }
    }



}
