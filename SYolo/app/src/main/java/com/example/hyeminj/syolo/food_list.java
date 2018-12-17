package com.example.hyeminj.syolo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;

public class food_list extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private TextView tv;
    public List<HashMap<String, String>> storeinfoList = null;

    List<HashMap<String, String>> arrayList;
    SearchView search;
    ImageButton refresh;
    ImageButton question;
    public static final int LOAD_SUCCESS = 101;
    private ProgressDialog progressDialog = null;
    private SimpleAdapter adapter = null;
    String test;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    public String location_mapx;
    public String location_mapy;
    ArrayAdapter sAdapter;
    AdapterView<ArrayAdapter> spinner;
    private GpsInfo gps;
    private String real_address = "서울시";//&#xb9cc;&#xc57d; gps&#xc2b9;&#xc778;&#xc548;&#xd558;&#xba74; default&#xb85c; &#xc11c;&#xc6b8;&#xc2dc;&#xb85c; &#xb123;&#xc74c;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        String text = null;

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        Switch aSwitch = (Switch) findViewById(R.id.single);
        final ListView listviewPhtoList = (ListView) findViewById(R.id.listview_f);
        String[] from = new String[]{"title", "category", "roadAddress"};
        //"link","telephone","description","address","mapx","mapy"
        int[] to = new int[]{R.id.textview_main_listviewdata1, R.id.textview_main_listviewdata2,
                R.id.textview_main_listviewdata3};
        tv = (TextView) findViewById(R.id.text);
        storeinfoList = new ArrayList<HashMap<String, String>>();
        spinner = findViewById(R.id.txt_question_type);
        sAdapter = ArrayAdapter.createFromResource(this, R.array.question, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);
        refresh = findViewById(R.id.refresh);
        adapter = new SimpleAdapter(this, storeinfoList, R.layout.food_listview_items, from, to);
        listviewPhtoList.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView  parent, View view, int position, long id) {
                Log.d("##############",String.valueOf(spinner.getSelectedItemPosition()));
                if(spinner.getSelectedItemPosition()==2){
                    if(!isPermission){
                        callPermission();
                        return;
                    }
                    gps = new GpsInfo(food_list.this);
                    progressDialog = new ProgressDialog(food_list.this);
                    progressDialog.setMessage("Please wait.....");
                    progressDialog.show();
                    // GPS &#xc0ac;&#xc6a9;&#xc720;&#xbb34; &#xac00;&#xc838;&#xc624;&#xae30;
                    if (gps.isGetLocation()) {
                        progressDialog.dismiss();
                    // GPS &#xc0ac;&#xc6a9;&#xc720;&#xbb34; &#xac00;&#xc838;&#xc624;&#xae30;

                        Geocoder geocoder = new Geocoder(food_list.this, Locale.getDefault());
                        List<Address> addr = null;


                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        try{
                            addr = geocoder.getFromLocation(latitude,longitude,1);
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (IllegalArgumentException illegalArgumentException) {
                            //&#xc798;&#xbabb;&#xb41c; GPS &#xb97c; &#xac00;&#xc838;&#xc653;&#xc744;&#xc2dc;
                        }

                        if(addr == null || addr.size() == 0){

                        }else{
                            Address address = addr.get(0);
//                        Toast.makeText(
//                                getApplicationContext(),
//                                address.getAddressLine(0).toString(),
//                                Toast.LENGTH_LONG).show();
                            String addr_name_array[] = address.getAddressLine(0).split(" ");
                            real_address = addr_name_array[1]+addr_name_array[2];

                        }
                    } else {
                        // GPS &#xb97c; &#xc0ac;&#xc6a9;&#xd560;&#xc218; &#xc5c6;&#xc73c;&#xbbc0;&#xb85c;
                        gps.showSettingsAlert();
                    }
                }else if(spinner.getSelectedItemPosition()==0){

                    real_address = "서울시";
                }
                else{
                    real_address = "서울시 " + (String)spinner.getSelectedItem();
                }
            }
            public void onNothingSelected(AdapterView parent) {

            }
        });

        //&#xc5ec;&#xae30;&#xc11c;&#xbd80;&#xd130; GPS&#xad00;&#xb828;&#xd55c; &#xb0b4;&#xc6a9;(&#xba54;&#xc778;&#xd654;&#xba74;&#xc5d0; &#xb123;&#xc5b4;&#xc57c;&#xd568;)
        listviewPhtoList.setClickable(true);

        listviewPhtoList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), food_detail.class);
                food_detail fd = new food_detail();
                intent.putExtra("index",position);
                intent.putExtra("storeinfoList", storeinfoList.get(position));
                fd.setting_list(storeinfoList);
                startActivity(intent);
            }
        });



        arrayList = new ArrayList<HashMap<String, String>>();

        //&#xc5ec;&#xae30;&#xae4c;&#xc9c0;

        try {
            progressDialog = new ProgressDialog(food_list.this);
            progressDialog.setMessage("Please wait.....");
            progressDialog.show();
            text = URLEncoder.encode(real_address+" 식당", "UTF-8");
            doJSONParser(text);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String[] check = {"no"};
        search = (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(this);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                try {
                    storeinfoList.clear();
                    progressDialog = new ProgressDialog(food_list.this);
                    progressDialog.setMessage("Please wait.....");
                    progressDialog.show();
                    String text = null;
                    if(isChecked) {
                        text = URLEncoder.encode(real_address + " 1인 식당", "UTF-8");
                        check[0] = "yes";
                    }else{
                        text = URLEncoder.encode(real_address+" 식당", "UTF-8");
                        check[0] = "no";
                    }
                    doJSONParser(text);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog = new ProgressDialog(food_list.this);
                    progressDialog.setMessage("Please wait.....");
                    progressDialog.show();
                    String text = null;
                    if(check[0].equals("yes")) {
                        text = URLEncoder.encode(real_address + " 1인 식당", "UTF-8");
                    }else if(check[0].equals("no")){
                        text = URLEncoder.encode(real_address+" 식당", "UTF-8");
                    }
                    doJSONParser(text);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
            }
        });

    }

    //gps &#xad00;&#xb828;&#xb0b4;&#xc6a9; &#xc5ec;&#xae30;&#xc11c;&#xbd80;&#xd130;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // &#xc804;&#xd654;&#xbc88;&#xd638; &#xad8c;&#xd55c; &#xc694;&#xccad;
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    //&#xc5ec;&#xae30;&#xae4c;&#xc9c0;

    private final MyHandler mHandler = new MyHandler(food_list.this);


    private static class MyHandler extends Handler {
        private final WeakReference<food_list> weakReference;

        public MyHandler(food_list food_list) {
            weakReference = new WeakReference<food_list>(food_list);
        }

        @Override
        public void handleMessage(Message msg) {

            food_list food_list = weakReference.get();

            if (food_list != null) {
                switch (msg.what) {

                    case LOAD_SUCCESS:
                        food_list.progressDialog.dismiss();
                        food_list.adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
    private   String clientId = "gACXXnwBfyo7uc2UomKt";
    private String clientSecret = "ywYHcu7NVP";
    private  String apiURL = "https://openapi.naver.com/v1/search/local.json?query=";

    public void doJSONParser(final String keyword) throws UnsupportedEncodingException {

        int display = 2;
        if ( keyword == null) return;

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;
                try{
                    arrayList.clear();
                    storeinfoList.clear();
                    URL url = new URL(apiURL+keyword+"&display="+100);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    // con.setReadTimeout(3000);
                    // con.setConnectTimeout(3000);
                    // con.setDoOutput(true);
                    // con.setDoInput(true);
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    // con.setUseCaches(false);
                    // con.connect();
                    // post request

                    String postParams = "source=ko&target=en&text=" + keyword;
                    //     con.setDoOutput(true);
                    //    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    //   wr.writeBytes(postParams);
                    //  wr.flush();
                    //  wr.close();
                    int responseCode = con.getResponseCode();
                    BufferedReader br;
                    if(responseCode==200) { // &#xc815;&#xc0c1; &#xd638;&#xcd9c;
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {  // &#xc5d0;&#xb7ec; &#xbc1c;&#xc0dd;
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String inputLine;

                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    test = response.toString();
                    con.disconnect();
                } catch (Exception e) {
                    System.out.println(e);

                }
                if(jsonParser(test)){
                    Message message = mHandler.obtainMessage(LOAD_SUCCESS);
                    mHandler.sendMessage(message);
                }

                arrayList.addAll(storeinfoList);
            }
        });
        thread.start();
    }


    public boolean jsonParser(String jsonString){

        if (jsonString == null ) return false;
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray real = root.getJSONArray("items");
            storeinfoList.clear();

            for(int i = 0; i < real.length(); i++){
                JSONObject StoreInfo = real.getJSONObject(i);
                String title = removeTag(StoreInfo.getString("title"));
                String category = StoreInfo.getString("category");
                String address = StoreInfo.getString("address");
                String roadAddress = StoreInfo.getString("roadAddress");
                String link = StoreInfo.getString("link");
                String description = StoreInfo.getString("description");
                String telephone = StoreInfo.getString("telephone");
                String mapx_d = StoreInfo.getString("mapx");
                String mapy_d = StoreInfo.getString("mapy");
                HashMap<String, String> storeinfoMap = new HashMap<String, String>();

                storeinfoMap.put("title",title);
                storeinfoMap.put("category",category);
                storeinfoMap.put("address",address);
                storeinfoMap.put("roadAddress",roadAddress);
                storeinfoMap.put("mapx",mapx_d);
                storeinfoMap.put("mapy",mapy_d);
                storeinfoMap.put("link",link);
                storeinfoMap.put("description",description);
                storeinfoMap.put("telephone",telephone);
                storeinfoList.add(storeinfoMap);
            }
            return true;
        } catch (JSONException e) {

            Log.d("Error", e.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    private void search(String charText) {
        storeinfoList.clear();

        if (charText.length() == 0) {
            storeinfoList.addAll(arrayList);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).get("title").contains(charText)) {
                    storeinfoList.add(arrayList.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    public String removeTag(String html) throws Exception {
        String noHTMLString = html.replaceAll("\\<.*?\\>", "");

// Remove Carriage return from java String
        noHTMLString = noHTMLString.replaceAll("<([bip])>.*?</\1>", "");
// Remove New line from java string and replace html break
        noHTMLString = noHTMLString.replaceAll("\n", " ");
        noHTMLString = noHTMLString.replaceAll("<(.*?)\\>"," ");//Removes all items in brackets
        noHTMLString = noHTMLString.replaceAll("<(.*?)\\\n"," ");//Must be undeneath
        noHTMLString = noHTMLString.replaceFirst("(.*?)\\>", " ");
        noHTMLString = noHTMLString.replaceAll("&nbsp;"," ");
        noHTMLString = noHTMLString.replaceAll("&amp;"," ");
        noHTMLString = noHTMLString.replaceAll("&quot;"," ");
        return noHTMLString;

    }


}
