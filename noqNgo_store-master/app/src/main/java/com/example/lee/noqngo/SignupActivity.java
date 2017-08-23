package com.example.lee.noqngo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.noqngo.common.activities.SampleActivityBase;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lee on 2017-05-03.
 */
public class SignupActivity extends SampleActivityBase implements PlaceSelectionListener {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private EditText inputEmail, inputPassword, inputName, inputAddress, inputInfo, inputAddres, inputPhone, inputWebsite, inputLonlat;
    private Button  btnSignUp;
    private FirebaseAuth auth;
    private TextView mPlaceDetailsText;
    private TextView mPlaceAttribution;
    ProgressDialog dialog = null;
    private QCnt userData;
    private QMarker markerData;
    private Qregister registers;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Retrieve the PlaceAutocompleteFragment. //Auto place Complete 자동 장소 입력
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        // Retrieve the TextViews that will display details about the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceAttribution = (TextView) findViewById(R.id.place_attribution);

        //Get Firebase auth instance 파이어베이스
        auth = FirebaseAuth.getInstance();
        userData = new QCnt();

        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText)findViewById(R.id.name);
        inputInfo = (EditText)findViewById(R.id.info);
        inputAddres = (EditText)findViewById(R.id.address);
        inputLonlat = (EditText)findViewById(R.id.lonlat);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usernums a =dataSnapshot.getValue(usernums.class);
                userData.keynum = a.usernum;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String Name = inputName.getText().toString().trim();
                String Info=inputInfo.getText().toString().trim();
               // String Address=inputAddress.getText().toString().trim();
                String lonlat = inputLonlat.getText().toString().trim();
               // String Phone=inputPhone.getText().toString().trim();
               // String Website=inputWebsite.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(v, "Enter email address!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(v, "Enter password!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                   // Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Snackbar.make(v, "Password too short, enter minimum 6 characters!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                 //   Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                userData.qcount = 0; //큐카운트 0 초기화
                markerData = new QMarker(Info,userData.keynum,lat,lon,Name); //큐마커에 userdata, keynum, lat ,lon, name 넣기
                databaseReference.child("cnt").child(String.valueOf(userData.keynum)).setValue(userData);
                registers = new Qregister(Name,userData.keynum,email);
                /*
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("key", String.valueOf(userData.keynum));
                editor.commit();
                Log.d("key",pref.getString("key",""));
                */
                databaseReference.child("marker").child(String.valueOf(userData.keynum)).setValue(markerData);
                databaseReference.child("usernum").setValue(userData.keynum+1);
            //    databaseReference.child("register").child(email).setValue(registers);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Snackbar.make(v, "Password too short, enter minimum 6 characters!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                Toast.makeText(SignupActivity.this, "회원가입?:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "회원가입 실패." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    //Snackbar.make(v, "회원가입 성공", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    //Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                    databaseReference.child("register").child(auth.getCurrentUser().getUid()).setValue(registers);
                                    startActivity(new Intent(SignupActivity.this, getuserdata.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }
    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        inputAddres.setText(place.getAddress());
        inputName.setText(place.getName());
        inputLonlat.setText(place.getLatLng().toString());
        lat = place.getLatLng().latitude;
        lon = place.getLatLng().longitude;
       // inputInfo.setText(place.getPlaceTypes().get(0));
//        inputPhone.setText(place.getPhoneNumber());

        // Format the returned place's details and display them in the TextView.
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getLatLng(),
                place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
        } else {
            mPlaceAttribution.setText("");
        }
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     * 자동 장소 입력
     */
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, LatLng LatLng,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, LatLng, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, LatLng, address, phoneNumber,
                websiteUri));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }
}

class usernums{
    public int usernum;
    usernums(){}
    usernums(int s){
        usernum=s;
    }
}
