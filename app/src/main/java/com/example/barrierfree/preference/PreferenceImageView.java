package com.example.barrierfree.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.example.barrierfree.R;

public class PreferenceImageView extends Preference {
    private ImageView imageView;
    View.OnClickListener imageClickListener;

    public PreferenceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        imageView = (ImageView)holder.findViewById(R.id.user_photo);
        imageView.setOnClickListener(imageClickListener);
    }

    public void setImageClickListener(View.OnClickListener onClickListener)
    {
        imageClickListener = onClickListener;
    }
}

//https://stackoverflow.com/questions/59507248/how-to-add-image-view-that-can-be-edited-inside-a-preferencescreen-using-prefere