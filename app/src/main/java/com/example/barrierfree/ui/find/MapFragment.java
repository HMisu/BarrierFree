package com.example.barrierfree.ui.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.barrierfree.R;
import com.skt.Tmap.TMapView;

public class MapFragment extends Fragment {

    private FindViewModel findViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        findViewModel =
//                ViewModelProviders.of(this).get(FindViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery2, container, false);
        LinearLayout linearLayoutTmap = (LinearLayout)root.findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(getActivity());

        tMapView.setSKTMapApiKey("l7xx0bfddf7c9eaa4cc48ff35b5f8b73ce9d");

        linearLayoutTmap.addView( tMapView );
        return root;
    }
}
