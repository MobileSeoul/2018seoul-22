package com.example.hyeminj.syolo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Board_Write extends AppCompatActivity implements View.OnClickListener{
    EditText title;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_write);
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
        title = (EditText)findViewById(R.id.title);
        content = (EditText)findViewById(R.id.content);

        if(v.getId() == R.id.ok){
            Intent intent = new Intent();
            if(title.getText().toString().length()>0 &&content.getText().toString().length()>0){
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("content",content.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }else if(title.getText().toString().length()>0){
                Toast.makeText(getApplicationContext(),"내용을 입력하세요",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"제목을 입력하세요",Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.cancel){
            finish();
        }
    }
}