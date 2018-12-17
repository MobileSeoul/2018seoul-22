package com.example.hyeminj.syolo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FAQ extends AppCompatActivity {

    private ArrayList<String> mGroupList = null;
    private LinkedHashMap<String,List<String>> mChildList = null;
    private ArrayList<String> mChildListContent = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        setLayout();

        mGroupList = new ArrayList<String>();
        mChildList = new LinkedHashMap<String,List<String>>();

        mGroupList.add("원하는 구를 선택했는데 리스트가 바뀌지 않아요");
        mGroupList.add("병원 지도가 이상하게 떠요");
        mGroupList.add("코스리스트에서 아이템을 선택해도 아무런 변화가 없어요");
        mGroupList.add("관광지에 사진이 뜨지않아요");
        mGroupList.add("제 아이디인데 삭제가 되지 않아요");
        mGroupList.add("개발자가 누구인가요?");

        for(String question : mGroupList){
            if(question.contains("원하는 구")){
                loadChild("옆의 새로 고침 버튼을 눌러보세요!\n새로 고침을 누르지 않아서 바뀌지 않는 것일 수 있습니다.");
            }else if(question.contains("병원 지도")){
                loadChild("리스트에 있는 병원을 클릭해보세요!\n아무 것도 누르지 않으면 지도에 아무 것도 뜨지 않습니다.");
            }else if(question.contains("코스리스트에서")){
                loadChild("세부 사항이 제공되지 않는 장소입니다.\n정보가 부족한 장소는 세부사항이 제공되지 않습니다.");
            }else if(question.contains("개발자가")){
                loadChild("충남대학교 컴퓨터공학과에 재학중인 전혜민, 최소영, 허아정 학생입니다.\n 셋은 친구랍니다.");
            }else if(question.contains("관광지에")){
                loadChild("관광지 자체에서 제공되지 않는 사진입니다.\n제공되지 않는 사진은 'No Image'라고 뜨는게 정상입니다.");
            }else if(question.contains("제 아이디")){
                loadChild("로그인 타입(카카오톡, 네이버, 페이스북)을 확인해보세요!\n작성된 글과 현재 로그인한 타입이 일치하지 않으면 삭제가 되지 않습니다.");
            }
            mChildList.put(question,mChildListContent);

        }
        setGroupIndicatorToRight();
        mListView.setAdapter(new BaseExpandableAdapter(this, mGroupList, mChildList));
        /*
        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), "g click = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 차일드 클릭 했을 경우 이벤트
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), "c click = " + childPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
*/
        // 그룹이 닫힐 경우 이벤트
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });
        /*
        // 그룹이 열릴 경우 이벤트
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /*
     * Layout
     */
    private ExpandableListView mListView;
    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        mListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }
    private void setLayout(){
        mListView = (ExpandableListView) findViewById(R.id.FAQ_list);
    }
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
    private void loadChild(String ask) {
        mChildListContent = new ArrayList<String>();
            mChildListContent.add(ask);
    }
}

