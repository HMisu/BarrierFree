package com.example.barrierfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WaitAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<WaitListView> listCustom = new ArrayList<WaitListView>();

    public WaitAdapter(){

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
            convertView = inflater.inflate(R.layout.waitt_list, parent, false);
        }

        TextView list_name = (TextView) convertView.findViewById(R.id.textName);
        TextView list_phone = (TextView) convertView.findViewById(R.id.textPhone);

        WaitListView listViewItem = listCustom.get(position);

        list_name.setText(listViewItem.getName());
        list_phone.setText(listViewItem.getPhone());

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

    public void addItem(String name, String phone) {
        WaitListView item = new WaitListView();

        item.setName(name);
        item.setPhone(phone);

        listCustom.add(item);

    }
}
