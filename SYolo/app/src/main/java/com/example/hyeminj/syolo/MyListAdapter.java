package com.example.hyeminj.syolo;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter{
    Context context;
    Bitmap bitmap;
    ArrayList<image_item> list_itemArrayList;

    public MyListAdapter(Context context, ArrayList<image_item> list_itemArrayList) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_image_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.title_textView = (TextView) convertView.findViewById(R.id.textView_title);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView_image);

            convertView.setTag(holder);
        }
            if(list_itemArrayList.get(position) != null) {
                ViewHolder holder1 = (ViewHolder) convertView.getTag();

                holder1.title_textView.setText(list_itemArrayList.get(position).getTitle());

                AQuery aq = new AQuery(convertView);
                String imageUrl = list_itemArrayList.get(position).getImage();

                aq.id(holder1.image).progress(this).image(imageUrl,true,true,0,0,new BitmapAjaxCallback(){
                   @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
                       iv.setImageBitmap(bm);
                   }
                });


            }
        return convertView;
    }

    static class ViewHolder{
        TextView title_textView;
        ImageView image;
    }
}
