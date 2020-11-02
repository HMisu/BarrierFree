package com.example.barrierfree.ui.bottomNV;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.barrierfree.R;
import com.example.barrierfree.models.ListViewMember;
import com.example.barrierfree.ui.member.MemberConnectFragment;

import java.util.ArrayList;

public class AlertAdapter extends BaseAdapter {

    private AlertListView alert = new AlertListView();
    BottomAlert bottomAlert;
    LayoutInflater inflater = null;
    private ArrayList<AlertListView> listCustom = null;

    public AlertAdapter(){
    }

    public AlertAdapter(Context context, BottomAlert bottomAlert) {
        super();
        context = context;
        listCustom = new ArrayList<AlertListView>();
        this.bottomAlert = bottomAlert;

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
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_member, null);
        }
        alert = (AlertListView) getItem(position);

        TextView list_title = (TextView) convertView.findViewById(R.id.textTitle);
        TextView list_content = (TextView) convertView.findViewById(R.id.textContents);
        TextView list_date = (TextView) convertView.findViewById(R.id.textDate);

        AlertListView listViewItem = listCustom.get(position);

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
        AlertListView item = new AlertListView();

        item.setTitle(title);
        item.setContent(content);
        item.setDate(date);

        listCustom.add(item);

    }
}
