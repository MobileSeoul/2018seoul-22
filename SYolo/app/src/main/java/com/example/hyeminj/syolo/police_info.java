package com.example.hyeminj.syolo;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

public class police_info extends AppCompatActivity implements OnMapReadyCallback{

    private boolean Case1_name = false, Name = false, FullName = false, Addr = false, Tel = false;
    private String case1_name = null, name = null, fullName = null,addr = null, tel = null;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    public static final int LOAD_SUCCESS = 101;
    private Geocoder geocoder;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private double latitude = 37.56;
    private double longitude = 126.97;
    private SimpleAdapter adapter = null;
    private List<HashMap<String,String>> list = null;
    private ArrayList<List<Address>> point_addr=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

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
                        // private boolean Case1_name = false, Case2_name = false, FullName = false, Addr = false, Tel = false;
                        //
                        if (parser.getName().equals("CATE1_NAME")) {
                            Case1_name = true;
                        }
                        if (parser.getName().equals("NAME_KOR")) {
                            Name = true;
                        }
                        if (parser.getName().equals("GOV_FN")) {
                            FullName = true;
                        }
                        if (parser.getName().equals("TEL")) {
                            Tel = true;
                        }
                        if (parser.getName().equals("ADD_KOR_ROAD")) {
                            Addr = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        // private boolean Case1_name = false, Case2_name = false, FullName = false, Addr = false, Tel = false;

                        if (Case1_name) {
                            case1_name = parser.getText();
                            Case1_name = false;
                        }
                        if (Name) {
                            name= parser.getText();
                            Name = false;
                        }
                        if (FullName) {
                            fullName= parser.getText();
                            FullName = false;
                        }

                        if (Tel) {
                            tel = parser.getText();
                            Tel = false;
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

                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("row")) {
                            HashMap<String,String> map = new HashMap<String,String>();
                            map.put("case1_name",case1_name);
                            map.put("name",name);
                            map.put("fullName",fullName);
                            map.put("addr",addr);
                            map.put("tel",tel);

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
