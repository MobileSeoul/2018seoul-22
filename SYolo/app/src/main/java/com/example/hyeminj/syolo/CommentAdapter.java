package com.example.hyeminj.syolo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CommentAdapter extends BaseAdapter{
    Context context;
    ArrayList<comment_item> list_itemArrayList;
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


    public CommentAdapter(Context context, ArrayList<comment_item> list_itemArrayList) {
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
        mConditionRef = mDatabase.child("board").child(list_itemArrayList.get(position).data_key_now).child("comment");
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
            convertView = inflater.inflate(R.layout.activity_comment_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.comment_id);
            holder.date = (TextView) convertView.findViewById(R.id.comment_date);
            //  holder.content = (TextView)convertView.findViewById(R.id.review_content);
            holder.content = (TextView) convertView.findViewById(R.id.comment_content);
            holder.trash = (ImageView)convertView.findViewById(R.id.trash);
            holder.comment_login_chk = convertView.findViewById(R.id.cmt_login_type_chk);

            convertView.setTag(holder);
        }
        if(list_itemArrayList.get(position) != null){
            ViewHolder holder1 = (ViewHolder)convertView.getTag();

            holder1.id.setText(list_itemArrayList.get(position).getId());
            holder1.date.setText(list_itemArrayList.get(position).getDate());
            holder1.content.setText(list_itemArrayList.get(position).getContent());

            switch (list_itemArrayList.get(position).getLogin_type()){
                case "kakao":
                    holder1.comment_login_chk.setImageResource(R.drawable.kakao_chk);
                    break;
                case "facebook":
                    holder1.comment_login_chk.setImageResource(R.drawable.facebook_chk);
                    break;
                case "naver":
                    holder1.comment_login_chk.setImageResource(R.drawable.naver_chk);
                    break;
            }
            holder1.trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(parent.getContext());
                    alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ListView comment_listview = (ListView) parent.findViewById(R.id.comment_listview);
                                    CommentAdapter adapter = new CommentAdapter(parent.getContext(),list_itemArrayList);
                                    if(list_itemArrayList.get(position).getReal_id().equals(ID)){
                                        content = list_itemArrayList.get(position).getContent();
                                        remove = remove_array[position];
                                        mConditionRef.child(remove).removeValue();
                                        int count;
                                        count = adapter.getCount();
                                        if(count>0){

                                            if (position > -1 && position < count) {
                                                list_itemArrayList.remove(position);
                                                adapter = new CommentAdapter(parent.getContext(),list_itemArrayList);
                                                comment_listview.setAdapter(adapter);
                                            }
                                        }if(count==1){
                                            list_itemArrayList.clear();
                                            adapter = new CommentAdapter(parent.getContext(),list_itemArrayList);
                                            comment_listview.setAdapter(adapter);
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
        ImageView comment_login_chk;
        ImageView trash;
    }
}