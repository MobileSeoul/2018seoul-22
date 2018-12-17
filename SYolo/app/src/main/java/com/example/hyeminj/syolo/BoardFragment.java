package com.example.hyeminj.syolo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class BoardFragment extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private ImageButton add;
    String title, content, id,loginType;

    ArrayList<board_item> arrayList;
    SharedPreferences pref;
    private SimpleAdapter adapter = null;
    BoardAdapter boardAdapter;
    FirebaseDatabase firebaseDatabase;
    SearchView search;
    DatabaseReference databaseReference;
    String type = "board";
    String data_key;
    String ID;

    ArrayList<board_item> list_itemArrayList;
    ListView listviewPhtoList;
    int i = 0;
    long borad_size;



    public BoardFragment() {
        // Required empty public constructor
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        arrayList = new ArrayList<board_item>();
        listviewPhtoList = (ListView) findViewById(R.id.listview_board);
        search = findViewById(R.id.search);
        add = (ImageButton)findViewById(R.id.adding);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BoardFragment.this,Board_Write.class);
                startActivityForResult(i,1);
            }

        });

        /*
           if(v.getId() == R.id.ok){
            Intent intent = new Intent(getApplicationContext(), BoardFragment.class);
            intent.putExtra("title",title.getText().toString());
            intent.putExtra("content",content.getText().toString());
            setResult(RESULT_OK,intent);
            startActivity(intent);
            finish();
        }
         */

        search.setOnQueryTextListener(this);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        id =pref.getString("name","");
        ID = pref.getString("id","");
        listviewPhtoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), board_detail.class);
                intent.putExtra("title", list_itemArrayList.get(position).getTitle());
                intent.putExtra("content",list_itemArrayList.get(position).getContent());
                intent.putExtra("date",list_itemArrayList.get(position).getDate());
                intent.putExtra("id",list_itemArrayList.get(position).getId());
                intent.putExtra("data_key_now",list_itemArrayList.get(position).getData_key());
                intent.putExtra("login",list_itemArrayList.get(position).getLogin());
                startActivity(intent);
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //   DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        //   DatabaseReference mConditionRef = mDatabase.child("getActivity().getIntent().getStringExtra(\"content\")");
        list_itemArrayList = new ArrayList<board_item>();
        boardAdapter = new BoardAdapter(this,list_itemArrayList);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef = mDatabase.child("board");


        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                list_itemArrayList = new ArrayList<board_item>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String content = snapshot.child("content").getValue(String.class);
                    String user_id = snapshot.child("user_id").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String time = snapshot.child("date").getValue(String.class);
                    String p = snapshot.child("user_id").getValue(String.class);
                    String real_id = snapshot.child("real_id").getValue(String.class);
                    String id = snapshot.child("ID").getValue(String.class);
                    String login = snapshot.child("login").getValue(String.class);
                    if(p!=null && p.equals(user_id)){
                        //String id, String content, String date, String title
                        Collections.reverse(list_itemArrayList);
                        list_itemArrayList.add(new board_item(user_id,content,time,title,snapshot.getKey(),id,login));
                        Collections.reverse(list_itemArrayList);
                        boardAdapter= new BoardAdapter(BoardFragment.this,list_itemArrayList);
                        listviewPhtoList.setAdapter(boardAdapter);
                    }
                }
                arrayList.addAll(list_itemArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                //  Intent intent = new Intent(getApplicationContext(), Board_Write.class);
                String title = data.getStringExtra("title");
                String content = data.getStringExtra("content");
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                final DatabaseReference push =  databaseReference.child("board").push();

                /*
                push.child("title").setValue(title);
                push.child("type").setValue(type);
                push.child("addr").setValue(addr);
                push.child("tel").setValue(tel);
                 */
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time = sdf.format(date);
                String user_id = id;
                push.child("user_id").setValue(user_id);
                push.child("content").setValue(content);
                push.child("title").setValue(title);
                push.child("date").setValue(time);
                push.child("ID").setValue(ID);
                push.child("login").setValue(loginType);
            }
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return false;
    }
    private void search(String newText) {
        list_itemArrayList.clear();

        if(newText.length() == 0){
            list_itemArrayList.addAll(arrayList);
        }else{
            for(int i=0; i<arrayList.size(); i++){
                if(arrayList.get(i).getTitle().contains(newText)){
                    list_itemArrayList.add(arrayList.get(i));
                }
                if(arrayList.get(i).getId().contains(newText)){
                    list_itemArrayList.add(arrayList.get(i));
                }
            }
        }
        boardAdapter.notifyDataSetChanged();
    }
}