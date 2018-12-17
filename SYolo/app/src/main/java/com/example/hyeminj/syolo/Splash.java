package com.example.hyeminj.syolo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifTextView;

public class Splash extends AppCompatActivity {

    private ImageView iv;
    private GifTextView gifTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //iv= (ImageView) findViewById(R.id.iv);
        //Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        //iv.startAnimation(myanim);

        gifTextView = (GifTextView)findViewById(R.id.gifTextView);

        //set gifimageview resource

        //wait for 3 seconds and start activity main
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Splash.this.startActivity(new Intent(Splash.this,LoginActivity.class));
                Splash.this.finish();
            }
        },5000);
       /* final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread(){
            public void run () {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };

        timer.start();*/
    }
}