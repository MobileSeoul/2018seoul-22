package com.example.hyeminj.syolo;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class hospital_info extends AppCompatActivity implements OnMapReadyCallback {

    private boolean Dist = false, Addr = false, Category = false, Name = false, Tel = false,EndTime = false, StartTime = false;
    private String dist = null,time = null, addr = null, category = null, name = null, tel = null,endTime = null, startTime = null;

    Button b;
    private  GpsInfo gps;
    private Geocoder geocoder;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    public static final int LOAD_SUCCESS = 101;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private double latitude = 37.56;
    private double longitude = 126.97;
    private SimpleAdapter adapter = null;
    private List<HashMap<String,String>> list = null;
    private ArrayList<List<Address>> point_addr=null;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        b = (Button)findViewById(R.id.button5);
        final ListView HospitalList = (ListView)findViewById(R.id.listview_f_2);
        String[] from = new String[]{"name", "category", "time","addr"};
        //"link","telephone","description","address","mapx","mapy"
        int[] to = new int[]{R.id.textview_hospital_listviewdata1, R.id.textview_hospital_listviewdata2,
                R.id.textview_hospital_listviewdata3,R.id.textview_hospital_listviewdata4};
        tv = (TextView) findViewById(R.id.text);
        list = new ArrayList<HashMap<String, String>>();

        //                              Toast.makeText(
        //                        getApplicationContext(), list.indexOf("title"),
        //                      Toast.LENGTH_LONG).show();
        //&#xc5ec;&#xae30;&#xc11c;&#xbd80;&#xd130; GPS&#xad00;&#xb828;&#xd55c; &#xb0b4;&#xc6a9;(&#xba54;&#xc778;&#xd654;&#xba74;&#xc5d0; &#xb123;&#xc5b4;&#xc57c;&#xd568;)
        HospitalList.setClickable(true);

        HospitalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), food_detail.class);
                food_detail fd = new food_detail();
                intent.putExtra("index",position);
                intent.putExtra("hospitalList", list.get(position));
                fd.setting_list(list);
                startActivity(intent);
            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map_place);
        mapFragment.getMapAsync(this);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPermission) {
                    callPermission();
                    return;
                }
                gps = new GpsInfo(hospital_info.this);
                // GPS &#xc0ac;&#xc6a9;&#xc720;&#xbb34; &#xac00;&#xc838;&#xc624;&#xae30;
                if (gps.isGetLocation()) {

                    Geocoder geocoder = new Geocoder(hospital_info.this, Locale.getDefault());
                    List<Address> addresses = null;

                    latitude= gps.getLatitude();

                    longitude = gps.getLongitude();
                    isPermission = true;

                    try{
                        addresses = geocoder.getFromLocation(latitude,longitude,1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (IllegalArgumentException illegalArgumentException) {
                        //&#xc798;&#xbabb;&#xb41c; GPS &#xb97c; &#xac00;&#xc838;&#xc653;&#xc744;&#xc2dc;
                    }
                    if(addresses == null || addresses.size() == 0){

                    }else{
                        Address address = addresses.get(0);
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                    }
                } else {
                    // GPS &#xb97c; &#xc0ac;&#xc6a9;&#xd560;&#xc218; &#xc5c6;&#xc73c;&#xbbc0;&#xb85c;
                    gps.showSettingsAlert();
                }

            }
        });
        StrictMode.enableDefaults();
        try{
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            URL url = new URL("http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncLcinfoInqire?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&pageNo=1&numOfRows=10&WGS84_LON=" + longitude + "&WGS84_LAT=" + latitude);
            XmlPullParser parser = parserCreator.newPullParser();
            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            point_addr = new ArrayList<List<Address>>();
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("distance")) {
                            Dist = true;
                        }
                        if (parser.getName().equals("dutyAddr")) {
                            Addr = true;
                        }
                        if (parser.getName().equals("dutyDivName")) {
                            Category = true;
                        }
                        if (parser.getName().equals("dutyName")) {
                            Name = true;
                        }
                        if (parser.getName().equals("dutyTel1")) {
                            Tel = true;
                        }
                        if (parser.getName().equals("startTime")) {
                            StartTime = true;
                        }
                        if (parser.getName().equals("endTime")) {
                            EndTime = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (Dist) {
                            dist = parser.getText();
                            Dist = false;
                        }
                        if (Addr) {
                            isPermission = true;
                            geocoder = new Geocoder(this);
                            addr = parser.getText();

                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocationName(addr, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            point_addr.add(addressList);
                            Addr = false;
                        }
                        if (Category) {
                            category= parser.getText();
                            Category = false;
                        }
                        if (Name) {
                            name = parser.getText();
                            Name = false;
                        }
                        if (Tel) {
                            tel = parser.getText();
                            Tel = false;
                        }
                        if (StartTime && EndTime) {
                            startTime = parser.getText();
                            endTime = parser.getText();
                            time = startTime + "~" + endTime;
                            StartTime = false;
                            EndTime = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            HashMap<String,String> map = new HashMap<String,String>();
                            map.put("dist",dist);
                            map.put("addr",addr);
                            map.put("category",category);
                            map.put("name",name);
                            map.put("tel",tel);
                            map.put("time",time);
                            list.add(map);
                        }
                        break;
                }
                parserEvent = parser.next();
            }
            for(int i = 0; i<point_addr.size(); i++){
                Log.d("#######################",point_addr.get(i).toString());
            }

        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(this, list, R.layout.listview_hospital_item, from, to);
        HospitalList.setAdapter(adapter);
    }
    @Override
    public void onRequestPermissionsResult ( int requestCode, String[] permissions,
                                             int[] grantResults){
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }
    // &#xc804;&#xd654;&#xbc88;&#xd638; &#xad8c;&#xd55c; &#xc694;&#xccad;
    private void callPermission () {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        isPermission = true;
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


        for(int i = 0; i <10; i++){
            address = point_addr.get(i).get(0);
            latitude = address.getLatitude();
            longitude = address.getLongitude();
            Log.d("##############", Double.toString(latitude)+" "+Double.toString(longitude));
            LatLng Place = new LatLng(latitude,longitude);

            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions.position(Place);
            makerOptions.title(name);
            googleMap.addMarker(makerOptions);

        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }
}
