package com.example.hyeminj.syolo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.transform.Templates;

public class review_write extends AppCompatActivity implements View.OnClickListener {

    EditText rating;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        TextView cancel = (TextView)findViewById(R.id.cancel);
        TextView ok = (TextView)findViewById(R.id.ok);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        rating = (EditText)findViewById(R.id.rating);
        content = (EditText)findViewById(R.id.content);

        if(v.getId() == R.id.ok){
            if(rating.getText().toString().length()>0&&content.getText().toString().length()>0){
                Intent intent = new Intent();
                intent.putExtra("rating",rating.getText().toString());
                intent.putExtra("content",content.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        }else if(v.getId() == R.id.cancel){
            finish();
        }
    }
}
