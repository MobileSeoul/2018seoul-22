package com.example.hyeminj.syolo;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.util.HashMap;
import java.util.List;


public class food_detail extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{
    public food_detail(){

    }
    public List<HashMap<String, String>> storeinfoList = null;


    public void setting_list(List<HashMap<String,String>> a){
        this.storeinfoList = a;
    }
    String title = null,addr = null,category = null, road_addr = null,link = null, dscrp = null, tel = null, mapx = null, mapy = null;
    Bitmap bitmap;
    int count = 0;
    double mapx_d, mapy_d;
    TextView header1;
    TextView header2;
    TextView header3;
    ConstraintLayout frame;
    LinearLayout review;
    LinearLayout info;
    TextView placeTitle;
    TextView description;
    TextView content;
    ImageView imageView;
    TextView address;
    TextView placeLink;
    TextView telephone;
    TextView placeCategory;
    TextView roadAddress;
    ImageView favorite;
    private Geocoder geocoder;
    ReviewAdapter adapter;
    ArrayList<review_item> list_itemArrayList;
    String id;
    int i = 0;
    long review_size;
    String remove;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button review_button;
    ListView review_list;

    SharedPreferences pref;
    String loginType;
    String ID;
    String user_name;
    TextView review_count;

    HashMap<String, String> temp;

    String type = "food";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        placeTitle = (TextView)findViewById(R.id.place_title);
        address = (TextView)findViewById(R.id.place_addr);
        description = (TextView)findViewById(R.id.place_description);
        placeLink = (TextView)findViewById(R.id.place_link);
        placeCategory = (TextView)findViewById(R.id.place_category);
        roadAddress = (TextView)findViewById(R.id.place_roadAddress);
        telephone = (TextView)findViewById(R.id.place_tel);
        favorite = (ImageView)findViewById(R.id.imageView_favorite);
        header2 = (TextView)findViewById(R.id.header_detail);
        header3 = (TextView)findViewById(R.id.header_review);
        review_count = (TextView)findViewById(R.id.review_count);
        review_button = (Button)findViewById(R.id.review_button);
        list_itemArrayList = new ArrayList<review_item>();

        header2.setOnClickListener(this);
        header3.setOnClickListener(this);
        favorite.setOnClickListener(this);
        review_button.setOnClickListener(this);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        ID=pref.getString("id","");
        user_name=pref.getString("name","");

//,addr = null, road_addr = null,link = null, dscrp = null, tel = null, mapx = null, mapy = null;
        Intent intent = getIntent();


