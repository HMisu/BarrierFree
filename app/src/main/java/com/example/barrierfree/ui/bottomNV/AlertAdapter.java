package com.example.barrierfree.ui.bottomNV;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barrierfree.R;
import com.example.barrierfree.models.ListViewMember;
import com.example.barrierfree.ui.member.MemberConnectFragment;

import java.util.ArrayList;
import java.util.Date;

public class AlertAdapter extends BaseAdapter {

    private AlertListView alert = new AlertListView();
    BottomAlert fragment;
    LayoutInflater inflater = null;
    private ArrayList<AlertListView> listCustom = new ArrayList<AlertListView>();


    public AlertAdapter(){
    }

    public AlertAdapter(Context context, BottomAlert fragment) {
        super();
        context = context;
        listCustom = new ArrayList<AlertListView>();
        this.fragment = fragment;

    }

    @Override
    public int getCount() {
        return listCustom.size();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null)
        {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_list, null);
        }

        TextView list_title = (TextView) convertView.findViewById(R.id.textTitle);
        TextView list_content = (TextView) convertView.findViewById(R.id.textContents);
        TextView list_date = (TextView) convertView.findViewById(R.id.textDate);
        ImageView list_image = (ImageView) convertView.findViewById(R.id.alertImage);


        alert = listCustom.get(position);

        list_title.setText(alert.getTitle());
        list_content.setText(alert.getContent());
        list_date.setText(alert.getDate());

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return listCustom.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(int image, String title, String content, String date) {
        AlertListView  item  = new AlertListView();

        item.setImage(image);
        item.setTitle(title);
        item.setContent(content);
        item.setDate(date);

        listCustom.add(item);
    }
}
