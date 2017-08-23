package pus.pynchanggo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 임원진 on 2017-05-08.
 */
public class PushActivity extends Activity {

    TextView tv_title, tv_num, tv_remind;
    private  ArrayList<QMarker> marker;
    private  ArrayList<TourInfo> info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        Intent intent = getIntent();
        int keyn = Integer.valueOf(intent.getStringExtra("key")) - 1;
        marker = (ArrayList<QMarker>) intent.getSerializableExtra("Mark");
        info =  (ArrayList<TourInfo>) intent.getSerializableExtra("info");

        QMarker q = marker.get(keyn);
        tv_title = (TextView) findViewById(R.id.tv_ptitle);
        tv_num = (TextView) findViewById(R.id.tv_pnum);
        tv_remind = (TextView) findViewById(R.id.tv_prm);

        tv_title.setText(q.getName());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final String id = FirebaseInstanceId.getInstance().getToken();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("cnt").child(String.valueOf(q.getKeynum()));
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QCnt p = dataSnapshot.getValue(QCnt.class);
                        int me = Integer.valueOf(p.inqueue.get(id));
                        int qcnt = 0;
                        Iterator iterator = p.inqueue.keySet().iterator();
                        while (iterator.hasNext()) {
                            if (me > p.inqueue.get((String) iterator.next())) qcnt++;
                        }
                        setremind(Integer.toString(qcnt),Integer.toString(me));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


    }

    public void setremind(String s, String num) {
        tv_remind.setText("대기인원 : "+ s );
        tv_num.setText(num);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent gointent = new Intent(PushActivity.this, MapsActivity.class);
                gointent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                gointent.putExtra("Mark", marker);
                gointent.putExtra("info",info);
                startActivity(gointent);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
