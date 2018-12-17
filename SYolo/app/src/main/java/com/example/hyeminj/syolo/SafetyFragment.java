package com.example.hyeminj.syolo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SafetyFragment extends Fragment implements OnMapReadyCallback{
    private boolean Dist = false, Addr = false, Category = false, Name = false, Tel = false,EndTime = false, StartTime = false;
    private String dist = null,time = null, addr = null, category = null, name = null, tel = null,endTime = null, startTime = null;

    Button b;
    private  GpsInfo gps;
    private Geocoder geocoder;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private double gps_lat;
    private double gps_lng;
    private ProgressDialog progressDialog = null;
    List<HashMap<String, String>> arrayList;
    public static final int LOAD_SUCCESS = 101;
    private boolean isAccessFineLocation = false;
    private GoogleMap mGoogleMap;
    ImageButton refresh;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private double latitude = 37.56;
    private double longitude = 126.97;
    private SimpleAdapter adapter = null;
    private List<HashMap<String,String>> list = null;
    private ArrayList<String> point_addr=null;
    private ArrayList<String> point_name=null;
    private ArrayList<String> point_tel = null;
    private ArrayList<String> point_category = null;
    String test;
    TextView tv;
    TextView header1;
    TextView police;
    TextView hospital;
    ConstraintLayout frame;
    LinearLayout review;
    LinearLayout nothing;
    TextView placeTitle;
    TextView description;
    TextView content;
    ImageView imageView;
    ArrayAdapter sAdapter;
    Spinner spinner;
    Address address;
    TextView placeLink;
    TextView telephone;
    TextView placeCategory;
    TextView roadAddress;
    ImageView favorite;
    Button call_button;
    String real_address = "서울시";
    ImageButton place;
    ListView HospitalList = null;
    private boolean check = false;
    String text;
    private int list_position = 0;

    public SafetyFragment() {
        // Required empty public constructor
    }
    private static View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_safety, container ,false);
            //defining Cards
            hospital = rootView.findViewById(R.id.header_hospital);
            // Inflate the layout for this fragment
            refresh = rootView.findViewById(R.id.refresh);
            frame = rootView.findViewById(R.id.frame);

            review = rootView.findViewById(R.id.review);
            placeTitle = rootView.findViewById(R.id.place_title);
            b = rootView.findViewById(R.id.button5);
            HospitalList = rootView.findViewById(R.id.listview_f_2);
            tv = (TextView) rootView.findViewById(R.id.text);
            call_button = rootView.findViewById(R.id.call_button);
            // place = rootView.findViewById(R.id.imageView_place);

            //  StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            sAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.question, android.R.layout.simple_spinner_dropdown_item);

            review.setVisibility(View.VISIBLE);
            hospital.setBackgroundResource(R.drawable.border);
            String[] from = new String[]{"title", "category", "address","telephone"};
            //"link","telephone","description","address","mapx","mapy"
            int[] to = new int[]{R.id.textview_hospital_listviewdata1, R.id.textview_hospital_listviewdata2,
                    R.id.textview_hospital_listviewdata3,R.id.textview_hospital_listviewdata4};
            list = new ArrayList<HashMap<String, String>>();

            //                              Toast.makeText(
            //                        getApplicationContext(), list.indexOf("title"),
            //                      Toast.LENGTH_LONG).show();
            //&#xc5ec;&#xae30;&#xc11c;&#xbd80;&#xd130; GPS&#xad00;&#xb828;&#xd55c; &#xb0b4;&#xc6a9;(&#xba54;&#xc778;&#xd654;&#xba74;&#xc5d0; &#xb123;&#xc5b4;&#xc57c;&#xd568;)
            spinner = rootView.findViewById(R.id.txt_question_type);
            sAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.question, android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(sAdapter);
            adapter = new SimpleAdapter(getActivity(), list, R.layout.listview_hospital_item, from, to);
            //   adapter = new SimpleAdapter(rootView.getContext(), list, R.layout.listview_hospital_item, from, to);
            //       HospitalList.setAdapter(adapter);
            HospitalList.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView  parent, View view, int position, long id) {
                    Log.d("##############",String.valueOf(spinner.getSelectedItemPosition()));


                    if(spinner.getSelectedItemPosition()==0) {
                        real_address = "서울시 병원";
                    }else{
                        real_address = "서울시 " + (String)spinner.getSelectedItem()+" 병원";
                    }
                }
                public void onNothingSelected(AdapterView parent) {

                }
            });
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please wait.....");
                        progressDialog.show();
                        String text = null;
                        text = real_address;
                        doJSONParser(text);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();

                    }
                }
            });

            call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDial(v);
                }
            });
            HospitalList.setClickable(true);

            HospitalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                    AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                    //       ab.setMessage(Html.fromHtml(" "));
                    ab.setPositiveButton("전화걸기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int num = HospitalList.getPositionForView(view);
                            openDial_list(view, num);
                        }
                    });
                    ab.setNegativeButton("위치보기",null);
                    ab.show();
                    list_position =  HospitalList.getPositionForView(view);
                    onMapReady(mGoogleMap);

                    check = true;

                    //  onMapReady(mGoogleMap);
                }
            });

            FragmentManager fragmentManager = getActivity().getFragmentManager();
            MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map_place);
            mapFragment.getMapAsync(this);
            StrictMode.enableDefaults();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait.....");
            progressDialog.show();
            text = URLEncoder.encode(real_address+" 병원", "UTF-8");
            doJSONParser(text);




            //        rootView = inflater.inflate(R.layout.fragment_safety, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return rootView;

    }

    public void changeList(){
    }
    public void openDial_list(View view, int position){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String receiver =point_tel.get(position);
        intent.setData(Uri.parse("tel:"+receiver));
        startActivity(intent);

    }
    public void openDial(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String receiver ="112";
        intent.setData(Uri.parse("tel:"+receiver));
        startActivity(intent);
    }

    private void callPermission () {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
        }
    }

    private final SafetyFragment.MyHandler mHandler = new SafetyFragment.MyHandler(SafetyFragment.this);


    private static class MyHandler extends Handler {
        private final WeakReference<SafetyFragment> weakReference;

        public MyHandler(SafetyFragment SafetyFragment) {
            weakReference = new WeakReference<SafetyFragment>(SafetyFragment);
        }

        @Override
        public void handleMessage(Message msg) {

            SafetyFragment SafetyFragment = weakReference.get();

            if (SafetyFragment != null) {
                switch (msg.what) {

                    case LOAD_SUCCESS:
                        SafetyFragment.progressDialog.dismiss();
                        SafetyFragment.adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mGoogleMap = googleMap;
        isPermission = true;
        //    private double latitude = 37.56;
        //    private double longitude = 126.97;
        LatLng PLACE = new LatLng(37.56,126.97);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        geocoder = new Geocoder(rootView.getContext());
        if(check){
            geocoder = new Geocoder(getActivity());
            addr = point_addr.get(list_position);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(addr, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
           PLACE = new LatLng(latitude,longitude);

            String name = point_name.get(list_position);
            latitude = address.getLatitude();
            longitude = address.getLongitude();

            LatLng Place = new LatLng(latitude,longitude);

            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions.position(Place);
            makerOptions.title(name);
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.hospital_placeholder);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            mGoogleMap.addMarker(makerOptions);
            check = false;
            list_position = 0;
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(Place));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));




           /* } else{
                address =  point_addr.get(list_position).get(0);
                latitude = address.getLatitude();
                longit
                                latitude = address.getLatitude();
                longitude = address.getLongitude();
                PLACE = new LatLng(latitude,longitude);

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                check = false;
                list_position = 0;

                //              private List<HashMap<String,String>> list = null;
                //              private ArrayList<List<Address>> point_addr=null;
                Log.d("########ADDRESS", address.toString());

            }*/
            review.setVisibility(View.VISIBLE);

        }
    }
    private   String clientId = "gACXXnwBfyo7uc2UomKt";
    private String clientSecret = "ywYHcu7NVP";
    private  String apiURL = "https://openapi.naver.com/v1/search/local.json?query=";
    public static StringBuilder sb;

    public void doJSONParser(final String keyword) throws UnsupportedEncodingException {

        int display = 2;
        if ( keyword == null) return;

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;
                try{

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
                    // con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/4.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 1.1.4322; .NET CLR 3.5.30729; InfoPath.1; .NET CLR 3.0.30618)");
                    BufferedReader br;
                    if(responseCode==200) { // &#xc815;&#xc0c1; &#xd638;&#xcd9c;
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {  // &#xc5d0;&#xb7ec; &#xbc1c;&#xc0dd;
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    sb = new StringBuilder();
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


            }
        });
        thread.start();
    }


    public boolean jsonParser(String jsonString){
        point_name = new ArrayList<String>();
        point_addr = new ArrayList<String>();
        point_tel = new ArrayList<String>();
        point_category = new ArrayList<String>();
        if (jsonString == null ) return false;
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray real = root.getJSONArray("items");
            list.clear();

            for(int i = 0; i < real.length(); i++){
                JSONObject StoreInfo = real.getJSONObject(i);
                String title = removeTag(StoreInfo.getString("title"));
                point_name.add(title);
                String category = StoreInfo.getString("category");
                point_category.add(category);
                String address = StoreInfo.getString("address");
                point_addr.add(address);
                String telephone = StoreInfo.getString("telephone");
                point_tel.add(telephone);
                HashMap<String, String> storeinfoMap = new HashMap<String, String>();

                storeinfoMap.put("title",title);
                storeinfoMap.put("category",category);
                storeinfoMap.put("address",address);
                storeinfoMap.put("telephone",telephone);
                list.add(storeinfoMap);
            }
            return true;
        } catch (JSONException e) {

            Log.d("Error", e.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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