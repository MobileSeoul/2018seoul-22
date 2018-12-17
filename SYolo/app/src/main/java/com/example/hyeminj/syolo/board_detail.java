package com.example.hyeminj.syolo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class board_detail extends AppCompatActivity {
    TextView id;
    TextView title;
    TextView content;
    TextView date;
    ListView comment;
    String id_str, title_str, content_str, date_str, comment_str, comment_date, comment_id,real_id, new_comment_str;
    long comment_num;
    String type = "comment";
    ImageButton comment_adding;
    SharedPreferences pref;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<comment_item> list_itemArrayList;
    private InputMethodManager imm;
    CommentAdapter adapter;
    String data_key;
    String data_key_now;
    String login_chk_str;
    TextView commentNum;
    ListView comment_listview;
    EditText new_comment;
    ImageView trash;
    ImageView login_chk;
    String now_user;
    String ID;
    String comment_login_chk_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        id = findViewById(R.id.id);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        date = findViewById(R.id.date);
        comment = findViewById(R.id.comment_listview);
        Intent intent = getIntent();
        login_chk = findViewById(R.id.login_type_chk);
        comment_adding = findViewById(R.id.comment_adding);
        commentNum = findViewById(R.id.comment_num);
        new_comment = findViewById(R.id.new_comment);
        trash = findViewById(R.id.trash);
        id_str = intent.getStringExtra("id");
        title_str = intent.getStringExtra("title");
        content_str = intent.getStringExtra("content");
        date_str = intent.getStringExtra("date");
        data_key_now = intent.getStringExtra("data_key_now");
        login_chk_str = intent.getStringExtra("login");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        content.setMovementMethod(new ScrollingMovementMethod());
        title.setMovementMethod(new ScrollingMovementMethod());
/*
 public void onDataChange(DataSnapshot dataSnapshot) {
                //lodge_like_size = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("addr").getValue(String.class)!=null){
                        if (snapshot.child("addr").getValue(String.class).equals(addr)) {
                            remove = snapshot.getKey();
                            favorite.setImageResource(R.drawable.like);
                            count = 1;
                        }
                    }
                }
            }

 */

        id.setText(id_str);
        title.setText(title_str);
        content.setText(content_str);
        date.setText(date_str);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        switch (login_chk_str){
            case "kakao":
                login_chk.setImageResource(R.drawable.kakao_chk);
                break;
            case "facebook":
                login_chk.setImageResource(R.drawable.facebook_chk);
                break;
            case "naver":
                login_chk.setImageResource(R.drawable.naver_chk);
                break;
        }

        comment_adding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new_comment.getText().length() != 0){
                    new_comment_str = new_comment.getText().toString();
                    imm.hideSoftInputFromWindow(new_comment.getWindowToken(), 0);
                    String content = new_comment_str;
                    new_comment.setText("");
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    String time = sdf.format(date);
                    String user_id = real_id;
                    //databaseReference.child("member").child(loginType).child(ID).child("Lodge");
                    final DatabaseReference push = databaseReference.child("board").child(data_key).child("comment").push();

                    push.child("comment_date").setValue(time);
                    push.child("comment_id").setValue(real_id);
                    push.child("comment_str").setValue(content);
                    push.child("user_id").setValue(ID);
                    push.child("comment_login").setValue(comment_login_chk_str);
                }

            }
        });
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        real_id =pref.getString("name","");
        ID = pref.getString("id","");
        comment_login_chk_str = pref.getString("login","");

        final DatabaseReference comment_check = databaseReference.child("board");
        comment_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lodge_like_size = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("title").getValue(String.class)!=null){
                        if (snapshot.child("title").getValue(String.class).equals(title_str) &&snapshot.child("user_id").getValue(String.class).equals(id_str) ) {
                            data_key = snapshot.getKey();
                            now_user = snapshot.child("ID").getValue(String.class);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list_itemArrayList = new ArrayList<comment_item>();
        adapter = new CommentAdapter(this,list_itemArrayList);
        comment_listview = (ListView)findViewById(R.id.comment_listview);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef = mDatabase.child("board").child(data_key_now).child("comment");

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_itemArrayList = new ArrayList<comment_item>();

               /*
                  push.child("comment_str").setValue(comment_str);
                push.child("comment_date").setValue(comment_date);
                push.child("comment_id").setValue(comment_id);

                */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String content = snapshot.child("comment_str").getValue(String.class);
                    String user_id = snapshot.child("comment_id").getValue(String.class);
                    String date = snapshot.child("comment_date").getValue(String.class);
                    String p = snapshot.child("comment_str").getValue(String.class);
                    String q = snapshot.child("comment_id").getValue(String.class);
                    String id = snapshot.child("user_id").getValue(String.class);
                    String login_type = snapshot.child("comment_login").getValue(String.class);
                    if(p!=null&&q!=null&&date!=null){
                        //String id, String content, String date
                        list_itemArrayList.add(new comment_item(user_id, content,date,data_key_now,id,login_type));
                        adapter = new CommentAdapter(board_detail.this,list_itemArrayList);
                        comment_listview.setAdapter(adapter);
                    }

                }
                comment_num = list_itemArrayList.size();
                commentNum.setText(String.valueOf(comment_num));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(board_detail.this);
                alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(now_user.equals(ID)){
                                    comment_check.child(data_key).removeValue();
                                    Intent i = new Intent(getApplicationContext(),BoardFragment.class);
                                    Toast.makeText(board_detail.this,"삭제되었습니다",Toast.LENGTH_LONG).show();
                                    startActivity(i);
                                }else{
                                    Toast.makeText(board_detail.this,"접근 권한이 없습니다!",Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

    }

}