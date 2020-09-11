package com.example.barrierfree.ui.bottomNV;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.barrierfree.CustomAdapter;
import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class BottomAlert extends ListFragment {

    ViewGroup viewGroup;
    ListView listview;
    private ListView item_list = null;
    CustomAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.app_bar_alert, container, false);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Alert Log", "getInstanceId Failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("Alert Log", "알람 토큰 : " + token);
                        Context context;
                        context = container.getContext();
                        Toast.makeText(context, token, Toast.LENGTH_SHORT).show();
                    }
                });
        adapter = new CustomAdapter();
        setListAdapter(adapter);

        adapter.addItem("제목 2", "내용 2", "20-05-21");

        adapter.addItem("제목 1", "내용 1", "20-05-03");


        return super.onCreateView(inflater, container, savedInstanceState);


    }
}