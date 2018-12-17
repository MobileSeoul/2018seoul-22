package com.example.hyeminj.syolo;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class lodge_detail extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    int count = 0;

    String title, type, addr, tel;

    int position;

    TextView header2;
    TextView header3;

    ConstraintLayout frame;
    LinearLayout review;
    LinearLayout info;

    TextView lodge_name;
    TextView lodge_type;
    TextView lodge_address;
    TextView lodge_tel;
    TextView review_count;

    ImageView favorite;
    Button review_button;

    private Geocoder geocoder;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    SharedPreferences pref;
    String loginType;
    String ID;
    String name;
    ReviewAdapter adapter;
    ArrayList<review_item> list_itemArrayList;
    ListView review_list;

    long lodge_like_size;

    long review_size;
    String remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodge_detail);

        review_count = (TextView)findViewById(R.id.review_count);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        lodge_name = (TextView)findViewById(R.id.lodge_title);
        lodge_type = (TextView)findViewById(R.id.lodge_type);
        lodge_address = (TextView)findViewById(R.id.lodge_addr);
        lodge_tel = (TextView)findViewById(R.id.lodge_tel);

        header2 = (TextView)findViewById(R.id.header_detail);
        header3 = (TextView)findViewById(R.id.header_review);

        favorite = (ImageView)findViewById(R.id.imageView_favorite);

        review_button = (Button)findViewById(R.id.review_button);
        list_itemArrayList = new ArrayList<review_item>();

        header2.setOnClickListener(this);
        header3.setOnClickListener(this);

        favorite.setOnClickListener(this);

        review_button.setOnClickListener(this);

        frame = (ConstraintLayout)findViewById(R.id.frame);
        review = (LinearLayout)findViewById(R.id.review);
        info = (LinearLayout)findViewById(R.id.info);
        review.setVisibility(View.INVISIBLE);
        header2.setBackgroundResource(R.drawable.border);
        review_list = (ListView)findViewById(R.id.review_list);

        Intent intent = getIntent();

        title = intent.getStringExtra("장소명");
        type =  intent.getStringExtra("분류");
        addr = intent.getStringExtra("주소");
        tel = intent.getStringExtra("전화번호");
        position = intent.getIntExtra("position",0);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        ID=pref.getString("id","");
        name=pref.getString("name","");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mConditionRef = mDatabase.child("review").child("Lodge").child(addr);

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_itemArrayList = new ArrayList<review_item>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String content = snapshot.child("content").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String rating = snapshot.child("rating").getValue(String.class);
                    String time = snapshot.child("date").getValue(String.class);
                    String p = snapshot.child("addr").getValue(String.class);
                    String id = snapshot.child("ID").getValue(String.class);
                    String lg_type = snapshot.child("login_type").getValue(String.class);
                    if(p!=null&&p.equals(addr)){
                        Collections.reverse(list_itemArrayList);
                        list_itemArrayList.add(new review_item(name,content,time,Float.parseFloat(rating),"Lodge",addr,id,lg_type));
                        Collections.reverse(list_itemArrayList);
                        adapter = new ReviewAdapter(lodge_detail.this,list_itemArrayList);
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
        adapter = new ReviewAdapter(this,list_itemArrayList);
        review_list.setAdapter(adapter);

        String str = "리뷰 "+list_itemArrayList.size();
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),3,str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        review_count.setText(ssb);

        final DatabaseReference like_check = databaseReference.child("member").child(loginType).child(ID).child("Lodge");

        like_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("addr").getValue(String.class)!=null){
                        if (snapshot.child("addr").getValue(String.class).equals(addr)) {
                            favorite.setImageResource(R.drawable.like);
                            count = 1;
                            i++;
                            remove = snapshot.getKey();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        StrictMode.enableDefaults();

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map_place);
        mapFragment.getMapAsync(this);
        if(!title.equals("\n")) {

            lodge_name.setText(title);
        }
        if(!addr.equals("\n")){
            lodge_address.setText("위치: "+addr);
        }

        try {
            if(!type.equals("\n")) {
                lodge_type.setText(removeTag(type));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(!tel.equals("\n")){

                lodge_tel.setText(removeTag(tel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        geocoder = new Geocoder(this);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(addr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addressList.size()!=0){
            Address address = addressList.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            LatLng PLACE = new LatLng(latitude,longitude);

            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions.position(PLACE);
            makerOptions.title("click");
            Marker marker = googleMap.addMarker(makerOptions);
            marker.showInfoWindow();

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        }else{
            //   Log.d("#####ADDR",addressList.get(0).getAddressLine(0));

        }
    }

    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.header_detail){
            header2.setBackgroundResource(R.drawable.border);
            header3.setBackgroundResource(0);
            review.setVisibility(View.INVISIBLE);
            info.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.header_review){
            header3.setBackgroundResource(R.drawable.border);
            header2.setBackgroundResource(0);
            review.setVisibility(View.VISIBLE);
            info.setVisibility(View.INVISIBLE);

        }else if(v.getId() == R.id.imageView_favorite){
            if(count==1){
                favorite.setImageResource(R.drawable.like_blank);
                count = 0;
                databaseReference.child("/member/"+loginType+"/"+ID+"/Lodge/"+remove).removeValue();

            }else if(count==0){
                favorite.setImageResource(R.drawable.like);
                count = 1;
                final DatabaseReference push = databaseReference.child("member").child(loginType).child(ID).child("Lodge").push();
                push.child("title").setValue(title);
                push.child("type").setValue(type);
                push.child("addr").setValue(addr);
                push.child("tel").setValue(tel);
            }

        }else if(v.getId() == R.id.review_button){
            Intent intent = new Intent(this,review_write.class);
            startActivityForResult(intent,1);
        }
    }

    public void openDial(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String receiver =lodge_tel.getText().toString();
        intent.setData(Uri.parse("tel:"+receiver));
        startActivity(intent);
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
                DatabaseReference push = databaseReference.child("review").child("Lodge").child(addr).push();
                push.child("name").setValue(name);
                push.child("content").setValue(content);
                push.child("rating").setValue(rating);
                push.child("date").setValue(time);
                push.child("type").setValue("Lodge");
                push.child("ID").setValue(ID);
                push.child("addr").setValue(addr);
                push.child("login_type").setValue(loginType);
                DatabaseReference myreview = databaseReference.child("member").child(loginType).child(ID).child("review").child("Lodge").child(addr).child(push.getKey());
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