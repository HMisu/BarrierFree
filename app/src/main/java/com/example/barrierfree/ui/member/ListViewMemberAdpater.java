package com.example.barrierfree.ui.member;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.barrierfree.R;
import com.example.barrierfree.RoundImageView;
import com.example.barrierfree.models.Connection;
import com.example.barrierfree.models.ListViewMember;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ListViewMemberAdpater extends BaseAdapter implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private ListViewMember member;
    private Connection conn;
    private Context context;
    Bitmap bitmap;

    // ListView 내부 View들을 가르킬 변수들
    RoundImageView roundImageView;
    private TextView name;
    private TextView email;
    private Button btnrefuse, btnapply;

    private String uid;

    // 리스트 아이템 데이터를 저장할 배열
    private ArrayList<ListViewMember> memData;

    public ListViewMemberAdpater(){

    }

    public ListViewMemberAdpater(Context context) {
        super();
        context = context;
        memData = new ArrayList<ListViewMember>();
    }

    @Override
    // @return 아이템의 총 개수를 반환
    public int getCount() {
        // TODO Auto-generated method stub
        return memData.size();
    }

    @Override
    // @return 선택된 아이템을 반환
    public ListViewMember getItem(int position) {
        // TODO Auto-generated method stub
        return memData.get(position);
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
        db = FirebaseFirestore.getInstance();

        // 리스트 아이템이 새로 추가될 경우에는 v가 null값이다.
        // view는 어느 정도 생성된 뒤에는 재사용이 일어나기 때문에 효율을 위해서 해준다.
        if (convertView == null) {
            // inflater를 이용하여 사용할 레이아웃을 가져옵니다.
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_member, null);
        }

        roundImageView = (RoundImageView) convertView.findViewById(R.id.mem_photo);
        name = (TextView) convertView.findViewById(R.id.txt_mem_name);
        email = (TextView) convertView.findViewById(R.id.txt_mem_email);
        btnrefuse = (Button) convertView.findViewById(R.id.btn_refuse);
        btnapply = (Button) convertView.findViewById(R.id.btn_apply);

        // 받아온 position 값을 이용하여 배열에서 아이템을 가져온다.
        member = getItem(position);

        // Tag를 이용하여 데이터와 뷰를 묶습니다.
        btnrefuse.setTag(member);
        btnapply.setTag(member);

        if (member != null) {
            if (member.getMem_photo() != null) {
                //사진 적용
            }
            name.setText(member.getMem_name());
            email.setText(member.getMem_email());
            switch (member.getLv_id()){
                case "adpsearch":
                    btnrefuse.setVisibility(convertView.GONE);
                    btnapply.setText("신청");
                    break;
                case "adpapply":
                    btnrefuse.setText("취소");
                    btnapply.setText("수락대기");
                    break;
                case "adarequest":
                    btnrefuse.setText("거절");
                    btnapply.setText("수락");
                    break;
                default:
                    break;
            }
            btnrefuse.setOnClickListener(this);
            btnapply.setOnClickListener(this);
        }

        return convertView;
    }

    public void add(ListViewMember member){
        memData.add(member);
        //String m1 = member.getMem_name();
        //Log.d("메시지", String.valueOf(memData.size()));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        ListViewMember clickItem = (ListViewMember) v.getTag();
        switch (v.getId()) {
            case R.id.btn_refuse:
                Toast.makeText(context, "버튼 클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_apply:
                if(member.getLv_id().equals("adpsearch")){
                    PopupMenu p = new PopupMenu(context, v);
                    p.getMenuInflater().inflate(R.menu.popup_connect, p.getMenu());
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.weak:
                                    Log.d("메시지", "weak");
                                    conn = new Connection(member.getMem_uid(), user.getUid(), user.getUid(), false);
                                    break;
                                case R.id.protect:
                                    Log.d("메시지","protect");
                                    conn = new Connection(user.getUid(), member.getMem_uid(), user.getUid(), false);
                                    break;
                            }
                            db.collection("connection").document()
                                    .set(conn)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("메시지", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("메시지", "Error writing document", e);
                                        }
                                    });
                            return true;
                        }
                    });
                    p.show();
                }
                break;
            default:
                break;
        }
    }
}