package com.example.hyeminj.syolo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ReviewAdapter extends BaseAdapter {
    Context context;
    ArrayList<review_item> list_itemArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference mDatabase;
    DatabaseReference mConditionRef;
    String remove_array[];
    String remove;
    SharedPreferences pref;
    String loginType;
    String ID;
    String name;
    String content;
    String id;
    String date;
    String rating;
    long size;

    public ReviewAdapter(Context context, ArrayList<review_item> list_itemArrayList) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
        pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        ID=pref.getString("id","");
        name=pref.getString("name","");
    }

    @Override
    public int getCount() {
        return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mConditionRef = mDatabase.child("review").child(list_itemArrayList.get(position).getType()).child(list_itemArrayList.get(position).getAddr());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                size = dataSnapshot.getChildrenCount();
                remove_array = new String[(int)size];
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        remove_array[i] = snapshot.getKey();
                        i++;
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_review_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.review_id);
            holder.date = (TextView) convertView.findViewById(R.id.review_date);
            holder.content = (TextView)convertView.findViewById(R.id.review_content);
            holder.ratingBar = (RatingBar)convertView.findViewById(R.id.ratingbar);
            holder.trash = (ImageView)convertView.findViewById(R.id.trash);
            holder.login_type= (ImageView)convertView.findViewById(R.id.login_type_chk);

            convertView.setTag(holder);
        }
        if(list_itemArrayList.get(position) != null){
            final ViewHolder holder1 = (ViewHolder)convertView.getTag();

            if(list_itemArrayList.get(position).getLogin_type().equals("kakao")){
                holder1.login_type.setImageResource(R.drawable.kakao_chk);
            }else if(list_itemArrayList.get(position).getLogin_type().equals("facebook")){
                holder1.login_type.setImageResource(R.drawable.facebook_chk);
            }else if(list_itemArrayList.get(position).getLogin_type().equals("naver")){
                holder1.login_type.setImageResource(R.drawable.naver_chk);
            }

            holder1.id.setText(list_itemArrayList.get(position).getId());
            id=list_itemArrayList.get(position).getId();
            holder1.date.setText(list_itemArrayList.get(position).getDate());
            date = list_itemArrayList.get(position).getDate();
            holder1.content.setText(list_itemArrayList.get(position).getContent());

            holder1.ratingBar.setRating(list_itemArrayList.get(position).getRating());
            rating =String.valueOf(list_itemArrayList.get(position).getRating());

            holder1.trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder (parent.getContext());
                    alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ListView review_list = (ListView) parent.findViewById(R.id.review_list);
                                    ReviewAdapter adapter = new ReviewAdapter(parent.getContext(),list_itemArrayList);
                                    if(list_itemArrayList.get(position).getReal_id().equals(ID)){
                                        content = list_itemArrayList.get(position).getContent();
                                        remove = remove_array[(int)size-position-1];
                                        mConditionRef.child(remove).removeValue();
                                        DatabaseReference myreview = FirebaseDatabase.getInstance().getReference().child("member").child(loginType).child(ID).child("review").child(list_itemArrayList.get(position).getType()).child(list_itemArrayList.get(position).getAddr());
                                        myreview.child(remove).removeValue();

                                        int count;
                                        count = adapter.getCount();
                                        if(count>0){

                                            if (position > -1 && position < count) {
                                                list_itemArrayList.remove(size-(position+1));
                                                adapter = new ReviewAdapter(parent.getContext(),list_itemArrayList);
                                                review_list.setAdapter(adapter);
                                            }
                                        }if(count==1){
                                            String temp;
                                            if(list_itemArrayList.get(position).getType().equals("Lodge")){
                                                temp = "lodge";
                                            }else if(list_itemArrayList.get(position).getType().equals("place")){
                                                temp = "place";
                                            }else if(list_itemArrayList.get(position).getType().equals("food")){
                                                temp = "food";
                                            }else{
                                                temp = "etc";
                                            }
                                            list_itemArrayList.clear();
                                            adapter = new ReviewAdapter(parent.getContext(),list_itemArrayList);
                                            review_list.setAdapter(adapter);
                                            if(temp.equals("lodge")){
                                                ((lodge_detail)context).onPause();
                                            }else if(temp.equals("place")){
                                                ((place_detail)context).onPause();
                                            }else if(temp.equals("food")){
                                                ((food_detail)context).onPause();
                                            }else if(temp.equals("etc")){
                                                ((detail)context).onPause();
                                            }

                                        }

                                    }else{
                                        Toast.makeText(context,"접근 권한이 없습니다.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }

            });

        }

        return convertView;
    }

    static class ViewHolder{
        TextView id;
        TextView date;
        TextView content;
        RatingBar ratingBar;
        ImageView trash;
        ImageView login_type;
    }
}
