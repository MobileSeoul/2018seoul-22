package com.example.hyeminj.syolo;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class place_detail extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    double mapx = 37.56;
    double mapy = 126.97;

    String addr = null, home = null, mapx_d = null, mapy_d = null,
            overview = null, tel = null, title = null, image = null,
            pet = null, age = null, park = null, rest = null, time = null;

    Bitmap bitmap;

   TextView header1;
   TextView header2;
   TextView header3;
    ConstraintLayout frame;
    LinearLayout detail;
    LinearLayout info;
    TextView placename;
    TextView content;
    ImageView imageView;
    TextView address;

    TextView homepage;
    TextView number;
    TextView pet_;
    TextView age_;
    TextView park_;
    TextView rest_;
    TextView time_;

    String place_id;
    String detail_id;
    String id;

    int i = 0;
    int count = 0;
    ImageView favorite;
    Button review_button;

    ListView review_list;
    ReviewAdapter adapter;
    ArrayList<review_item> list_itemArrayList;
    LinearLayout review;
    long review_size;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView review_count;

    String remove;
    String type = "place";

    SharedPreferences pref;
    String loginType;
    String ID;
    String name;
    long place_like_size;

    boolean Addr = false, Home = false, Mapx = false, Mapy = false,
            Overview = false, Tel = false, Title = false, Image = false,
            Rest = false, Time = false, Park = false, Pet = false, Age = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        list_itemArrayList = new ArrayList<review_item>();

        review_list = (ListView)findViewById(R.id.review_list);
        review_button = (Button)findViewById(R.id.review_button);

        placename = (TextView)findViewById(R.id.place_title);
        content = (TextView)findViewById(R.id.place_overview);
        imageView = (ImageView)findViewById(R.id.imageView);
        address = (TextView)findViewById(R.id.place_addr);

        homepage = (TextView)findViewById(R.id.place_homepage);
        number = (TextView)findViewById(R.id.place_tel);
        pet_ = (TextView)findViewById(R.id.place_pet);
        age_ = (TextView)findViewById(R.id.place_age);
        park_ = (TextView)findViewById(R.id.place_park);
        rest_ = (TextView)findViewById(R.id.place_rest);
        time_ = (TextView)findViewById(R.id.place_time);

        header1 = (TextView)findViewById(R.id.header_overview);
        header2 = (TextView)findViewById(R.id.header_detail);
        header3 = (TextView)findViewById(R.id.header_review);
        favorite = (ImageView)findViewById(R.id.imageView_favorite);

        header1.setOnClickListener(this);
        header2.setOnClickListener(this);
        header3.setOnClickListener(this);
        favorite.setOnClickListener(this);
        review_button.setOnClickListener(this);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        image = intent.getStringExtra("image");
        StrictMode.enableDefaults();

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        loginType = pref.getString("login","");
        ID = pref.getString("id","");
        name = pref.getString("name","");

        final DatabaseReference like_check = databaseReference.child("member").child(loginType).child(ID).child("place");

        like_check.addValueEventListener(new ValueEventListener() {
            @Override
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        place_id = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&contentId="+intent.getStringExtra("id")+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y";
        detail_id = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&contentId="+intent.getStringExtra("id")+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&introYN=Y";

        boolean Addr = false, Home = false, Mapx = false, Mapy = false,
                Overview = false, Tel = false, Title = false, Image = false,
                Rest = false, Time = false, Park = false, Pet = false, Age = false;

        try {
            URL url = new URL(place_id);
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("addr1")) {
                            Addr = true;
                        }
                        if (parser.getName().equals("homepage")) {
                            Home = true;
                        }
                        if (parser.getName().equals("mapx")) {
                            Mapx = true;
                        }
                        if (parser.getName().equals("mapy")) {
                            Mapy = true;
                        }
                        if (parser.getName().equals("overview")) {
                            Overview = true;
                        }
                        if (parser.getName().equals("tel")) {
                            Tel = true;
                        }
                        if (parser.getName().equals("title")) {
                            Title = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (Addr) {
                            addr = parser.getText();
                            Addr = false;
                        }
                        if (Home) {
                            home = parser.getText();
                            Home = false;
                        }
                        if (Mapx) {
                            mapx_d = parser.getText();
                            mapx = Double.parseDouble(mapx_d);
                            Mapx = false;
                        }
                        if (Mapy) {
                            mapy_d = parser.getText();
                            mapy = Double.parseDouble(mapy_d);
                            Mapy = false;
                        }
                        if (Overview) {
                            overview = parser.getText();
                            Overview = false;
                        }
                        if (Tel) {
                            tel = parser.getText();
                            Tel = false;
                        }
                        if (Title) {
                            title = parser.getText();
                            Title = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL(detail_id);
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("chkpet")) {
                            Pet = true;
                        }
                        if (parser.getName().equals("expagerange")) {
                            Age = true;
                        }
                        if (parser.getName().equals("parking")) {
                            Park = true;
                        }
                        if (parser.getName().equals("resttime")) {
                            Rest = true;
                        }
                        if (parser.getName().equals("usetime")) {
                            Time = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (Pet) {
                            pet = parser.getText();
                            Pet = false;
                        }
                        if (Age) {
                            age = parser.getText();
                            Age = false;
                        }
                        if (Park) {
                            park = parser.getText();
                            Park = false;
                        }
                        if (Rest) {
                            rest = parser.getText();
                            Rest = false;
                        }
                        if (Time) {
                            time = parser.getText();
                            Time = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map_place);
        mapFragment.getMapAsync(this);

        placename.setText(title);
        address.setText("위치: "+addr);

        try {
            content.setText(removeTag(overview));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            time_.setText(removeTag(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            homepage.setText(removeTag(home));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            number.setText(removeTag(tel));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            age_.setText(removeTag(age));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            park_.setText(removeTag(park));
        } catch (Exception e) {
            e.printStackTrace();
        }
        pet_.setText((pet));
        try {
            rest_.setText(removeTag(rest));
        } catch (Exception e) {
            e.printStackTrace();
        }


        Thread mThread = new Thread(){
            public void run(){
                try {
                    URL url = new URL(image);
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
        frame = (ConstraintLayout)findViewById(R.id.frame);
        detail = (LinearLayout)findViewById(R.id.detail);
        detail.setVisibility(View.INVISIBLE);
        header2.setBackgroundResource(R.drawable.border);


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef = mDatabase.child("review").child("place").child(addr);

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_itemArrayList = new ArrayList<review_item>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String content = snapshot.child("content").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String rating = snapshot.child("rating").getValue(String.class);
                    String time = snapshot.child("date").getValue(String.class);
                    String p = snapshot.child("id").getValue(String.class);
                    String user_id = snapshot.child("ID").getValue(String.class);
                    String lg_type = snapshot.child("login_type").getValue(String.class);
                    if(p!=null){
                        Collections.reverse(list_itemArrayList);
                        list_itemArrayList.add(new review_item(name,content,time,Float.parseFloat(rating),type,addr,user_id,lg_type));
                        Collections.reverse(list_itemArrayList);
                        adapter = new ReviewAdapter(place_detail.this,list_itemArrayList);
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


        adapter = new ReviewAdapter(place_detail.this,list_itemArrayList);
        review_list.setAdapter(adapter);

        String str = "리뷰 "+list_itemArrayList.size();
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),3,str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        review_count = (TextView)findViewById(R.id.review_count);
        review_count.setText(ssb);
        review = (LinearLayout)findViewById(R.id.review);
        review.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng PLACE = new LatLng(mapy,mapx);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(PLACE);
        makerOptions.title("click");
        googleMap.addMarker(makerOptions);

        Marker marker = googleMap.addMarker(makerOptions);
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(PLACE).zoom(17).build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    @Override
    public void onClick(View v) {
        frame = (ConstraintLayout)findViewById(R.id.frame);
        info = (LinearLayout)findViewById(R.id.info);
        detail = (LinearLayout)findViewById(R.id.detail);
        favorite = (ImageView)findViewById(R.id.imageView_favorite);
        review = (LinearLayout)findViewById(R.id.review);

        if(v.getId() == R.id.header_overview){
            header1.setBackgroundResource(R.drawable.border);
            header2.setBackgroundResource(0);
            header3.setBackgroundResource(0);
            detail.setVisibility(View.VISIBLE);
            info.setVisibility(View.INVISIBLE);
            review.setVisibility(View.INVISIBLE);

        }else if(v.getId() == R.id.header_detail){
            header2.setBackgroundResource(R.drawable.border);
            header1.setBackgroundResource(0);
            header3.setBackgroundResource(0);
            detail.setVisibility(View.INVISIBLE);
            info.setVisibility(View.VISIBLE);
            review.setVisibility(View.INVISIBLE);

        }else if(v.getId() == R.id.header_review){
            header3.setBackgroundResource(R.drawable.border);
            header2.setBackgroundResource(0);
            header1.setBackgroundResource(0);
            detail.setVisibility(View.INVISIBLE);
            info.setVisibility(View.INVISIBLE);
            review.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.imageView_favorite){
            if(count==1){
                favorite.setImageResource(R.drawable.like_blank);
                count = 0;
                databaseReference.child("/member/"+loginType+"/"+ID+"/place/"+remove).removeValue();
            }else if(count==0){
                favorite.setImageResource(R.drawable.like);
                count = 1;
                DatabaseReference push = databaseReference.child("member").child(loginType).child(ID).child(type).push();
                push.child("title").setValue(title);
                push.child("addr").setValue(addr);
                push.child("image").setValue(image);
                push.child("id").setValue(id);
            }
        }else if(v.getId() == R.id.review_button){
            Intent intent = new Intent(this,review_write.class);
            startActivityForResult(intent,1);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String rating = data.getStringExtra("rating");
                String content = data.getStringExtra("content");
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String time = sdf.format(date);
                DatabaseReference push = databaseReference.child("review").child(type).child(addr).push();
                push.child("name").setValue(name);
                push.child("ID").setValue(ID);
                push.child("login_type").setValue(loginType);
                push.child("content").setValue(content);
                push.child("rating").setValue(rating);
                push.child("date").setValue(time);
                push.child("id").setValue(id);
                push.child("type").setValue(type);
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
