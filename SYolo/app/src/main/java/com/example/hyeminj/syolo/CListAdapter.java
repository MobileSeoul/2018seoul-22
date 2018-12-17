package com.example.hyeminj.syolo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class CListAdapter extends BaseAdapter {
    Context context;
    Bitmap bitmap;
    ArrayList<course_item> list_itemArrayList;

    public CListAdapter(Context context, ArrayList<course_item> list_itemArrayList) {
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
        int position_add = position + 1;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_course_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.title_textView = (TextView) convertView.findViewById(R.id.course_title);
            holder.content_textView = (TextView) convertView.findViewById(R.id.textView_content);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView_image);

            convertView.setTag(holder);
        }
        if (list_itemArrayList.get(position) != null) {
            ViewHolder holder1 = (ViewHolder) convertView.getTag();

            holder1.title_textView.setText("코스 " + position_add + " : " + list_itemArrayList.get(position).getTitle());
            try {
                holder1.content_textView.setText(removeTag(list_itemArrayList.get(position).getContent()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            AQuery aq = new AQuery(convertView);
            String imageUrl = list_itemArrayList.get(position).getImage();

            aq.id(holder1.image).progress(this).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {
                @Override
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    iv.setImageBitmap(bm);
                }
            });
        }
        return convertView;
    }

    static class ViewHolder {
        TextView title_textView;
        TextView content_textView;
        ImageView image;
    }

    public String removeTag(String html) throws Exception {
        // Remove HTML tag from java String
        String noHTMLString = html.replaceAll("\\<.*?\\>", "");

// Remove Carriage return from java String
        noHTMLString = noHTMLString.replaceAll("\r", "<br/>");
        noHTMLString = noHTMLString.replaceAll("<([bip])>.*?</\1>", "");
// Remove New line from java string and replace html break
        noHTMLString = noHTMLString.replaceAll("\n", " ");
        noHTMLString = noHTMLString.replaceAll("\"", "&quot;");
        noHTMLString = noHTMLString.replaceAll("<(.*?)\\>", " ");//Removes all items in brackets
        noHTMLString = noHTMLString.replaceAll("<(.*?)\\\n", " ");//Must be undeneath
        noHTMLString = noHTMLString.replaceFirst("(.*?)\\>", " ");
        noHTMLString = noHTMLString.replaceAll("&nbsp;", " ");
        noHTMLString = noHTMLString.replaceAll("&amp;", " ");
        return noHTMLString;

    }
}

