package com.saket.saket_image_rec_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class offlineprocess extends AppCompatActivity {
    String kairos_id="4985f625";
    String kairos_key="4423301b832793e217d04bc44eb041d3";
    Kairos myKairos;
    File folder;
    String[] images;
    Bitmap img;
    TextView filesview;
    ImageView imageView;
    int imgno=0;
    ///////////////VIEW WAS ADDED, in networkconnected;
    View view;

    ImageView timeProgress;
    PieProgressDrawable pieProgressDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offlineprocess);
        imageView = (ImageView) findViewById(R.id.imageView);
        filesview = (TextView) findViewById(R.id.filesview);
        folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        images = folder.list();
        myKairos = new Kairos();
        myKairos.setAuthentication(this,kairos_id,kairos_key);
        pieProgressDrawable = new PieProgressDrawable();
        pieProgressDrawable.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        timeProgress = (ImageView) findViewById(R.id.time_progress);
        timeProgress.setImageDrawable(pieProgressDrawable);
        updateTime(0);
        /*for(int i=0;i<images.length;++i){
            filesview.setText(filesview.getText().toString()+"\n"+images[i]);
            Bitmap img = BitmapFactory.decodeFile(folder.getPath()+"/"+images[i]);
            try {
                myKairos.detect(img,null,null,listener);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/
    }
    public void processImage(View view){
        img = BitmapFactory.decodeFile(folder.getPath() + "/" + images[imgno]);
        filesview.setText(filesview.getText().toString() + "\n"+"Processing image: "+images[imgno]);
        imageView.setImageBitmap(img);
        if(img==null)
            Toast.makeText(this,"no image",Toast.LENGTH_SHORT).show();
        else
            sendPOST();
    }
    public void sendPOST(){
        try {
            myKairos.detect(img, null, null, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String response) {
            if(response!=null && response.substring(0,8).equals("{\"Errors"))
                filesview.setText(filesview.getText().toString()+"\n"+"No face in image");
            else {
                File img = new File(folder,images[imgno]);
                if(img.delete())
                    filesview.setText(filesview.getText().toString() + "\n" + "Face Found => Image Deleted");
                else
                    filesview.setText(filesview.getText().toString() + "\n" + "Face Found => Image couldn't be Deleted");
            }
            updateTime((imgno+1)*100/images.length);
            imgno=imgno+1;
            if(imgno<images.length)
                processImage(filesview);
        }
        @Override
        public void onFail(String response) {
            //Toast.makeText(null,"FAILED RESPONSE",Toast.LENGTH_SHORT).show();
            // your code here!
            Log.d("KAIROS DEMO", response);
        }
    };
    ////////////////////    PROGRESS PIE UPDATE
    public void updateTime(int progress) {
        pieProgressDrawable.setLevel(progress);
        timeProgress.invalidate();
    }
    private boolean isNetworkConnected() {
        this.processImage(view);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
