package pus.pynchanggo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * Created by 임원진 on 2017-05-06.
 */
public class MenuActivitiy extends Activity{
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        Map<String, String> list = (Map<String,String>) LoadingActivity.QMAP.getAll();
        Intent intent =getIntent();
        ArrayList<QMarker> marker = (ArrayList < QMarker >)intent.getSerializableExtra("Mark");
        int n= list.size();
        final Vector<String> title =new Vector<>();
        final Vector<String> number =new Vector<>();
        final Vector<Integer> index = new Vector<>();
        for(int i=1;i<=n;i++){
            String s =list.get(Integer.toString(i));
             if(!s.equals("false")){
                 title.add(marker.get(i-1).getName());
                 number.add(s);
                 index.add(i);
             }
        }
        if(title.size()>0) {
            adapter = new MyAdapter(getApplicationContext(), R.layout.activity_qscroll, title, number);
            GridView gv = (GridView) findViewById(R.id.gridView);
            gv.setAdapter(adapter);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    LoadingActivity.QMAP_EDIT.putString(Integer.toString(index.get(i)),"false");
                    LoadingActivity.QMAP_EDIT.commit();
                    index.remove(i);
                    title.remove(i);
                    number.remove(i);
                    adapter.notifyDataSetChanged();

                }
            });
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.anim_slide_out_top);
        }
        return super.onKeyDown(keyCode, event);
    }

}
class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    Vector<String> title;
    Vector<String> number;
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, Vector<String> title, Vector<String> number) {
        this.context = context;
        this.layout = layout;
        this.title = title;
        this.number = number;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return title.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);
            convertView.setLayoutParams( new GridView.LayoutParams( 480, 600 ) );

        TextView tvt = (TextView) convertView.findViewById(R.id.tv_qtitle);
        TextView tvn = (TextView) convertView.findViewById(R.id.tv_qnum);
        tvt.setText(title.get(position));
        tvn.setText(number.get(position));

        return convertView;
    }
}

