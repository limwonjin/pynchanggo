package pus.pynchanggo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 임원진 on 2017-05-08.
 */
public class PushActivity extends Activity {

    TextView tv_title, tv_info, tv_num, tv_remind;
    private ImageButton btn_exit;
    private ImageView frontview;
    private ImageView backview;
    private String isturn = "false";
    private  ArrayList<QMarker> marker;
    private  ArrayList<TourInfo> info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makerpopup);
        Intent intent = getIntent();
        int keyn = Integer.valueOf(intent.getStringExtra("key")) - 1;
        marker = (ArrayList<QMarker>) intent.getSerializableExtra("Mark");
        info =  (ArrayList<TourInfo>) intent.getSerializableExtra("info");

        QMarker q = marker.get(keyn);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_remind = (TextView) findViewById(R.id.tv_remind);
        btn_exit = (ImageButton)findViewById(R.id.btn_exit);
        frontview =(ImageView)findViewById(R.id.front);
        backview =(ImageView)findViewById(R.id.back);

        frontview.setVisibility(View.GONE);
        backview.setVisibility(View.VISIBLE);
        tv_title.setText(q.getName());
        tv_info.setText(q.getInfo());
        tv_title.setTextColor(Color.parseColor("#000000"));
        tv_info.setTextColor(Color.parseColor("#000000"));
        tv_remind.setTextColor(Color.parseColor("#000000"));
        isturn = LoadingActivity.QMAP.getString(String.valueOf(q.getKeynum()), null);
        tv_num.setText(isturn);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gointent = new Intent(PushActivity.this, MapsActivity.class);
                gointent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                gointent.putExtra("Mark", marker);
                gointent.putExtra("info",info);
                startActivity(gointent);
                finish();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("cnt").child(String.valueOf(q.getKeynum()));
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QCnt p = dataSnapshot.getValue(QCnt.class);
                        int me = Integer.valueOf(isturn);
                        int qcnt = 0;
                        Iterator iterator = p.inqueue.keySet().iterator();
                        while (iterator.hasNext()) {
                            if (me > p.inqueue.get((String) iterator.next())) qcnt++;
                        }
                        setremind(Integer.toString(qcnt));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


    }

    public void setremind(String s) {
        tv_remind.setText(s + " 명 남았습니다.");
    }
}
