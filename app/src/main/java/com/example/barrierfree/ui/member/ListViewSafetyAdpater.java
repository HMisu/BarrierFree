package com.example.barrierfree.ui.member;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.barrierfree.R;
import com.example.barrierfree.models.Safety;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListViewSafetyAdpater extends BaseAdapter implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private Safety safety = new Safety();
    private Context context;

    private ImageView imageView;
    private TextView name, addr, register;
    private Button btnreg;

    private double latitude, longitude;
    private String weak_uid, register_uid, sf_id, kind;

    private ArrayList<Safety> sfData = null;
    SafetyFragment fragment;
    Safety clickItem;

    public ListViewSafetyAdpater() {

    }

    public ListViewSafetyAdpater(Context context, SafetyFragment fragment) {
        super();
        context = context;
        sfData = new ArrayList<Safety>();
        this.fragment = fragment;
    }

    @Override
    // @return 아이템의 총 개수를 반환
    public int getCount() {
        // TODO Auto-generated method stub
        return sfData.size();
    }

    @Override
    // @return 선택된 아이템을 반환
    public Safety getItem(int position) {
        // TODO Auto-generated method stub
        return sfData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        // 리스트 아이템이 새로 추가될 경우에는 v가 null값이다.
        // view는 어느 정도 생성된 뒤에는 재사용이 일어나기 때문에 효율을 위해서 해준다.
        if (convertView == null) {
            // inflater를 이용하여 사용할 레이아웃을 가져옵니다.
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_safety, null);
        }

        imageView = (ImageView) convertView.findViewById(R.id.img_kind);
        name = (TextView) convertView.findViewById(R.id.txt_name);
        addr = (TextView) convertView.findViewById(R.id.txt_addr);
        register = (TextView) convertView.findViewById(R.id.txt_register);
        btnreg = (Button) convertView.findViewById(R.id.btn_reg);
        // 받아온 position 값을 이용하여 배열에서 아이템을 가져온다.
        safety = getItem(position);

        // Tag를 이용하여 데이터와 뷰를 묶습니다.
        btnreg.setTag(safety);

        if(safety.getMem_register().equals(safety.getMem_weak())){
            register.setVisibility(convertView.GONE);
        }

        name.setText(safety.getName());
        addr.setText(safety.getAddr());
        weak_uid = safety.getMem_weak();
        register_uid = safety.getMem_register();
        latitude = safety.getLatitude();
        longitude = safety.getLongitude();
        kind = safety.getKind();
        sf_id = safety.getSf_id();
        Log.d("메시지", sf_id);

        switch (kind) {
            case "home":
                imageView.setImageResource(R.drawable.ic_kind_home);
                break;
            case "company":
                imageView.setImageResource(R.drawable.ic_kind_company);
                break;
            case "shcool":
                imageView.setImageResource(R.drawable.ic_kind_school);
                break;
            case "etc":
                imageView.setImageResource(R.drawable.ic_kind_etc);
                break;
            default:
                break;
        }

        btnreg.setOnClickListener(this);

        return convertView;
    }

    public void add(Safety safety) {
        sfData.add(safety);
    }

    public void clear() {
        sfData.clear();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        clickItem = (Safety) v.getTag();
        switch (v.getId()) {
            case R.id.btn_reg:
                Log.d("메시지", "22 : " +sf_id);
                Log.d("메시지", "22 : " +safety.getName());
                PopupMenu p = new PopupMenu(context, v);
                p.getMenuInflater().inflate(R.menu.popup_safety, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.update:
                                fragment.updateSafety(clickItem.getSf_id());
                                break;
                            case R.id.delete:
                                FirebaseFirestore.getInstance().collection("safety").document(clickItem.getSf_id()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("메시지", "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("메시지", "Error deleting document", e);
                                            }
                                        });
                                break;
                        }
                        return true;
                    }
                });
                p.show();
                break;
            default:
                break;
        }
    }

}