package pus.pynchanggo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Iterator;


/**
 * Created by 임원진 on 2017-05-03.
 */
public class MakerPopUp extends Activity implements GestureDetector.OnGestureListener {

    private TextView tv_title,tv_info,tv_num, tv_remind;
    private ImageButton btn_exit;
    private ViewGroup container;
    private ImageView frontview;
    private ImageView backview;
    private float centerX;
    private float centerY;
    private int DURATION = 100;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private  String isturn ="false";

    private GestureDetector gestureScanner;
    private  QMarker pick;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_makerpopup);
        Intent intent = getIntent();
        pick = (QMarker) intent.getSerializableExtra("pickm");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_info =(TextView)findViewById(R.id.tv_info);
        tv_num =(TextView)findViewById(R.id.tv_num);
        tv_remind =(TextView)findViewById(R.id.tv_remind);
        btn_exit = (ImageButton)findViewById(R.id.btn_exit);
        container=(ViewGroup)findViewById(R.id.container);
        frontview =(ImageView)findViewById(R.id.front);
        backview =(ImageView)findViewById(R.id.back);
        gestureScanner = new GestureDetector(this);

        isturn =LoadingActivity.QMAP.getString(String.valueOf(pick.getKeynum()),null);
        if(isturn!=null && !isturn.equals("false")){
            frontview.setVisibility(View.GONE);
            backview.setVisibility(View.VISIBLE);
            tv_num.setText(isturn);
        }
        tv_title.setText(pick.getName());
        tv_info.setText(pick.getInfo());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("cnt").child(String.valueOf(pick.getKeynum()));
        databaseReference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        QCnt p = dataSnapshot.getValue(QCnt.class);
                        if(isturn.equals("false")){
                            setremind(Integer.toString(p.inqueue.size()));
                        }else{
                            int me = Integer.valueOf(isturn);
                            int qcnt=0;
                            Iterator iterator = p.inqueue.keySet().iterator();
                            while(iterator.hasNext()) {
                                if(me > p.inqueue.get((String) iterator.next())) qcnt++;
                            }
                            setremind(Integer.toString(qcnt));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void setremind(String s){
        tv_remind.setText(s+" 명 남았습니다.");
    }


    public void upload(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("cnt").child(String.valueOf(pick.getKeynum()));
        final String id = FirebaseInstanceId.getInstance().getToken();

        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                QCnt p = mutableData.getValue(QCnt.class);
                if (p == null || id ==null) {
                    return Transaction.success(mutableData);
                }
                if (!p.inqueue.containsKey(id) && isturn.equals("false")) {
                    p.qcount++;
                    p.inqueue.put(id, p.qcount);
                    isturn= String.valueOf(p.qcount);
                    LoadingActivity.QMAP_EDIT.putString(String.valueOf(pick.getKeynum()), isturn);
                    LoadingActivity.QMAP_EDIT.commit();
                }
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                if( !isturn.equals("false")){
                    frontview.setVisibility(View.GONE);
                    backview.setVisibility(View.VISIBLE);
                    tv_num.setText(isturn);
                }
                Log.d("upload", "postTransaction:onComplete:" + databaseError);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    public boolean onDown(MotionEvent e) {// down
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(isturn.equals("false")){
                    applyRotation(0f, 180f, 360f, 0f);
                    upload();
                }else{
                    Toast.makeText(getApplicationContext(),"이미 발급되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if(isturn.equals("false")){
                    applyRotation(0f, 180f, 360f, 0f);
                    upload();
                }else{
                    Toast.makeText(getApplicationContext(),"이미 발급되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

            }
        } catch (Exception e) {

        }
        return true;
    }
    public void onLongPress(MotionEvent e) {}
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }
    public void onShowPress(MotionEvent e) { }
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }


    private void applyRotation(float start, float mid, float end, float depth) {
        this.centerX = container.getWidth() / 2.0f;
        this.centerY = container.getHeight() / 2.0f;

        Rotate3DAnimation rot = new Rotate3DAnimation(start, mid, centerX, centerY, depth, true);
        rot.setDuration(DURATION);
        rot.setAnimationListener(new DisplayNextView(mid, end, depth));
        rot.setInterpolator(new AccelerateInterpolator(0.2f));
        container.startAnimation(rot);

    }

    private class DisplayNextView implements AnimationListener {
        private float mid;
        private float end;
        private float depth;

        public DisplayNextView(float mid, float end, float depth) {
            this.mid = mid;
            this.end = end;
            this.depth = depth;
        }

        public void onAnimationEnd(Animation animation) {
            container.post(new Runnable() {
                public void run() {
                    Rotate3DAnimation rot = new Rotate3DAnimation(mid, end, centerX, centerY, depth, false);
                    rot.setDuration(DURATION);
                    container.startAnimation(rot);
                }
            });
        }

        public void onAnimationStart(Animation animation) {}

        public void onAnimationRepeat(Animation animation) {}
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
