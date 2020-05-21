package com.example.barrierfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<CustomListView> listCustom = new ArrayList<CustomListView>();

    public CustomAdapter(){

    }

    @Override
    public int getCount() {
        return listCustom.size();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        TextView list_title = (TextView) convertView.findViewById(R.id.textTitle);
        TextView list_content = (TextView) convertView.findViewById(R.id.textContents);
        TextView list_date = (TextView) convertView.findViewById(R.id.textDate);

        CustomListView listViewItem = listCustom.get(position);

        list_title.setText(listViewItem.getTitle());
        list_content.setText(listViewItem.getContent());
        list_date.setText(listViewItem.getDate());

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

    public void addItem(String title, String content, String date) {
        CustomListView item = new CustomListView();

        item.setTitle(title);
        item.setContent(content);
        item.setDate(date);

        listCustom.add(item);

    }
}
