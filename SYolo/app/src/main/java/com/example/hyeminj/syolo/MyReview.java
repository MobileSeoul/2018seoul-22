package com.example.hyeminj.syolo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyReview extends Activity implements View.OnClickListener{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    SharedPreferences pref;
    String loginType;
    String ID;

    private TextView header1;
    private TextView header2;
    private TextView header3;
    private TextView header4;

    ListView lodge, meal, place, cul_life;

    String name, rating, title, date, content, addr, key, type;
    ImageView trash;

    private MyReviewAdapter Lodge_adapter = null ;
    private List<HashMap<String, String>> Lodge_reviewList = null;
    private MyReviewAdapter Food_adapter = null ;
    private List<HashMap<String, String>> Food_reviewList = null;
    private MyReviewAdapter Place_adapter = null ;
    private List<HashMap<String, String>> Place_reviewList = null;
    private MyReviewAdapter Cul_adapter = null ;
    private List<HashMap<String, String>> Cul_reviewList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreview);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        ID=pref.getString("id","");

        header1 = findViewById(R.id.header_lodge);
        header2 = findViewById(R.id.header_meal);
        header3 = findViewById(R.id.header_place);
        header4 = findViewById(R.id.header_culife);

        lodge = findViewById(R.id.lodge);
        meal = findViewById(R.id.meal);
        place = findViewById(R.id.place);
        cul_life = findViewById(R.id.cul_life);
        trash = findViewById(R.id.trash);

        header1.setOnClickListener(this);
        header2.setOnClickListener(this);
        header3.setOnClickListener(this);
        header4.setOnClickListener(this);

        lodge.setVisibility(View.VISIBLE);
        meal.setVisibility(View.GONE);
        place.setVisibility(View.GONE);
        cul_life.setVisibility(View.GONE);
        header1.setBackgroundResource(R.drawable.border);
        header2.setBackgroundResource(R.drawable.border2);
        header3.setBackgroundResource(R.drawable.border2);
        header4.setBackgroundResource(R.drawable.border2);
        StrictMode.enableDefaults();



        //////////////////////////////////////////////////Lodge///////////////////////////////////////////////////////////////

        final ListView listView_lodge = (ListView) findViewById(R.id.lodge); //파싱된 결과확인!
        Lodge_reviewList = new ArrayList<HashMap<String, String>>();

        StrictMode.enableDefaults();

        final DatabaseReference Lodge_list = databaseReference.child("member").child(loginType).child(ID).child("review").child("Lodge");

        Lodge_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Lodge_reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    addr=snapshot.getKey();
                    for (DataSnapshot s : snapshot.getChildren()){
                        key = s.getKey();
                        title = s.child("title").getValue(String.class);
                        name = s.child("name").getValue(String.class);
                        rating = s.child("rating").getValue(String.class);
                        date = s.child("date").getValue(String.class);
                        content = s.child("content").getValue(String.class);

                        HashMap<String, String> infoMap = new HashMap<String, String>();

                        infoMap.put("key", key);
                        infoMap.put("title", title);
                        infoMap.put("name", name);
                        infoMap.put("rating", rating);
                        infoMap.put("date", date);
                        infoMap.put("content", content);
                        infoMap.put("addr", addr);
                        infoMap.put("type", "Lodge");

                        Lodge_reviewList.add(infoMap);
                    }
                }
                Lodge_adapter = new MyReviewAdapter(MyReview.this, Lodge_reviewList);
                listView_lodge.setAdapter(Lodge_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        //////////////////////////////////////////////////Place///////////////////////////////////////////////////////////////

        final ListView listView_place = (ListView) findViewById(R.id.place); //파싱된 결과확인!
        Place_reviewList = new ArrayList<HashMap<String, String>>();

        StrictMode.enableDefaults();

        final DatabaseReference Place_list = databaseReference.child("member").child(loginType).child(ID).child("review").child("place");

        Place_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Place_reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    addr=snapshot.getKey();
                    for (DataSnapshot s : snapshot.getChildren()){
                        key = s.getKey();
                        title = s.child("title").getValue(String.class);
                        name = s.child("name").getValue(String.class);
                        rating = s.child("rating").getValue(String.class);
                        date = s.child("date").getValue(String.class);
                        content = s.child("content").getValue(String.class);

                        HashMap<String, String> infoMap = new HashMap<String, String>();

                        infoMap.put("key", key);
                        infoMap.put("title", title);
                        infoMap.put("name", name);
                        infoMap.put("rating", rating);
                        infoMap.put("date", date);
                        infoMap.put("content", content);
                        infoMap.put("addr", addr);
                        infoMap.put("type","place");

                        Place_reviewList.add(infoMap);
                    }
                }
                Place_adapter = new MyReviewAdapter(MyReview.this, Place_reviewList);
                listView_place.setAdapter(Place_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        //////////////////////////////////////////////////meal///////////////////////////////////////////////////////////////

        final ListView listView_food = (ListView) findViewById(R.id.meal); //파싱된 결과확인!
        Food_reviewList = new ArrayList<HashMap<String, String>>();

        StrictMode.enableDefaults();

        final DatabaseReference Food_list = databaseReference.child("member").child(loginType).child(ID).child("review").child("food");

        Food_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Food_reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    addr=snapshot.getKey();
                    for (DataSnapshot s : snapshot.getChildren()){
                        key = s.getKey();
                        title = s.child("title").getValue(String.class);
                        name = s.child("name").getValue(String.class);
                        rating = s.child("rating").getValue(String.class);
                        date = s.child("date").getValue(String.class);
                        content = s.child("content").getValue(String.class);

                        HashMap<String, String> infoMap = new HashMap<String, String>();

                        infoMap.put("key", key);
                        infoMap.put("title", title);
                        infoMap.put("name", name);
                        infoMap.put("rating", rating);
                        infoMap.put("date", date);
                        infoMap.put("content", content);
                        infoMap.put("addr", addr);
                        infoMap.put("type", "food");

                        Food_reviewList.add(infoMap);
                    }
                }
                Food_adapter = new MyReviewAdapter(MyReview.this, Food_reviewList);
                listView_food.setAdapter(Food_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        //////////////////////////////////////////////////Cul_life///////////////////////////////////////////////////////////////

        final ListView listView_cul_life = (ListView) findViewById(R.id.cul_life); //파싱된 결과확인!
        Cul_reviewList = new ArrayList<HashMap<String, String>>();

        StrictMode.enableDefaults();

        final DatabaseReference Cul_list = databaseReference.child("member").child(loginType).child(ID).child("review");
        Cul_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Cul_reviewList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("festival") || snapshot.getKey().equals("play") || snapshot.getKey().equals("musical") ||
                            snapshot.getKey().equals("gallery") || snapshot.getKey().equals("etc")) {
                        type = snapshot.getKey();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            addr = snap.getKey();
                            for (DataSnapshot s : snap.getChildren()) {
                                key = s.getKey();
                                title = s.child("title").getValue(String.class);
                                name = s.child("name").getValue(String.class);
                                rating = s.child("rating").getValue(String.class);
                                date = s.child("date").getValue(String.class);
                                content = s.child("content").getValue(String.class);

                                HashMap<String, String> infoMap = new HashMap<String, String>();

                                infoMap.put("key", key);
                                infoMap.put("title", title);
                                infoMap.put("name", name);
                                infoMap.put("rating", rating);
                                infoMap.put("date", date);
                                infoMap.put("content", content);
                                infoMap.put("addr", addr);
                                infoMap.put("type", type);

                                Cul_reviewList.add(infoMap);
                            }
                        }
                    }
                }
                Cul_adapter = new MyReviewAdapter(MyReview.this, Cul_reviewList);
                listView_cul_life.setAdapter(Cul_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

    }

    public void onClick(View v) {
        if(v.getId() == R.id.header_lodge){
            header1.setBackgroundResource(R.drawable.border);
            header2.setBackgroundResource(R.drawable.border2);
            header3.setBackgroundResource(R.drawable.border2);
            header4.setBackgroundResource(R.drawable.border2);
            meal.setVisibility(View.GONE);
            place.setVisibility(View.GONE);
            cul_life.setVisibility(View.GONE);
            lodge.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.header_meal){
            header2.setBackgroundResource(R.drawable.border);
            header1.setBackgroundResource(R.drawable.border2);
            header3.setBackgroundResource(R.drawable.border2);
            header4.setBackgroundResource(R.drawable.border2);
            lodge.setVisibility(View.GONE);
            place.setVisibility(View.GONE);
            cul_life.setVisibility(View.GONE);
            meal.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.header_place){
            header3.setBackgroundResource(R.drawable.border);
            header1.setBackgroundResource(R.drawable.border2);
            header2.setBackgroundResource(R.drawable.border2);
            header4.setBackgroundResource(R.drawable.border2);
            lodge.setVisibility(View.GONE);
            meal.setVisibility(View.GONE);
            cul_life.setVisibility(View.GONE);
            place.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.header_culife){
            header4.setBackgroundResource(R.drawable.border);
            header1.setBackgroundResource(R.drawable.border2);
            header2.setBackgroundResource(R.drawable.border2);
            header3.setBackgroundResource(R.drawable.border2);
            lodge.setVisibility(View.GONE);
            meal.setVisibility(View.GONE);
            place.setVisibility(View.GONE);
            cul_life.setVisibility(View.VISIBLE);

        }
    }

    }
