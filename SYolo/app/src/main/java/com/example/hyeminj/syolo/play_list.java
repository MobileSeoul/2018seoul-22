package com.example.hyeminj.syolo;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class play_list extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SimpleAdapter adapter = null;
    private List<HashMap<String,String>> list = null;

    ArrayList<item> list_itemArrayList;
    List<HashMap<String,String>> arrayList;
    SearchView search;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        ListView listView = (ListView)findViewById(R.id.listview_p);

        list_itemArrayList = new ArrayList<item>();
        list = new ArrayList<HashMap<String,String>>();
        String[] from = new String[]{"title","start_date","end_date","place"};
        int[] to = new int[] {R.id.textView_title,R.id.textView_start,R.id.textView_end,R.id.textView_place};
        StrictMode.enableDefaults();

        boolean inAGE = false, inCodename = false, inEtc = false, inHomepage = false, inTitle = false, inStart = false,
                inEnd = false, inPlace = false, inTime = false, inQuiry = false, inFree = false, inSpon = false,
                inLink = false, inImg = false, inTarget = false, inFee = false, inPro = false;

        String age = null, codename = null, etc = null, homepage = null, title = null, start = null,
                end = null, place = null, time = null, quiry = null, free = null, spon = null,
                link = null, img = null, target = null, fee = null, program = null;

        try {
            URL url = new URL("http://openapi.seoul.go.kr:8088/42697a4e62776c61373241737a516c/xml/SearchConcertDetailService/1/1000/");
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("AGELIMIT")) {
                            inAGE = true;
                        }
                        if (parser.getName().equals("CODENAME")) {
                            inCodename = true;
                        }
                        if (parser.getName().equals("ETC_DESC")) {
                            inEtc = true;
                        }
                        if (parser.getName().equals("HOMEPAGE")) {
                            inHomepage = true;
                        }
                        if (parser.getName().equals("INQUIRY")) {
                            inQuiry = true;
                        }
                        if (parser.getName().equals("IS_FREE")) {
                            inFree = true;
                        }
                        if (parser.getName().equals("MAIN_IMG")) {
                            inImg = true;
                        }
                        if (parser.getName().equals("ORG_LINK")) {
                            inLink = true;
                        }
                        if (parser.getName().equals("SPONSOR")) {
                            inSpon = true;
                        }
                        if (parser.getName().equals("TIME")) {
                            inTime = true;
                        }
                        if (parser.getName().equals("USE_FEE")) {
                            inFee = true;
                        }
                        if (parser.getName().equals("USE_TRGT")) {
                            inTarget = true;
                        }
                        if (parser.getName().equals("TITLE")) {
                            inTitle = true;
                        }
                        if (parser.getName().equals("STRTDATE")) {
                            inStart = true;
                        }
                        if (parser.getName().equals("END_DATE")) {
                            inEnd = true;
                        }
                        if (parser.getName().equals("PLACE")) {
                            inPlace = true;
                        }
                        if(parser.getName().equals("PROGRAM")){
                            inPro = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (inAGE) {
                            age = parser.getText();
                            inAGE = false;
                        }
                        if (inCodename) {
                            codename = parser.getText();
                            inCodename = false;
                        }
                        if (inEtc) {
                            etc = parser.getText();
                            inEtc = false;
                        }
                        if (inHomepage) {
                            homepage = parser.getText();
                            inHomepage = false;
                        }
                        if (inQuiry) {
                            quiry = parser.getText();
                            inQuiry = false;
                        }
                        if (inFree) {
                            free = parser.getText();
                            inFree = false;
                        }
                        if (inImg) {
                            img = parser.getText();
                            inImg = false;
                        }
                        if (inLink) {
                            link = parser.getText();
                            inLink = false;
                        }
                        if (inSpon) {
                            spon = parser.getText();
                            inSpon = false;
                        }
                        if (inTime) {
                            time = parser.getText();
                            inTime = false;
                        }
                        if (inFee) {
                            fee = parser.getText();
                            inFee = false;
                        }
                        if (inTarget) {
                            target = parser.getText();
                            inTarget = false;
                        }
                        if (inTitle) {
                            title = removeTag(parser.getText());
                            inTitle = false;
                        }
                        if (inStart) {
                            start = parser.getText();
                            inStart = false;
                        }
                        if (inEnd) {
                            end = parser.getText();
                            inEnd = false;
                        }
                        if (inPlace) {
                            place = parser.getText();
                            inPlace = false;
                        }
                        if(inPro){
                            program = parser.getText();
                            inPro = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("row")) {
                            if(codename.contains("연극")){
                                HashMap<String,String> map = new HashMap<String,String>();

                                map.put("title",title);
                                map.put("start_date",start);
                                map.put("end_date",end);
                                map.put("place",place);

                                list.add(map);
                                list_itemArrayList.add(new item(title,start,end,place,age,codename,etc,homepage,time,quiry,free,spon,link,img,target,fee,program));

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(this,list,R.layout.activity_item,from,to);
        listView.setAdapter(adapter);

        arrayList = new ArrayList<HashMap<String,String>>();
        arrayList.addAll(list);

        search = (SearchView)findViewById(R.id.search);
        search.setOnQueryTextListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), detail.class);
                intent.putExtra("title",list_itemArrayList.get(position).getTitle());
                intent.putExtra("start",list_itemArrayList.get(position).getStart_date());
                intent.putExtra("end",list_itemArrayList.get(position).getEnd_date());
                intent.putExtra("place",list_itemArrayList.get(position).getPlace());
                intent.putExtra("age",list_itemArrayList.get(position).getAge());
                intent.putExtra("etc",list_itemArrayList.get(position).getEtc());
                intent.putExtra("homepage",list_itemArrayList.get(position).getHomepage());
                intent.putExtra("time",list_itemArrayList.get(position).getTime());
                intent.putExtra("quiry",list_itemArrayList.get(position).getQuiry());
                intent.putExtra("free",list_itemArrayList.get(position).getTitle());
                intent.putExtra("spon",list_itemArrayList.get(position).getSpon());
                intent.putExtra("link",list_itemArrayList.get(position).getLink());
                intent.putExtra("img",list_itemArrayList.get(position).getImg());
                intent.putExtra("target",list_itemArrayList.get(position).getTarget());
                intent.putExtra("fee",list_itemArrayList.get(position).getFee());
                intent.putExtra("program",list_itemArrayList.get(position).getProgram());
                intent.putExtra("type","play");

                startActivity(intent);
            }
        });
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
        list.clear();

        if(charText.length() == 0){
            list.addAll(arrayList);
        }else{
            for(int i=0; i < arrayList.size(); i++){
                if(arrayList.get(i).get("title").contains(charText)){
                    list.add(arrayList.get(i));
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
        noHTMLString = noHTMLString.replaceAll("&amp;","&");
        noHTMLString = noHTMLString.replaceAll("&quot;","\"");
        noHTMLString = noHTMLString.replaceAll("&#39;","\'");
        return noHTMLString;

    }
}
