package com.example.hyeminj.syolo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class seoul_chk extends AppCompatActivity  implements View.OnClickListener {
    EditText rating;
    EditText content;
    Bitmap bitmap;
    PhotoViewAttacher photoViewAttacher;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seoul_map);
        imageView = findViewById(R.id.img_seoul);
        imageView.setImageResource(R.drawable.seoul_chk);
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        TextView ok = (TextView)findViewById(R.id.ok);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
