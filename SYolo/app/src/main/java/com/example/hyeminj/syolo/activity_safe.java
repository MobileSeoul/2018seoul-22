package com.example.hyeminj.syolo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class activity_safe extends AppCompatActivity {
    ImageButton police;
    ImageButton fire;
    ImageButton hospital;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
     //   police = (ImageButton) findViewById(R.id.police_officer);

       // hospital = (ImageButton) findViewById(R.id.doctor);

        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), hospital_info.class);
                startActivity(intent);
            }
        });
    }
}