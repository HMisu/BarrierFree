package com.example.barrierfree.ui.bottomNV;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.barrierfree.CustomAdapter;
import com.example.barrierfree.R;

public class BottomAlert extends ListFragment {

    ViewGroup viewGroup;
    ListView listview;
    private ListView item_list = null;
    CustomAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.app_bar_alert, container, false);

        adapter = new CustomAdapter();
        setListAdapter(adapter);

        adapter.addItem("제목 2", "내용 2", "20-05-21");

        adapter.addItem("제목 1", "내용 1", "20-05-03");

        return super.onCreateView(inflater, container, savedInstanceState);


    }
}