package com.example.barrierfree.ui.bottomNV;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.barrierfree.R;

public class BottomNVTest2 extends Fragment{
    ViewGroup viewGroup;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.app_bar_test2,container,false);
        return viewGroup;
    }
}