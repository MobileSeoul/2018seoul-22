package com.example.hyeminj.syolo;

import android.content.Intent;
import android.icu.text.StringPrepParseException;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class place_list extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    ListView listView;
    MyListAdapter myListAdapter;
    ArrayList<image_item> image_itemArrayList;
    ArrayList<image_item> arrayList;

    SearchView search;
    TextView textView;
    String course_url, info_url;
    Switch aSwitch;
    Spinner spinner;

    boolean ID = false, Title = false, Image = false;

    String id = null, title = null, image = null;
    String url_s;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        aSwitch = (Switch) findViewById(R.id.switch1);
        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.question,R.layout.support_simple_spinner_dropdown_item);

        ListView listView = (ListView) findViewById(R.id.listview_place);
        image_itemArrayList = new ArrayList<image_item>();
        search = (SearchView)findViewById(R.id.search);

        StrictMode.enableDefaults();

        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);


        try {
            URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&areaCode=1&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=545");
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            int i = 0;
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("contentid")) {
                            ID = true;
                        }
                        if (parser.getName().equals("firstimage")) {
                            Image = true;
                        }
                        if (parser.getName().equals("title")) {
                            Title = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (ID) {
                            id = parser.getText();
                            ID = false;
                        }
                        if (Image) {
                            image = parser.getText();
                            i = 1;
                            Image = false;
                        }
                        if (Title) {
                            title = parser.getText();
                            Title = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            if (i == 1) {
                                image_itemArrayList.add(new image_item(id,image, title));
                                i = 0;
                            } else {
                                image_itemArrayList.add(new image_item(id,"https://cdn.samsung.com/etc/designs/smg/global/imgs/support/cont/NO_IMG_600x600.png", title));
                            }
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
        arrayList = new ArrayList<image_item>();
        arrayList.addAll(image_itemArrayList);

        myListAdapter = new MyListAdapter(place_list.this, image_itemArrayList);
        listView.setAdapter(myListAdapter);

        search.setOnQueryTextListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), place_detail.class);
                intent.putExtra("id", image_itemArrayList.get(position).getId());
                intent.putExtra("position",position);
                intent.putExtra("image",image_itemArrayList.get(position).getImage());
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    private void search(String newText) {
        image_itemArrayList.clear();

        if(newText.length() == 0){
            image_itemArrayList.addAll(arrayList);
        }else{
            for(int i=0; i<arrayList.size(); i++){
                if(arrayList.get(i).getTitle().contains(newText)){
                    image_itemArrayList.add(arrayList.get(i));
                }
            }
        }
        myListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long long_id) {
        final int select_position = position;
        if(aSwitch.isChecked()==false){
            if(select_position!=0){
                url_s = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&areaCode=1&sigunguCode=" + select_position + "&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=300&pageNo=1";
            }else{
                url_s = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&areaCode=1&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=545";
            }
            ListView listView = (ListView) findViewById(R.id.listview_place);
            image_itemArrayList = new ArrayList<image_item>();
            myListAdapter.notifyDataSetChanged();
            try {
                URL url = new URL(url_s);
                XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserCreator.newPullParser();

                parser.setInput(url.openStream(), null);

                int parserEvent = parser.getEventType();

                int i = 0;
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG:
                            if (parser.getName().equals("contentid")) {
                                ID = true;
                            }
                            if (parser.getName().equals("firstimage")) {
                                Image = true;
                            }
                            if (parser.getName().equals("title")) {
                                Title = true;
                            }
                            break;

                        case XmlPullParser.TEXT:
                            if (ID) {
                                id = parser.getText();
                                ID = false;
                            }
                            if (Image) {
                                image = parser.getText();
                                i = 1;
                                Image = false;
                            }
                            if (Title) {
                                title = parser.getText();
                                Title = false;
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                if (i == 1) {
                                    image_itemArrayList.add(new image_item(id, image, title));
                                    i = 0;
                                } else {
                                    image_itemArrayList.add(new image_item(id, "https://cdn.samsung.com/etc/designs/smg/global/imgs/support/cont/NO_IMG_600x600.png", title));
                                }
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
            arrayList = new ArrayList<image_item>();
            arrayList.addAll(image_itemArrayList);

            myListAdapter = new MyListAdapter(place_list.this, image_itemArrayList);
            listView.setAdapter(myListAdapter);

            search.setOnQueryTextListener(place_list.this);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), place_detail.class);
                    intent.putExtra("id", image_itemArrayList.get(position).getId());
                    intent.putExtra("image", image_itemArrayList.get(position).getImage());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (select_position != 0) {
                        url_s = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&areaCode=1&sigunguCode=" + select_position + "&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=300&pageNo=1";
                    } else {
                        url_s = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=12&areaCode=1&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=545";
                    }
                    ListView listView = (ListView) findViewById(R.id.listview_place);
                    image_itemArrayList = new ArrayList<image_item>();
                    myListAdapter.notifyDataSetChanged();
                    try {
                        URL url = new URL(url_s);
                        XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = parserCreator.newPullParser();

                        parser.setInput(url.openStream(), null);

                        int parserEvent = parser.getEventType();

                        int i = 0;
                        while (parserEvent != XmlPullParser.END_DOCUMENT) {
                            switch (parserEvent) {
                                case XmlPullParser.START_TAG:
                                    if (parser.getName().equals("contentid")) {
                                        ID = true;
                                    }
                                    if (parser.getName().equals("firstimage")) {
                                        Image = true;
                                    }
                                    if (parser.getName().equals("title")) {
                                        Title = true;
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    if (ID) {
                                        id = parser.getText();
                                        ID = false;
                                    }
                                    if (Image) {
                                        image = parser.getText();
                                        i = 1;
                                        Image = false;
                                    }
                                    if (Title) {
                                        title = parser.getText();
                                        Title = false;
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (parser.getName().equals("item")) {
                                        if (i == 1) {
                                            image_itemArrayList.add(new image_item(id, image, title));
                                            i = 0;
                                        } else {
                                            image_itemArrayList.add(new image_item(id, "https://cdn.samsung.com/etc/designs/smg/global/imgs/support/cont/NO_IMG_600x600.png", title));
                                        }
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
                    arrayList = new ArrayList<image_item>();
                    arrayList.addAll(image_itemArrayList);

                    myListAdapter = new MyListAdapter(place_list.this, image_itemArrayList);
                    listView.setAdapter(myListAdapter);

                    search.setOnQueryTextListener(place_list.this);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(), place_detail.class);
                            intent.putExtra("id", image_itemArrayList.get(position).getId());
                            intent.putExtra("image", image_itemArrayList.get(position).getImage());
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    });
                } else {
                    ListView listView = (ListView) findViewById(R.id.listview_place);
                    image_itemArrayList = new ArrayList<image_item>();

                    StrictMode.enableDefaults();

                    boolean ID = false, Title = false, Image = false;

                    String id = null, title = null, image = null;

                    URL url = null;
                    try {
                        url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=25&areaCode=1&sigunguCode=&cat1=C01&cat2=C0113&cat3=C01130001&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12");

                        XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = parserCreator.newPullParser();

                        parser.setInput(url.openStream(), null);

                        int parserEvent = parser.getEventType();

                        int i = 0;
                        while (parserEvent != XmlPullParser.END_DOCUMENT) {
                            switch (parserEvent) {
                                case XmlPullParser.START_TAG:
                                    if (parser.getName().equals("contentid")) {
                                        ID = true;
                                    }
                                    if (parser.getName().equals("firstimage")) {
                                        Image = true;
                                    }
                                    if (parser.getName().equals("title")) {
                                        Title = true;
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    if (ID) {
                                        id = parser.getText();
                                        ID = false;
                                    }
                                    if (Image) {
                                        image = parser.getText();
                                        i = 1;
                                        Image = false;
                                    }
                                    if (Title) {
                                        title = parser.getText();
                                        Title = false;
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (parser.getName().equals("item")) {
                                        if (i == 1) {
                                            image_itemArrayList.add(new image_item(id, image, title));
                                            i = 0;
                                        } else {
                                            image_itemArrayList.add(new image_item(id, "https://cdn.samsung.com/etc/designs/smg/global/imgs/support/cont/NO_IMG_600x600.png", title));
                                        }
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
                    myListAdapter = new MyListAdapter(place_list.this, image_itemArrayList);
                    listView.setAdapter(myListAdapter);

                    search.setOnQueryTextListener(place_list.this);
                    if (isChecked) {
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getApplicationContext(), course_list.class);
                                intent.putExtra("id", image_itemArrayList.get(position).getId());
                                intent.putExtra("title", image_itemArrayList.get(position).getTitle());
                                intent.putExtra("position", position);
                                intent.putExtra("image", image_itemArrayList.get(position).getImage());
                                startActivity(intent);
                            }
                        });
                    } else {
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getApplicationContext(), place_detail.class);
                                intent.putExtra("id", image_itemArrayList.get(position).getId());
                                intent.putExtra("position", position);
                                intent.putExtra("image", image_itemArrayList.get(position).getImage());
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
