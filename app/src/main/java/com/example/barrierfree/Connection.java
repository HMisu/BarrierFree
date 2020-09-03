package com.example.barrierfree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Connection extends Fragment {
    private ListView connect_list, wait_list;
    ViewGroup viewGroup;
    ConnectAdapter connect_adapter;
    WaitAdapter wait_adapter;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.connection,container,false);

        connect_adapter = new ConnectAdapter();
        wait_adapter = new WaitAdapter();

        connect_list = (ListView) viewGroup.findViewById(R.id.connect_list);
        wait_list = (ListView) viewGroup.findViewById(R.id.waitt_list);

        connect_list.setAdapter(connect_adapter);
        wait_list.setAdapter(wait_adapter);

        connect_adapter.addItem("사용자이름","010-XXXX-XXXX");
        connect_adapter.addItem("사용자이름","010-XXXX-XXXX");
        connect_adapter.addItem("사용자이름","010-XXXX-XXXX");

        wait_adapter.addItem("사용자이름","010-XXXX-XXXX");
        wait_adapter.addItem("사용자이름","010-XXXX-XXXX");
        wait_adapter.addItem("사용자이름","010-XXXX-XXXX");


        return viewGroup;
    }
}