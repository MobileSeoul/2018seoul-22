package com.example.hyeminj.syolo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class lodge_list extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    private SimpleAdapter adapter = null;
    private List<HashMap<String, String>> infoList = null;

    private String UPSO_NM = null, BIZCON = null, RDNMADR = null, TELNO = null;
    private boolean inrow = false, inUPSO_NM = false, inBIZCON = false, inRDNMADR = false, inTELNO = false;

    List<HashMap<String, String>> arrayList;
    String temp;
    SearchView search;

    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodge_list);

        ListView listView = (ListView) findViewById(R.id.lodge_listview_main_list); //파싱된 결과확인!

        infoList = new ArrayList<HashMap<String, String>>();

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter_d = ArrayAdapter.createFromResource(this,R.array.question,R.layout.support_simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter_d);

        String[] from = new String[]{"UPSO_NM", "BIZCON", "RDNMADR"};
        int[] to = new int[]{R.id.lodge_listviewdata1, R.id.lodge_listviewdata2, R.id.lodge_listviewdata3};
        StrictMode.enableDefaults();

        for (int i = 0; i < 3; i++) {

            try {
                String one, two, three;
                one = "/1/1000/";
                two = "/1001/2000";
                three = "/2001/3000";

                String address = "http://openapi.seoul.go.kr:8088/6d6d4c5577646b773639724e544a65/xml/StateLdgindsty";
                if (i == 0) {
                    address = address + one;
                }
                if (i == 1) {
                    address = address + two;
                }
                if (i == 2) {
                    address = address + three;
                }
                URL url = new URL(address); //검색 URL부분

                XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserCreator.newPullParser();

                parser.setInput(url.openStream(), null);
                Log.e("파싱", "파싱시작합니다.");
                int parserEvent = parser.getEventType();

                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                            if (parser.getName().equals("UPSO_NM")) { //UPSO_NM 만나면 내용을 받을수 있게 하자
                                inUPSO_NM = true;
                            }
                            if (parser.getName().equals("BIZCON")) { //BIZCON 만나면 내용을 받을수 있게 하자
                                inBIZCON = true;
                            }
                            if (parser.getName().equals("RDNMADR")) { //RDNMADR 만나면 내용을 받을수 있게 하자
                                inRDNMADR = true;
                            }
                            if (parser.getName().equals("TELNO")) { //TELNO 만나면 내용을 받을수 있게 하자
                                inTELNO = true;
                            }
                            break;

                        case XmlPullParser.TEXT://parser가 내용에 접근했을때
                            if (inUPSO_NM) { //isUPSO_NM이 true일 때 태그의 내용을 저장.
                                UPSO_NM = parser.getText();
                                inUPSO_NM = false;
                            }
                            if (inBIZCON) { //isBIZCON이 true일 때 태그의 내용을 저장.
                                BIZCON = parser.getText();
                                inBIZCON = false;
                            }
                            if (inRDNMADR) { //isRDNMADR이 true일 때 태그의 내용을 저장.
                                RDNMADR = parser.getText();
                                String s[] = RDNMADR.split(",");
                                RDNMADR = s[0];
                                inRDNMADR = false;
                            }
                            if (inTELNO) { //isTELNO이 true일 때 태그의 내용을 저장.
                                TELNO = parser.getText();
                                inTELNO = false;
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("row") && (false == BIZCON.equals("여인숙업"))) {
                                //status1.setText(status1.getText()+"\n 장소명: "+ UPSO_NM + "\n 분류 : " + BIZCON
                                //        + "\n 주소 : " + RDNMADR + "\n 전화번호 : " + TELNO);
                                inrow = false;

                                HashMap<String, String> infoMap = new HashMap<String, String>();

                                infoMap.put("UPSO_NM", UPSO_NM);
                                infoMap.put("BIZCON", BIZCON);
                                infoMap.put("RDNMADR", RDNMADR);
                                infoMap.put("TELNO", TELNO);

                                infoList.add(infoMap);

                            }
                            break;
                    }
                    parserEvent = parser.next();
                }
            } catch (Exception e) {
                //status1.setAdapter("error");
                Log.e("파싱", e.toString());
            }

            arrayList = new ArrayList<HashMap<String, String>>();
            arrayList.addAll(infoList);

            adapter = new SimpleAdapter(this, infoList, R.layout.lodge_listview_items, from, to);
            listView.setAdapter(adapter);

            search = (SearchView) findViewById(R.id.search);
            search.setOnQueryTextListener(this);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), lodge_detail.class);
                    intent.putExtra("장소명", infoList.get(position).get("UPSO_NM"));
                    intent.putExtra("분류", infoList.get(position).get("BIZCON"));
                    intent.putExtra("주소", infoList.get(position).get("RDNMADR"));
                    intent.putExtra("전화번호", infoList.get(position).get("TELNO"));
                    intent.putExtra("position",position);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    private void search(String charText) {
        infoList.clear();

        if (charText.length() == 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if(temp.equals("서울시 전체")){
                    infoList.addAll(arrayList);
                    break;
                }
                if (arrayList.get(i).get("RDNMADR").contains(temp)) {
                    infoList.add(arrayList.get(i));
                }
            }
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                if(temp.equals("서울시 전체") && arrayList.get(i).get("UPSO_NM").contains(charText)){
                    infoList.add(arrayList.get(i));
                }
                if (arrayList.get(i).get("RDNMADR").contains(temp) && arrayList.get(i).get("UPSO_NM").contains(charText)) {
                    infoList.add(arrayList.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] goo = new String[]{"서울시 전체", "강남구", "강동구", "강북구", "강서구", "관악구", "광진구",
                "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
                "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중량구"};
        infoList.clear();
        if (position == 0) {
            infoList.addAll(arrayList);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).get("RDNMADR").contains(goo[position])) {
                    infoList.add(arrayList.get(i));
                }
            }
        }
        temp = goo[position];
        adapter.notifyDataSetChanged();
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}