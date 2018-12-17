package com.example.hyeminj.syolo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class LikeFragment extends Fragment implements View.OnClickListener{

    private TextView header1;
    private TextView header2;
    private TextView header3;
    private TextView header4;

    String lodge_title, lodge_type, lodge_addr, lodge_tel;
    String food_roadAddr, food_addr, food_title, food_category, food_mapx_d, food_mapy_d, food_link, food_description, food_telephone;
    public List<HashMap<String, String>> storeinfoList = null;

    String cul_title, cul_start, cul_end, cul_place ,cul_age, cul_etc, cul_homepage, cul_time, cul_quiry, cul_free, cul_spon, cul_link, cul_img
            ,cul_target, cul_fee, cul_program, cul_type;

    String place_id, place_img, place_title;
    ListView lodge, meal, place, cul_life;

    SharedPreferences pref;

    String ID;
    String loginType;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private SimpleAdapter Lodge_adapter = null ;
    private List<HashMap<String, String>> Lodge_infoList = null;
    private SimpleAdapter Food_adapter = null ;
    private List<HashMap<String, String>> Food_infoList = null;
    private SimpleAdapter Cul_adapter = null ;
    private List<HashMap<String, String>> Cul_infoList = null;
    private MyListAdapter Place_adapter;
    ArrayList<image_item> Place_infoList;


    public LikeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_like, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        ID=pref.getString("id","");
        loginType=pref.getString("login","");

        header1 = rootView.findViewById(R.id.header_lodge);
        header2 = rootView.findViewById(R.id.header_meal);
        header3 = rootView.findViewById(R.id.header_place);
        header4 = rootView.findViewById(R.id.header_culife);

        lodge = rootView.findViewById(R.id.lodge);
        meal = rootView.findViewById(R.id.meal);
        place = rootView.findViewById(R.id.place);
        cul_life = rootView.findViewById(R.id.cul_life);

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

        final ListView listView_lodge = (ListView) rootView.findViewById(R.id.lodge); //파싱된 결과확인!

        Lodge_infoList = new ArrayList<HashMap<String, String>>();

        final String[] from = new String[]{"lodge_title", "lodge_type", "lodge_addr"};
        final int[] to = new int[]{R.id.lodge_listviewdata1, R.id.lodge_listviewdata2, R.id.lodge_listviewdata3};
        StrictMode.enableDefaults();

        final DatabaseReference Lodge_list = databaseReference.child("member").child(loginType).child(ID).child("Lodge");

        Lodge_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Lodge_infoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lodge_addr= snapshot.child("addr").getValue(String.class);
                    lodge_type= snapshot.child("type").getValue(String.class);
                    lodge_tel= snapshot.child("tel").getValue(String.class);
                    lodge_title= snapshot.child("title").getValue(String.class);
                    HashMap<String, String> infoMap = new HashMap<String, String>();

                    infoMap.put("lodge_addr", lodge_addr);
                    infoMap.put("lodge_type", lodge_type);
                    infoMap.put("lodge_tel", lodge_tel);
                    infoMap.put("lodge_title", lodge_title);

                    Lodge_infoList.add(infoMap);
                }
                Collections.reverse(Lodge_infoList);
                Lodge_adapter = new SimpleAdapter(rootView.getContext(), Lodge_infoList, R.layout.lodge_listview_items, from, to);
                listView_lodge.setAdapter(Lodge_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        listView_lodge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), lodge_detail.class);
                intent.putExtra("장소명", Lodge_infoList.get(position).get("lodge_title"));
                intent.putExtra("분류", Lodge_infoList.get(position).get("lodge_type"));
                intent.putExtra("주소", Lodge_infoList.get(position).get("lodge_addr"));
                intent.putExtra("전화번호", Lodge_infoList.get(position).get("lodge_tel"));
                startActivity(intent);
            }
        });

        //////////////////////////////////////////////////Food///////////////////////////////////////////////////////////////

        final ListView listView_food = (ListView) rootView.findViewById(R.id.meal); //파싱된 결과확인!
        storeinfoList = new ArrayList<HashMap<String, String>>();

        Food_infoList = new ArrayList<HashMap<String, String>>();

        final String[] from2 = new String[]{"title", "category", "roadAddress"};
        final int[] to2 = new int[]{R.id.textview_main_listviewdata1, R.id.textview_main_listviewdata2, R.id.textview_main_listviewdata3};
        StrictMode.enableDefaults();

        final DatabaseReference Food_list = databaseReference.child("member").child(loginType).child(ID).child("Food");

        Food_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Food_infoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    food_roadAddr= snapshot.child("roadAddress").getValue(String.class);
                    food_title= snapshot.child("title").getValue(String.class);
                    food_category= snapshot.child("category").getValue(String.class);
                    food_addr = snapshot.child("address").getValue(String.class);
                    food_mapx_d = snapshot.child("mapx").getValue(String.class);
                    food_mapy_d = snapshot.child("mapy").getValue(String.class);
                    food_link = snapshot.child("link").getValue(String.class);
                    food_description = snapshot.child("description").getValue(String.class);
                    food_telephone = snapshot.child("telephone").getValue(String.class);

                    HashMap<String, String> storeinfoMap = new HashMap<String, String>();

                    storeinfoMap.put("title",food_title);
                    storeinfoMap.put("category",food_category);
                    storeinfoMap.put("address",food_addr);
                    storeinfoMap.put("roadAddress", food_roadAddr);
                    storeinfoMap.put("mapx",food_mapx_d);
                    storeinfoMap.put("mapy",food_mapy_d);
                    storeinfoMap.put("link",food_link);
                    storeinfoMap.put("description",food_description);
                    storeinfoMap.put("telephone",food_telephone);
                    storeinfoList.add(storeinfoMap);

                    Food_infoList.add(storeinfoMap);
                }
                Collections.reverse(Food_infoList);
                Collections.reverse(storeinfoList);
                Food_adapter = new SimpleAdapter(rootView.getContext(), Food_infoList, R.layout.food_listview_items, from2, to2);
                listView_food.setAdapter(Food_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        listView_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("########@@@@@@@@@","왜안돼7");
                Intent intent = new Intent(rootView.getContext(), food_detail.class);
                food_detail fd = new food_detail();
                intent.putExtra("storeinfoList", storeinfoList.get(position));
                fd.setting_list(storeinfoList);
                startActivity(intent);
            }
        });

        //////////////////////////////////////////////////Place///////////////////////////////////////////////////////////////

        final ListView listView_place = (ListView) rootView.findViewById(R.id.place); //파싱된 결과확인!

        Place_infoList = new ArrayList<image_item>();

        final DatabaseReference Place_list = databaseReference.child("member").child(loginType).child(ID).child("place");

        Place_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Place_infoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    place_id= snapshot.child("id").getValue(String.class);
                    place_img= snapshot.child("image").getValue(String.class);
                    place_title= snapshot.child("title").getValue(String.class);

                    Place_infoList.add(new image_item(place_id,place_img,place_title));
                }
                Collections.reverse(Place_infoList);
                Place_adapter = new MyListAdapter(rootView.getContext(), Place_infoList);
                listView_place.setAdapter(Place_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        listView_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), place_detail.class);
                intent.putExtra("id", Place_infoList.get(position).getId());
                startActivity(intent);
            }
        });

        //////////////////////////////////////////////////Cul_life///////////////////////////////////////////////////////////////
        final ListView listView_Cul = (ListView) rootView.findViewById(R.id.cul_life); //파싱된 결과확인!

        Cul_infoList = new ArrayList<HashMap<String, String>>();

        final String[] from3 = new String[]{"title","start","end","place"};
        final int[] to3 = new int[] {R.id.textView_title,R.id.textView_start,R.id.textView_end,R.id.textView_place};
        StrictMode.enableDefaults();

        final DatabaseReference Cul_list = databaseReference.child("member").child(loginType).child(ID).child("Cul_life");

        Cul_list.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Cul_infoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cul_title= snapshot.child("title").getValue(String.class);
                    cul_start= snapshot.child("start").getValue(String.class);
                    cul_end= snapshot.child("end").getValue(String.class);
                    cul_place= snapshot.child("place").getValue(String.class);
                    cul_age = snapshot.child("age").getValue(String.class);
                    cul_etc = snapshot.child("etc").getValue(String.class);
                    cul_homepage = snapshot.child("homepage").getValue(String.class);
                    cul_time = snapshot.child("time").getValue(String.class);
                    cul_quiry = snapshot.child("quiry").getValue(String.class);
                    cul_free = snapshot.child("free").getValue(String.class);
                    cul_spon = snapshot.child("spon").getValue(String.class);
                    cul_link = snapshot.child("link").getValue(String.class);
                    cul_img = snapshot.child("img").getValue(String.class);
                    cul_target = snapshot.child("target").getValue(String.class);
                    cul_fee = snapshot.child("fee").getValue(String.class);
                    cul_program = snapshot.child("program").getValue(String.class);
                    cul_type = snapshot.child("type").getValue(String.class);

                    HashMap<String, String> infoMap = new HashMap<String, String>();

                    infoMap.put("title", cul_title);
                    infoMap.put("start", cul_start);
                    infoMap.put("end", cul_end);
                    infoMap.put("place", cul_place);
                    infoMap.put("age", cul_age);
                    infoMap.put("etc", cul_etc);
                    infoMap.put("homepage", cul_homepage);
                    infoMap.put("time", cul_time);
                    infoMap.put("quiry", cul_quiry);
                    infoMap.put("free", cul_free);
                    infoMap.put("spon", cul_spon);
                    infoMap.put("link", cul_link);
                    infoMap.put("img", cul_img);
                    infoMap.put("target", cul_target);
                    infoMap.put("fee", cul_fee);
                    infoMap.put("program", cul_program);
                    infoMap.put("type", cul_type);

                    Cul_infoList.add(infoMap);
                }
                Collections.reverse(Cul_infoList);
                Cul_adapter = new SimpleAdapter(rootView.getContext(), Cul_infoList, R.layout.activity_item, from3, to3);
                listView_Cul.setAdapter(Cul_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("DATABASE ERROR");
            }
        });

        listView_Cul.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), detail.class);
                intent.putExtra("title",Cul_infoList.get(position).get("title"));
                intent.putExtra("start",Cul_infoList.get(position).get("start"));
                intent.putExtra("end",Cul_infoList.get(position).get("end"));
                intent.putExtra("place",Cul_infoList.get(position).get("place"));
                intent.putExtra("age",Cul_infoList.get(position).get("age"));
                intent.putExtra("etc",Cul_infoList.get(position).get("etc"));
                intent.putExtra("homepage",Cul_infoList.get(position).get("homepage"));
                intent.putExtra("time",Cul_infoList.get(position).get("time"));
                intent.putExtra("quiry",Cul_infoList.get(position).get("quiry"));
                intent.putExtra("free",Cul_infoList.get(position).get("free"));
                intent.putExtra("spon",Cul_infoList.get(position).get("spon"));
                intent.putExtra("link",Cul_infoList.get(position).get("link"));
                intent.putExtra("img",Cul_infoList.get(position).get("img"));
                intent.putExtra("target",Cul_infoList.get(position).get("target"));
                intent.putExtra("fee",Cul_infoList.get(position).get("fee"));
                intent.putExtra("program",Cul_infoList.get(position).get("program"));
                intent.putExtra("type",Cul_infoList.get(position).get("type"));
                startActivity(intent);
            }
        });


        return rootView;
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