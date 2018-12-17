package com.example.hyeminj.syolo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BoardAdapter extends BaseAdapter{
    Context context;
    ArrayList<board_item> list_itemArrayList;
    DatabaseReference mDatabase;
    DatabaseReference mConditionRef;
    String remove_array[];
    String remove;
    SharedPreferences pref;
    String loginType;
    String ID;
    String name;
    long size;

    public BoardAdapter(Context context, ArrayList<board_item> list_itemArrayList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mConditionRef = mDatabase.child("board");
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
            convertView = inflater.inflate(R.layout.board_list, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.textView_id);
            holder.date = (TextView) convertView.findViewById(R.id.textView_date);
          //  holder.content = (TextView)convertView.findViewById(R.id.review_content);
            holder.title = (TextView) convertView.findViewById(R.id.textView_title);

            convertView.setTag(holder);
        }
        if(list_itemArrayList.get(position) != null){
            ViewHolder holder1 = (ViewHolder)convertView.getTag();

            holder1.id.setText(list_itemArrayList.get(position).getId());
            holder1.date.setText(list_itemArrayList.get(position).getDate());
            holder1.title.setText(list_itemArrayList.get(position).getTitle());

        }

        return convertView;
    }

    static class ViewHolder{
        TextView id;
        TextView date;
        TextView content;
        TextView title;
    }
}
