package com.example.hyeminj.syolo;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class detail extends AppCompatActivity implements View.OnClickListener {
    TextView header1;
    TextView header2;
    int count = 0;
    ImageView favorite;
    Button review_button;

    ListView review_list;
    ConstraintLayout frame;
    LinearLayout info;
    String url;
    Bitmap bitmap;
    LinearLayout review;

    ReviewAdapter adapter;
    ArrayList<review_item> list_itemArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String addr;
    int i=0;
    long review_size;

    SharedPreferences pref;
    String loginType;
    String ID;
    String name;
    TextView review_count;
    String remove;
    PhotoViewAttacher photoViewAttacher;
    String title;
    String start;
    String end;
    String place;
    String age;
    String etc;
    String homepage;
    String time;
    String quiry;
    String free;
    String spon;
    String link;
    String img;
    String target;
    String fee;
    String program;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        list_itemArrayList = new ArrayList<review_item>();

        header1 = (TextView)findViewById(R.id.header_review);
        header2 = (TextView)findViewById(R.id.header_detail);
        favorite = (ImageView)findViewById(R.id.imageView_favorite);
        review_list = (ListView)findViewById(R.id.review_list);
        review_button = (Button)findViewById(R.id.review_button);

        header1.setOnClickListener(this);
        header2.setOnClickListener(this);
        favorite.setOnClickListener(this);
        review_button.setOnClickListener(this);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        TextView title_ = (TextView)findViewById(R.id.title);
        TextView date = (TextView)findViewById(R.id.date);
        final TextView place_ = (TextView)findViewById(R.id.place);
        TextView time_ = (TextView)findViewById(R.id.time);
        TextView quiry_ = (TextView)findViewById(R.id.quiry);
        TextView link_ = (TextView)findViewById(R.id.link);
        TextView target_ = (TextView)findViewById(R.id.target);
        TextView fee_ = (TextView)findViewById(R.id.fee);
        TextView content = (TextView)findViewById(R.id.overview);
        review_count = (TextView)findViewById(R.id.review_count);
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        final Intent intent = getIntent();

        try {
            title = removeTag(intent.getStringExtra("title"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        start = intent.getStringExtra("start");;
        end = intent.getStringExtra("end");
        place = intent.getStringExtra("place");
        age = intent.getStringExtra("age");
        etc = intent.getStringExtra("etc");
        homepage = intent.getStringExtra("homepage");
        time = intent.getStringExtra("time");
        quiry = intent.getStringExtra("quiry");
        free = intent.getStringExtra("free");
        spon = intent.getStringExtra("spon");
        link = intent.getStringExtra("link");
        img = intent.getStringExtra("img");
        target = intent.getStringExtra("target");
        fee = intent.getStringExtra("fee");
        program = intent.getStringExtra("program");
        type = intent.getStringExtra("type");

        addr = place;
        if(!title.equals("\n")){

            title_.setText(title);
        }
        if(!date.equals("\n")) {

            date.setText(start+" ~ "+end);
        }
        if(!place.equals("\n")) {
            place_.setText(place);
        }

        try {
            if(!time.equals("\n")) {
                time_.setText(removeTag(time));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(!quiry.equals("\n")) {

                quiry_.setText(removeTag(quiry));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!link.equals("\n")) {

            link_.setText(link);
        }

        if(!target.equals("\n")) {

            target_.setText(target);
        }
        if(!fee.equals("\n")) {

            fee_.setText(fee);
        }

        try {
            if(!program.equals("\n")) {

                content.setText(removeTag(program));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread mThread = new Thread(){
            public void run(){
                try {
                    URL url = new URL(intent.getStringExtra("img"));
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            imageView.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        ID=pref.getString("id","");
        name=pref.getString("name","");

        adapter = new ReviewAdapter(this,list_itemArrayList);
        review_list.setAdapter(adapter);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef = mDatabase.child("review").child(type).child(addr);

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_itemArrayList = new ArrayList<review_item>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String content = snapshot.child("content").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String rating = snapshot.child("rating").getValue(String.class);
                    String time = snapshot.child("date").getValue(String.class);
                    String p = snapshot.child("title").getValue(String.class);
                    String type = snapshot.child("type").getValue(String.class);
                    String id = snapshot.child("ID").getValue(String.class);
                    String lg_type = snapshot.child("login_type").getValue(String.class);
                    if(p!=null&&p.equals(title)){
                        Collections.reverse(list_itemArrayList);
                        list_itemArrayList.add(new review_item(name,content,time,Float.parseFloat(rating),type,addr,id,lg_type));
                        Collections.reverse(list_itemArrayList);
                        adapter = new ReviewAdapter(detail.this,list_itemArrayList);
                        review_list.setAdapter(adapter);
                    }
                    String str = "리뷰 "+list_itemArrayList.size();
                    SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                    ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),3,str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    TextView review_count = (TextView)findViewById(R.id.review_count);
                    review_count.setText(ssb);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ReviewAdapter(detail.this,list_itemArrayList);
        review_list.setAdapter(adapter);

        String str = "리뷰 "+list_itemArrayList.size();
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),3,str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        review_count.setText(ssb);
        ////////////////////////////Like///////////////////////////////

        final DatabaseReference like_check = databaseReference.child("member").child(loginType).child(ID).child("Cul_life");

        like_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lodge_like_size = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("title").getValue(String.class)!=null){
                        if (snapshot.child("title").getValue(String.class).equals(title)) {
                            remove = snapshot.getKey();
                            favorite.setImageResource(R.drawable.like);
                            count = 1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        review = (LinearLayout)findViewById(R.id.review);
        review.setVisibility(View.INVISIBLE);
        header2.setBackgroundResource(R.drawable.border);
        info = (LinearLayout)findViewById(R.id.info);

    }

    @Override
    public void onClick(View v) {

        frame = (ConstraintLayout)findViewById(R.id.frame);
        info = (LinearLayout)findViewById(R.id.info);
        favorite = (ImageView)findViewById(R.id.imageView_favorite);
        review = (LinearLayout)findViewById(R.id.review);

        if(v.getId() == R.id.header_review){
            header1.setBackgroundResource(R.drawable.border);
            header2.setBackgroundResource(0);
            info.setVisibility(View.INVISIBLE);
            review.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.header_detail){
            header2.setBackgroundResource(R.drawable.border);
            header1.setBackgroundResource(0);
            info.setVisibility(View.VISIBLE);
            review.setVisibility(View.INVISIBLE);

        }else if(v.getId() == R.id.imageView_favorite){
            if(count==1){
                favorite.setImageResource(R.drawable.like_blank);
                count=0;
                databaseReference.child("/member/"+loginType+"/"+ID+"/Cul_life/"+remove).removeValue();
            }else if(count==0){
                favorite.setImageResource(R.drawable.like);
                count=1;
                final DatabaseReference push = databaseReference.child("member").child(loginType).child(ID).child("Cul_life").push();
                push.child("title").setValue(title);
                push.child("start").setValue(start);
                push.child("end").setValue(end);
                push.child("place").setValue(place);
                push.child("age").setValue(age);
                push.child("etc").setValue(etc);
                push.child("homepage").setValue(homepage);
                push.child("time").setValue(time);
                push.child("quiry").setValue(quiry);
                push.child("free").setValue(free);
                push.child("spon").setValue(spon);
                push.child("link").setValue(link);
                push.child("img").setValue(img);
                push.child("target").setValue(target);
                push.child("fee").setValue(fee);
                push.child("program").setValue(program);
                push.child("type").setValue(type);
            }
        }else if(v.getId() == R.id.review_button){
            Intent intent = new Intent(this,review_write.class);
            startActivityForResult(intent,1);
        }
    }

    public String removeTag(String html) throws Exception {
        String noHTMLString = html.replaceAll("\\<.*?\\>", "");
        noHTMLString = noHTMLString.replaceAll("<([bip])>.*?</\1>", "");
        noHTMLString = noHTMLString.replaceAll("\n", " ");
        noHTMLString = noHTMLString.replaceAll("<(.*?)\\>"," ");//Removes all items in brackets
        noHTMLString = noHTMLString.replaceAll("<(.*?)\\\n"," ");//Must be undeneath
        noHTMLString = noHTMLString.replaceFirst("(.*?)\\>", " ");
        noHTMLString = noHTMLString.replaceAll("&nbsp;"," ");
        noHTMLString = noHTMLString.replaceAll("&amp;"," ");
        noHTMLString = noHTMLString.replaceAll("&quot;"," ");
        noHTMLString = noHTMLString.replaceAll("&#39;"," ");
        return noHTMLString;

    }
    public void openDial(View view){
        TextView quiry_ = (TextView)findViewById(R.id.quiry);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String receiver =quiry_.getText().toString();
        intent.setData(Uri.parse("tel:"+receiver));
        startActivity(intent);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String rating = data.getStringExtra("rating");
                String content = data.getStringExtra("content");
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String time = sdf.format(date);
                DatabaseReference push = databaseReference.child("review").child(type).child(addr).push();
                push.child("name").setValue(name);
                push.child("content").setValue(content);
                push.child("rating").setValue(rating);
                push.child("date").setValue(time);
                push.child("title").setValue(title);
                push.child("type").setValue(type);
                push.child("ID").setValue(ID);
                push.child("login_type").setValue(loginType);
                DatabaseReference myreview = databaseReference.child("member").child(loginType).child(ID).child("review").child(type).child(addr).child(push.getKey());
                myreview.child("name").setValue(name);
                myreview.child("content").setValue(content);
                myreview.child("rating").setValue(rating);
                myreview.child("date").setValue(time);
                myreview.child("title").setValue(title);

            }
        }
    }
    public void onPause() {
        super.onPause();
        String str = "리뷰 "+list_itemArrayList.size();
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),3,str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        review_count.setText(ssb);
    }
}
