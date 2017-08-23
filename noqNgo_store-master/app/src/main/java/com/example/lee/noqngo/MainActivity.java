package com.example.lee.noqngo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA3P78EyQ:APA91bEr9D1AiXux8IrJyqFDiMCNJA0RPdLorOJF880mF661OebBFuY0gfXaCyMRwMZdqld93Nz7s9nhxwl90lSpwf6grNVFPiuNVfPP_iGilssNIfbEZ9fYkPWeqwFc-DYST-lzWXgS";
    public FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private ArrayAdapter<String> adapter;
    //private ListView listView1;
    private TextView textView1;
    private int keyval;
    private String name;
    private String email,uid;

    // 추가 0604
    private List<Griditem> Griditem_;
    private GridAdapter GridAdapter_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //추가 0604
        final GridView gridview = (GridView)findViewById(R.id.gridview);
        Griditem_ = new ArrayList<Griditem>();
        GridAdapter_ = new GridAdapter(this, R.layout.list_item, Griditem_);
        gridview.setAdapter(GridAdapter_);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayAdapter<String> adapter;
        //  FirebaseMessaging.getInstance().subscribeToTopic("notice");
        //  FirebaseInstanceIDService A ;

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final int keyval = intent.getIntExtra("keyval",0);
        auth = FirebaseAuth.getInstance(); //get firebase auth instance
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //get current user
        email =  user.getEmail();
        final String uid = user.getUid();
        TextView textView1 = (TextView)findViewById(R.id.key) ;
        textView1.setText(name);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        //드로어
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View Navi_header = navigationView.getHeaderView(0);
        TextView Head_text = (TextView) Navi_header.findViewById(R.id.store_name);
        Head_text.setText(name);
        navigationView.setNavigationItemSelectedListener(this);

        //고객리스트



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Griditem data = (Griditem)adapterView.getAdapter().getItem(i);
                final String Data_ = data.getToken();
                Toast.makeText(getApplicationContext(), "고객 호출", Toast.LENGTH_LONG).show();
                //     final String Data = (String)adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // FMC 메시지 생성 start
                            JSONObject root = new JSONObject();
                            JSONObject notification = new JSONObject();
                            notification.put("sid",uid);
                            notification.put("body", keyval); //여기 가게 번호 넣자
                            notification.put("title",name);//여기 가게이름 넣고
                            root.put("data", notification);
                            // 여기 수정
                            root.put("to", Data_);
                            // FMC 메시지 생성 end

                            URL Url = new URL(FCM_MESSAGE_URL);
                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setRequestProperty("Content-type", "application/json");
                            conn.addRequestProperty("url",FCM_MESSAGE_URL);
                            OutputStream os = conn.getOutputStream();
                            os.write(root.toString().getBytes("utf-8"));
                            os.flush();
                            conn.getResponseCode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Griditem data = (Griditem)adapterView.getAdapter().getItem(i);
                final String Data_ = data.getToken();

                databaseReference.child("cnt").child(Integer.toString(keyval)).child("inqueue").child(Data_).removeValue();
                databaseReference.child("UserQueue").child(Data_).child(Integer.toString(keyval)).removeValue();
                Toast.makeText(getApplicationContext(), "삭제", Toast.LENGTH_LONG).show();
                for(int j = 0; j<4; j++){
                    try {
                        Griditem data2 = (Griditem) adapterView.getAdapter().getItem(j);
                    }
                    catch(Exception e){
                        break;
                    }
                    Griditem data2 = (Griditem) adapterView.getAdapter().getItem(j);
                    if(data2.getNum() == 999){
                        break;
                    }
                    final String Data_2 = data2.getToken();
                    Toast.makeText(getApplicationContext(), "호출3", Toast.LENGTH_LONG).show();
                    //     final String Data = (String)adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // FMC 메시지 생성 start
                                JSONObject root = new JSONObject();
                                JSONObject notification = new JSONObject();
                                notification.put("sid",uid);
                                notification.put("body", keyval); //여기 가게 번호 넣자
                                notification.put("title",name);//여기 가게이름 넣고
                                root.put("data", notification);
                                // 여기 수정
                                root.put("to", Data_2);
                                // FMC 메시지 생성 end

                                URL Url = new URL(FCM_MESSAGE_URL);
                                HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setDoOutput(true);
                                conn.setDoInput(true);
                                conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                conn.setRequestProperty("Accept", "application/json");
                                conn.setRequestProperty("Content-type", "application/json");
                                conn.addRequestProperty("url",FCM_MESSAGE_URL);
                                OutputStream os = conn.getOutputStream();
                                os.write(root.toString().getBytes("utf-8"));
                                os.flush();
                                conn.getResponseCode();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                return true;
            }

        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // 새로운 구조에 대한 이벤트 리스너 틀
        databaseReference.child("cnt").child(Integer.toString(keyval)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final QCnt userData = dataSnapshot.getValue(QCnt.class);  // chatData를 가져오고
                Iterator iterator = userData.inqueue.keySet().iterator();

                GridAdapter_.clear();
                int i = 1;
                Vector<Griditem> plate_vec = new Vector<Griditem>();


                plate_vec.clear();
                while (iterator.hasNext()) {
                    String temp = (String) iterator.next();
                    int numvalue = userData.inqueue.get(temp);
                    Griditem plate= new Griditem(numvalue,temp);
                    plate_vec.add(plate);
                    plate = null;
                    System.gc();
                }
                Collections.sort(plate_vec, new MemberComparator());

                for(int j = 0; j<plate_vec.size();j++){
                    GridAdapter_.add(plate_vec.get(j));
                }
                plate_vec = null;
                System.gc();
                GridAdapter_.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, QRcodeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            //nfc sender
            Intent intent2 = new Intent(MainActivity.this, NFCtagActivity.class);
            startActivity(intent2);

        } else if (id == R.id.nav_manage) {
            //로그아웃
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        auth.signOut();
    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}