        temp = (HashMap<String, String>) intent.getSerializableExtra("storeinfoList");
        title = temp.get("title");
        road_addr = temp.get("roadAddress");
        link = temp.get("link");
        dscrp = temp.get("description");
        tel = temp.get("telephone");
        mapx = temp.get("mapx");
        mapy = temp.get("mapy");
        addr =temp.get("address");
        category =temp.get("category");
        if(title==null){
            title = "정보 없음";
        }if(road_addr==null){
            road_addr = "정보 없음";
        }if(link==null){
            link = "정보 없음";
        }if(dscrp==null){
            dscrp = "정보 없음";
        }if(tel==null){
            tel = "정보 없음";
        }if(mapx==null){
            mapx = "정보 없음";
        }if(mapy==null){
            mapy = "정보 없음";
        }if(addr==null){
            addr = "정보 없음";
        }if(category==null){
            category = "정보 없음";
        }
  //      Toast.makeText(
  //              getApplicationContext(),
  //              title+" "+road_addr+" "+link+" "+dscrp+" "+tel+" "+mapx+" "+mapy+" "+addr+" "+category,
  //              Toast.LENGTH_LONG).show();
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map_place);
        mapFragment.getMapAsync(this);

        try {
            if(title!=null&& !title.equals("")){
            placeTitle.setText(removeTag(title));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        address.setText(addr);
        if(dscrp!=null&& !dscrp.equals("")){
        try {
            description.setText(removeTag(dscrp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        if(link!= null && !link.equals("")){
        placeLink.setText(link);
        }
        if(category!=null&& !category.equals("")){
        placeCategory.setText(category);
        }
        if(road_addr != null && !road_addr.equals("")){

        roadAddress.setText(road_addr);
        }
        if(telephone != null&& !telephone.equals("")){
        telephone.setText(tel);
        }

        frame = (ConstraintLayout)findViewById(R.id.frame);
        review = (LinearLayout)findViewById(R.id.review);
        review.setVisibility(View.INVISIBLE);
        header2.setBackgroundResource(R.drawable.border);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        id = intent.getStringExtra("id");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef = mDatabase.child("review").child("food").child(addr);
        list_itemArrayList = new ArrayList<review_item>();
        adapter = new ReviewAdapter(this,list_itemArrayList);
        review_list = (ListView)findViewById(R.id.review_list);
        review_button = (Button)findViewById(R.id.review_button);

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
                    String type = snapshot.child("type").getValue(String.class);
                    String id = snapshot.child("ID").getValue(String.class);
                    String lg_type = snapshot.child("login_type").getValue(String.class);
                    if(p!=null){
                        Collections.reverse(list_itemArrayList);
                        list_itemArrayList.add(new review_item(name,content,time,Float.parseFloat(rating),type,addr,id,lg_type));
                        Collections.reverse(list_itemArrayList);
                        adapter = new ReviewAdapter(food_detail.this,list_itemArrayList);
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
        String str = "리뷰 "+list_itemArrayList.size();
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#5F00FF")),3,str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        review_count.setText(ssb);

        //Like 기능
        final DatabaseReference like_check = databaseReference.child("member").child(loginType).child(ID).child("Food");

        like_check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //lodge_like_size = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("roadAddress").getValue(String.class)!=null){
                        if (snapshot.child("roadAddress").getValue(String.class).equals(road_addr)) {
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
    public void openDial(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String receiver =telephone.getText().toString();
        intent.setData(Uri.parse("tel:"+receiver));
        startActivity(intent);
    }
    public String removeTag(String html) throws Exception {
        return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
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
        Address address = addressList.get(0);
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();
        LatLng PLACE = new LatLng(latitude,longitude);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.food_placeholder);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(PLACE);
        makerOptions.title("click");
        makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        googleMap.addMarker(makerOptions);
        googleMap.addMarker(makerOptions);

        Marker marker = googleMap.addMarker(makerOptions);
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(PLACE).zoom(17).build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onClick(View v) {
        frame = (ConstraintLayout)findViewById(R.id.frame);
        info = (LinearLayout)findViewById(R.id.info);
        review = (LinearLayout)findViewById(R.id.review);

      if(v.getId() == R.id.header_detail){
            header2.setBackgroundResource(R.drawable.border);
            header3.setBackgroundResource(0);
            review.setVisibility(View.INVISIBLE);
            info.setVisibility(View.VISIBLE);

        }else if(v.getId() == R.id.header_review){
            info.setVisibility(View.INVISIBLE);
            review.setVisibility(View.VISIBLE);
            header3.setBackgroundResource(R.drawable.border);
            header2.setBackgroundResource(0);
      }else if(v.getId() == R.id.imageView_favorite){
          if(count==1){
              favorite.setImageResource(R.drawable.like_blank);
              count = 0;
              databaseReference.child("/member/"+loginType+"/"+ID+"/Food/"+remove).removeValue();
          }else if(count==0){
              favorite.setImageResource(R.drawable.like);
              count = 1;
              final DatabaseReference push = databaseReference.child("member").child(loginType).child(ID).child("Food").push();
              push.setValue(temp);
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
                push.child("name").setValue(user_name);
                push.child("ID").setValue(ID);
                push.child("login_type").setValue(loginType);
                push.child("content").setValue(content);
                push.child("rating").setValue(rating);
                push.child("date").setValue(time);
                push.child("addr").setValue(addr);
                push.child("type").setValue(type);
                DatabaseReference myreview = databaseReference.child("member").child(loginType).child(ID).child("review").child("food").child(addr).child(push.getKey());
                myreview.child("name").setValue(user_name);
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