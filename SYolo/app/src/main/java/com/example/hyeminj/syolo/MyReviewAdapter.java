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
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyReviewAdapter extends BaseAdapter{
    Context context;
    List<HashMap<String, String>> reviewList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    SharedPreferences pref;
    String loginType;
    String ID;
    String name;

    public MyReviewAdapter(Context context, List<HashMap<String, String>> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        loginType=pref.getString("login","");
        ID=pref.getString("id","");
        name=pref.getString("name","");
    }

    @Override
    public int getCount() {
        return this.reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_myreview_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.name = (TextView) convertView.findViewById(R.id.review_id);
            holder.rating = (RatingBar) convertView.findViewById(R.id.ratingbar);
            holder.date = (TextView) convertView.findViewById(R.id.review_date);
            holder.content = (TextView) convertView.findViewById(R.id.review_content);
            holder.trash = (ImageView)convertView.findViewById(R.id.trash);

            convertView.setTag(holder);
        }
        if(reviewList.get(position) != null){
            ViewHolder holder = (ViewHolder)convertView.getTag();

            holder.title.setText(reviewList.get(position).get("title"));
            holder.name.setText(reviewList.get(position).get("name"));
            holder.rating.setRating(Float.parseFloat(reviewList.get(position).get("rating")));
            holder.date.setText(reviewList.get(position).get("date"));
            holder.content.setText(reviewList.get(position).get("content"));

            holder.trash.setOnClickListener(new View.OnClickListener() {
                /**
                 * Called when a view has been clicked.
                 *
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
                    alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String addr = reviewList.get(position).get("addr");
                                    String key = reviewList.get(position).get("key");
                                    String type = reviewList.get(position).get("type");
                                    databaseReference.child("member").child(loginType).child(ID).child("review").child(type).child(addr).child(key).removeValue();
                                    databaseReference.child("review").child(type).child(addr).child(key).removeValue();
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
        TextView title;
        TextView name;
        RatingBar rating;
        TextView date;
        TextView content;
        ImageView trash;
    }
}