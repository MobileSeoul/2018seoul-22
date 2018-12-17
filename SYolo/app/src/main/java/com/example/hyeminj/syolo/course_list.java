package com.example.hyeminj.syolo;

import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class course_list extends AppCompatActivity {
    CListAdapter myListAdapter;
    ArrayList<course_item> image_itemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        Intent intent = getIntent();
        String bar_title = intent.getStringExtra("title");
        TextView textView_title = findViewById(R.id.textView_title);
        textView_title.setText(bar_title);

        ListView listView = (ListView) findViewById(R.id.listview_c);
        image_itemArrayList = new ArrayList<course_item>();
        TextView textView_distance = (TextView)findViewById(R.id.textView_distance);
        TextView textView_time = (TextView)findViewById(R.id.textView_time);

        StrictMode.enableDefaults();

        boolean  Distance = false, Time = false,ID = false, Content = false, Title = false, Image = false;

        String distance = null, time = null, id = null, content = null, title = null, image = null;

        try {
            URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=25&contentId="+intent.getStringExtra("id")+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&listYN=Y");
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            int i = 0;
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("distance")) {
                            Distance = true;
                        }
                        if (parser.getName().equals("taketime")) {
                            Time = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (Distance) {
                            distance = parser.getText();
                            Distance = false;
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

        textView_distance.setText("총 거리 : "+distance);
        textView_time.setText("걸리는 시간 : " + time);

        try {
            URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?ServiceKey=bzSNoAg0PRCkN55%2Br4HAAn8JW7RKd8%2B3oOUST6I113ZxsLdw8magLcWKMb16ZokU8Bv735iH%2FjO0MCgUuJvJhw%3D%3D&contentTypeId=25&contentId="+intent.getStringExtra("id")+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&listYN=Y");
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            int i = 0;
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("subcontentid")) {
                            ID = true;
                        }
                        if (parser.getName().equals("subdetailoverview")) {
                            Content = true;
                        }
                        if (parser.getName().equals("subdetailimg")) {
                            Image = true;
                        }
                        if (parser.getName().equals("subname")) {
                            Title = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (ID) {
                            id = parser.getText();
                            ID = false;
                        }
                        if (Content) {
                            content = parser.getText();
                            Content = false;
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
                                image_itemArrayList.add(new course_item(id,content,image, title));
                                i = 0;
                            } else {
                                image_itemArrayList.add(new course_item(id,content,"https://cdn.pixabay.com/photo/2015/10/30/12/14/search-1013911_1280.jpg", title));
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
        myListAdapter = new CListAdapter(course_list.this, image_itemArrayList);
        listView.setAdapter(myListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), place_detail.class);
                String temp = image_itemArrayList.get(position).getId();
                if(!(temp.equals("129760")||temp.equals("513517")||temp.equals("992288")||temp.equals("1922694")||temp.equals("403068")||temp.equals("135719")||temp.equals("1019327"))){
                    intent.putExtra("id", image_itemArrayList.get(position).getId());
                    intent.putExtra("image",image_itemArrayList.get(position).getImage());
                    startActivity(intent);
                }
            }
        });

    }
}
