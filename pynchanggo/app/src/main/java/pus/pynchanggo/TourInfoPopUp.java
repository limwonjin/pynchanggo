package pus.pynchanggo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by 임원진 on 2017-05-28.
 */
public class TourInfoPopUp extends Activity {
    TourInfo pick;
    TextView title;
    ImageView iv;
    Bitmap bm;
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_tourinfopopup);
        Intent intent = getIntent();
        pick = (TourInfo) intent.getSerializableExtra("picki");
        title = (TextView)findViewById(R.id.info_title);
        title.setText(pick.getTitle());
        iv = (ImageView) findViewById(R.id.imageView);
        new Thread(new Runnable(){
            @Override
            public void run() {
                downloadimage(pick.getImageUrl());
            }
        }).start();

    }

    public void downloadimage(String imurl) {
        if (!imurl.equals("no")) {
            try{
                URL url = new URL(imurl);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv.setImageBitmap(bm);
                    }
                });
            }
            catch (IOException e) {

        }

        }
    }

}
