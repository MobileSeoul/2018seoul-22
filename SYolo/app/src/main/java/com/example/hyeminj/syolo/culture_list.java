package com.example.hyeminj.syolo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;


public class culture_list extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_list);

        TableRow musical = (TableRow) findViewById(R.id.musical_frame);
        TableRow festival = (TableRow) findViewById(R.id.festival_frame);
        TableRow play = (TableRow) findViewById(R.id.play_frame);
        TableRow gallery = (TableRow) findViewById(R.id.gallery_frame);
        TableRow etc = (TableRow) findViewById(R.id.etc_frame);

        musical.setOnClickListener(this);
        festival.setOnClickListener(this);
        play.setOnClickListener(this);
        gallery.setOnClickListener(this);
        etc.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.musical_frame :
                i = new Intent(this, musical_list.class);
                startActivity(i);
                break;
            case R.id.play_frame :
                i = new Intent(this, play_list.class);
                startActivity(i);
                break;
            case R.id.festival_frame :
                i = new Intent(this, festival_list.class);
                startActivity(i);
                break;
            case R.id.gallery_frame :
                i = new Intent(this, gallery_list.class);
                startActivity(i);
                break;
            case R.id.etc_frame :
                i = new Intent(this, etc_list.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
