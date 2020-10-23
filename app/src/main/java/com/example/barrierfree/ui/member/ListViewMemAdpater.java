package com.example.barrierfree.ui.member;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.barrierfree.R;
import com.example.barrierfree.models.Connection;
import com.example.barrierfree.models.ListViewMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class ListViewMemAdpater extends BaseAdapter implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private ListViewMember member = new ListViewMember();
    private Connection conn;
    private Context context;

    Bitmap bitmap;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    private ImageView imageView;
    private TextView name, email, apply;
    private Button btnrefuse, btnapply;

    private boolean bool1 = false;
    private String mem_uid, cn_id, lv_id, b;

    private ArrayList<ListViewMember> memData = null;
    MemberConnectFragment fragment;
    ListViewMember clickItem;

    public ListViewMemAdpater() {

    }

    public ListViewMemAdpater(Context context, MemberConnectFragment fragment) {
        super();
        context = context;
        memData = new ArrayList<ListViewMember>();
        this.fragment = fragment;
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
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        initImageLoader();

        // 리스트 아이템이 새로 추가될 경우에는 v가 null값이다.
        // view는 어느 정도 생성된 뒤에는 재사용이 일어나기 때문에 효율을 위해서 해준다.
        if (convertView == null) {
            // inflater를 이용하여 사용할 레이아웃을 가져옵니다.
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_member, null);
        }

        imageView = (ImageView) convertView.findViewById(R.id.mem_photo);
        name = (TextView) convertView.findViewById(R.id.txt_mem_name);
        email = (TextView) convertView.findViewById(R.id.txt_mem_email);
        apply = (TextView) convertView.findViewById(R.id.txt_apply);
        btnrefuse = (Button) convertView.findViewById(R.id.btn_refuse);
        btnapply = (Button) convertView.findViewById(R.id.btn_apply);
        // 받아온 position 값을 이용하여 배열에서 아이템을 가져온다.
        member = getItem(position);

        // Tag를 이용하여 데이터와 뷰를 묶습니다.
        btnrefuse.setTag(member);
        btnapply.setTag(member);

        if (member.getMem_photo() == null || member.getMem_photo().equals("")) {
            imageView.setImageResource(R.drawable.ic_defaultuser);
        } else {
            imageLoader.displayImage(member.getMem_photo(), imageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            // 이미지를 비운다 (로드 실패할 경우)
                            imageView.setImageDrawable(null);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    }
            );
        }
        mem_uid = member.getMem_uid();
        cn_id = member.getCn_id();
        lv_id = member.getLv_id();
        name.setText(member.getMem_name());
        Log.d("메시지", member.getMem_name());
        email.setText(member.getMem_email());

        if (member.getMem_applicant() != null) {
            if (member.getLv_id().equals("adpapply")) {
                if (member.getMem_applicant().equals("weak"))
                    apply.setText(member.getMem_name() + " : 보호자 / " + user.getDisplayName() + " : 취약자");
                else
                    apply.setText(member.getMem_name() + " : 취약자 / " + user.getDisplayName() + " : 보호자");
            } else {
                if (member.getMem_applicant().equals("weak"))
                    apply.setText(member.getMem_name() + " : 취약자 / " + user.getDisplayName() + " : 보호자");
                else
                    apply.setText(member.getMem_name() + " : 보호자 / " + user.getDisplayName() + " : 취약자");
            }
        }

        switch (member.getLv_id()) {
            case "adpsearch":
                btnrefuse.setVisibility(convertView.GONE);
                apply.setVisibility(convertView.GONE);
                db.collection("connection").whereEqualTo("mem_applicant", user.getUid()).whereEqualTo("connect", false).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getString("mem_weak").equals(member.getMem_uid()) || document.getString("mem_protect").equals(member.getMem_uid())) {
                                            btnrefuse.setText("요청취소");
                                        }
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                    return;
                                }
                            }
                        });
                btnapply.setText("신청");
                break;
            case "adpapply":
                btnrefuse.setText("요청취소");
                btnapply.setText("수락대기중");
                btnapply.setBackgroundColor(Color.parseColor("#f2f2f2"));
                btnapply.setEnabled(false);
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

        return convertView;
    }

    public void add(ListViewMember member) {
        memData.add(member);
    }

    public void clear() {
        memData.clear();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        clickItem = (ListViewMember) v.getTag();
        switch (v.getId()) {
            case R.id.btn_refuse:
                Log.d("메시지", clickItem.getCn_id());
                Log.d("메시지", clickItem.getLv_id());
                if (clickItem.getLv_id().equals("adpapply") || clickItem.getLv_id().equals("adprequest")) {
                    db.collection("connection").document(clickItem.getCn_id()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("메시지", "DocumentSnapshot successfully deleted!");
                                    fragment.refreshFragment();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("메시지", "Error deleting document", e);
                                }
                            });
                }
                break;
            case R.id.btn_apply:
                if (clickItem.getLv_id().equals("adpsearch")) {
                    PopupMenu p = new PopupMenu(context, v);
                    p.getMenuInflater().inflate(R.menu.popup_connect, p.getMenu());
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.weak:
                                    conn = new Connection(clickItem.getMem_uid(), user.getUid(), user.getUid(), clickItem.getMem_uid(), false);
                                    b = "weak";
                                    break;
                                case R.id.protect:
                                    conn = new Connection(user.getUid(), clickItem.getMem_uid(), user.getUid(), clickItem.getMem_uid(), false);
                                    b = "protect";
                                    break;
                                default:
                                    break;
                            }
                            final String c = b;
                            Log.d("메시지", b);
                            db.collection("connection").whereEqualTo("mem_applicant", user.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    Toast.makeText(context, "이미 계정연결을 신청한 상태입니다", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    db.collection("connection").whereEqualTo("mem_applicant", clickItem.getMem_uid()).whereEqualTo("mem_respondent", user.getUid()).whereEqualTo("connect", false).get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(Task<QuerySnapshot> task) {
                                                                    QuerySnapshot querySnapshot = task.getResult();
                                                                    if (querySnapshot.isEmpty()) {
                                                                        db.collection("connection").document()
                                                                                .set(conn)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Log.d("메시지", "DocumentSnapshot successfully written!");
                                                                                        fragment.refreshFragment();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.w("메시지", "Error writing document", e);
                                                                                    }
                                                                                });
                                                                    }
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            Boolean bool1 = false;
                                                                            if (document.getString("mem_weak").equals(user.getUid()) && !c.equals("weak")) {
                                                                                Toast.makeText(context, "이미 상대방이 본인을 보호자, 당신을 취약자로 신청했습니다. 대기 목록을 확인하세요", Toast.LENGTH_SHORT).show();
                                                                            } else if (document.getString("mem_protect").equals(user.getUid()) && !c.equals("protect")) {
                                                                                Toast.makeText(context, "이미 상대방이 본인을 취약자, 당신을 보호자로 신청했습니다. 대기 목록을 확인하세요", Toast.LENGTH_SHORT).show();
                                                                            } else if((document.getString("mem_weak").equals(user.getUid()) && c.equals("weak"))||(document.getString("mem_protect").equals(user.getUid()) && c.equals("protect"))){
                                                                                bool1 = true;
                                                                                Toast.makeText(context, "이미 상대방이 같은 관계로 신청해 자동 연결되었습니다", Toast.LENGTH_SHORT).show();
                                                                                db.collection("connection").document(document.getId())
                                                                                        .update("connect", true)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Log.d("메시지", "DocumentSnapshot successfully updated!");
                                                                                                fragment.refreshFragment();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Log.w("메시지", "Error updating document", e);
                                                                                            }
                                                                                        });
                                                                            }

                                                                            if(bool1 == false){
                                                                                db.collection("connection").document()
                                                                                        .set(conn)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Log.d("메시지", "DocumentSnapshot successfully written!");
                                                                                                fragment.refreshFragment();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Log.w("메시지", "Error writing document", e);
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Log.d("메시지", "Error getting documents: ", task.getException());
                                                                        return;
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d("메시지", "Error getting documents: ", task.getException());
                                                return;
                                            }
                                        }
                                    });
                            return true;
                        }
                    });
                    p.show();
                } else if (lv_id.equals("adprequest")) {
                    if (clickItem.getCn_id() == null)
                        return;
                    db.collection("connection").whereEqualTo("connect", true).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.getString("mem_weak").equals(user.getUid()) || document.getString("mem_protect").equals(user.getUid())) {
                                                Log.d("메시지", "존재");
                                                Toast.makeText(context, "이미 다른 계정과 연결되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                bool1 = true;
                                                return;
                                            } else if (document.getString("mem_weak").equals(clickItem.getMem_uid()) || document.getString("mem_protect").equals(clickItem.getMem_uid())) {
                                                Log.d("메시지", "존재");
                                                Toast.makeText(context, "이미 상대방은 다른 계정과 연결되어 있습니다.", Toast.LENGTH_SHORT).show();
                                                bool1 = true;
                                                return;
                                            }
                                        }
                                        if (bool1 == false) {
                                            db.collection("connection").document(clickItem.getCn_id())
                                                    .update("connect", true)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("메시지", "DocumentSnapshot successfully updated!");
                                                            db.collection("connection").whereEqualTo("connect", false).get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                    if ((document.getString("mem_weak").equals(user.getUid()) && document.getString("mem_protect").equals(clickItem.getMem_uid())) || (document.getString("mem_weak").equals(clickItem.getMem_uid()) && document.getString("mem_protect").equals(user.getUid()))) {
                                                                                        db.collection("connection").document(document.getId()).delete()
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
                                                                                    }
                                                                                }
                                                                                fragment.refreshFragment();
                                                                            } else {
                                                                                Log.d("메시지", "Error getting documents: ", task.getException());
                                                                                return;
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("메시지", "Error updating document", e);
                                                        }
                                                    });
                                            fragment.refreshFragment();
                                        }
                                    } else {
                                        Log.d("메시지", "Error getting documents: ", task.getException());
                                        return;
                                    }
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    public void initImageLoader() {
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        // 보통 ARGB8888이나 RGB565 쓰면 된다

        imageLoader = ImageLoader.getInstance();    // imageLoader는 싱글톤이라 getInstance()해야함
        // configuer 만들어 줘야함: configure 구성
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).build();
        // => ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        imageLoader.init(configuration);

    }
}