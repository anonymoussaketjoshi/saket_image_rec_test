package com.saket.saket_image_rec_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {
    String kairos_id="4985f625";
    String kairos_key="4423301b832793e217d04bc44eb041d3";
    Kairos myKairos;
    TextView output;
    Bitmap tempImage;
    ImageView mImageView;
    String gallery = "MyGallery";
    EditText nameview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.output);
        nameview = (EditText) findViewById(R.id.nameview);
        mImageView = (ImageView) findViewById(R.id.image);
        myKairos = new Kairos();
        myKairos.setAuthentication(this,kairos_id,kairos_key);
    }
    //////////////////////TAKE PICTURE
    public void takePhoto(View view) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            tempImage = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(tempImage);
        }
    }
        ///////////////////KAIROS
    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String response) {
            // your code here!
            output.setText(response);
            try {
                JSONObject res = new JSONObject(response);
                JSONObject transaction = res.getJSONArray("images").getJSONObject(0).getJSONObject("transaction");
                if(transaction.getString("status").equals("success"))
                    output.setText(transaction.getString("subject_id"));
                else
                    output.setText("Person not recognized");
                //output.setText(res.getJSONArray("images").getJSONObject(0).getJSONArray("faces").getJSONObject(0).get("confidence").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFail(String response) {
            // your code here!
            output.setText(response);
            Log.d("KAIROS DEMO", response);
        }
    };

    /////////////////////////////////////BUTTONS
    public void detect(View view) throws UnsupportedEncodingException, JSONException {
        //String image = "http://media.kairos.com/liz.jpg";
        //Drawable obamadraw = getResources().getDrawable(R.drawable.obama);
        //Bitmap obama = ((BitmapDrawable) obamadraw).getBitmap();
        //myKairos.enroll(obama,"obama","tempimages",null, null,null,listener);
        if(tempImage!=null)
            myKairos.detect(tempImage,null,null,listener);
    }
    public void enroll(View view) throws UnsupportedEncodingException, JSONException {
        if(tempImage!=null){
            myKairos.enroll(tempImage,nameview.getText().toString(),gallery,null,null,null,listener);
        }
    }
    public void recognize(View view) throws UnsupportedEncodingException, JSONException {
        if(tempImage!=null){
            myKairos.recognize(tempImage,gallery,null,null,null,null,listener);
        }
    }
    ///////////////////////////////////////NEXT PAGE
    public void nextpage(View view){
        Intent nextpage = new Intent(this,offlineprocess.class);
        startActivity(nextpage);
    }
}
