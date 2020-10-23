package com.example.barrierfree.ui.member;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.example.barrierfree.models.Safety;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SafetyEditFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private View root;

    private String addr, kind, name, mem_weak, mem_register;
    private Boolean rfgadd = false;
    private String sf_id;
    private double latitude, longitude;
    private Safety safety;

    Button btnedit;
    ImageButton imghome, imgcompany, imgschool, imgetc;
    TextView editname, editaddr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean("rfgAdd") == true) {
                addr = bundle.getString("rfgAddr");
                Log.d("메시지", "addr : " + addr);
                Log.d("메시지", "rfgAddr : " + bundle.getString("rfgAddr"));
                latitude = bundle.getDouble("rfglatitude");
                longitude = bundle.getDouble("rfglongitude");
                rfgadd = bundle.getBoolean("rfgAdd");
            } else {
                sf_id = bundle.getString("rfgId");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_safety_edit, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        mem_weak = user.getUid();

        btnedit = (Button) root.findViewById(R.id.btn_edit);

        editname = (TextView) root.findViewById(R.id.edit_name);
        editaddr = (TextView) root.findViewById(R.id.edit_addr);
        editaddr.setText(addr);
        editaddr.setFocusable(false);
        editaddr.setClickable(false);

        imghome = (ImageButton) root.findViewById(R.id.img_home);
        imgcompany = (ImageButton) root.findViewById(R.id.img_company);
        imgschool = (ImageButton) root.findViewById(R.id.img_shcool);
        imgetc = (ImageButton) root.findViewById(R.id.img_etc);

        kind="";

        if (rfgadd == false) {
            btnedit.setText("수정");

            db.collection("safety").document(sf_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    kind = document.getString("kind");
                                    editname.setText(document.getString("name"));
                                    editaddr.setText(document.getString("addr"));

                                    switch (kind) {
                                        case "home":
                                            changeImageHome();
                                            break;
                                        case "company":
                                            changeImageCompany();
                                            break;
                                        case "shcool":
                                            changeImageSchool();
                                            break;
                                        case "etc":
                                            changeImageEtc();
                                            break;
                                        default:
                                            break;
                                    }
                                } else {
                                    Log.d("메시지", "No such document");
                                }
                            } else {
                                Log.d("메시지", "get failed with ", task.getException());
                            }
                        }
                    });
        }
        db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_protect", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (task.isSuccessful()) {
                            if (querySnapshot.isEmpty()) {
                                mem_weak = user.getUid();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mem_weak = document.getString("mem_weak");
                            }
                        }
                    }
                });

        imghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageHome();
            }
        });
        imgcompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageCompany();
            }
        });
        imgschool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageSchool();
            }
        });
        imgetc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageEtc();
            }
        });

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rfgadd == true) {
                    if (kind.equals("") || kind == null || editname.getText().toString().equals("") || editname.getText().toString() == null) {
                        Toast.makeText(getActivity(), "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    safety = new Safety(kind, editname.getText().toString(), addr, latitude, longitude, mem_weak, user.getUid());
                    db.collection("safety").document()
                            .set(safety)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("메시지", "DocumentSnapshot successfully written!");
                                    Toast.makeText(getActivity(), "안심장소 등록에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                    String fragmentTag = new SafetyFragment().getClass().getSimpleName();
                                    Fragment fragmentClass = new SafetyFragment();
                                    ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("메시지", "Error writing document", e);
                                }
                            });
                } else {
                    db.collection("safety").document(sf_id)
                            .update("kind", kind, "name", editname.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("메시지", "DocumentSnapshot successfully updated!");
                                    Toast.makeText(getActivity(), "안심장소가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                    String fragmentTag = new SafetyFragment().getClass().getSimpleName();
                                    Fragment fragmentClass = new SafetyFragment();
                                    ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("메시지", "Error updating document", e);
                                }
                            });
                }
            }
        });

        return root;
    }

    void changeImageHome() {
        kind = "home";
        imghome.setImageResource(R.drawable.ic_kind_home_white);
        imghome.setBackground(getResources().getDrawable(R.drawable.btn_green_oval));

        imgcompany.setImageResource(R.drawable.ic_kind_company);
        imgcompany.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgschool.setImageResource(R.drawable.ic_kind_school);
        imgschool.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgetc.setImageResource(R.drawable.ic_kind_etc);
        imgetc.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
    }

    void changeImageCompany() {
        kind = "company";
        imgcompany.setImageResource(R.drawable.ic_kind_company_white);
        imgcompany.setBackground(getResources().getDrawable(R.drawable.btn_green_oval));

        imghome.setImageResource(R.drawable.ic_kind_home);
        imghome.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgschool.setImageResource(R.drawable.ic_kind_school);
        imgschool.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgetc.setImageResource(R.drawable.ic_kind_etc);
        imgetc.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
    }

    void changeImageSchool() {
        kind = "shcool";
        imgschool.setImageResource(R.drawable.ic_kind_school_white);
        imgschool.setBackground(getResources().getDrawable(R.drawable.btn_green_oval));

        imghome.setImageResource(R.drawable.ic_kind_home);
        imghome.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgcompany.setImageResource(R.drawable.ic_kind_company);
        imgcompany.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgetc.setImageResource(R.drawable.ic_kind_etc);
        imgetc.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
    }

    void changeImageEtc() {
        kind = "etc";
        imgetc.setImageResource(R.drawable.ic_kind_etc_white);
        imgetc.setBackground(getResources().getDrawable(R.drawable.btn_green_oval));

        imghome.setImageResource(R.drawable.ic_kind_home);
        imghome.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgschool.setImageResource(R.drawable.ic_kind_school);
        imgschool.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
        imgcompany.setImageResource(R.drawable.ic_kind_company);
        imgcompany.setBackground(getResources().getDrawable(R.drawable.btn_white_oval));
    }
}
