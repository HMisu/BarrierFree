package com.example.barrierfree.ui.member;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.example.barrierfree.models.Safety;
import com.example.barrierfree.ui.find.SearchMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SafetyFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private View root;
    private ListView lvsafety;
    private ListViewSafetyAdpater adpsafety;

    private Button btnadd;

    private String mem_weak;
    int count = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_safety, container, false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        btnadd = (Button) root.findViewById(R.id.btn_search_mem);

        // Adapter 생성
        adpsafety = new ListViewSafetyAdpater(getActivity(), SafetyFragment.this);

        lvsafety = (ListView) root.findViewById(R.id.listview_safety);
        lvsafety.setAdapter(adpsafety);

        db.collection("connection").whereEqualTo("connect", true).whereEqualTo("mem_protect", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (task.isSuccessful()) {
                            if (querySnapshot.isEmpty()) {
                                Log.d("메시지", "empty");
                                mem_weak = user.getUid();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mem_weak = document.getString("mem_weak");
                            }
                            Log.d("메시지", "mem_weak : " + mem_weak);
                            db.collection("safety").whereEqualTo("mem_weak", mem_weak).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String sf_id = document.getId();
                                                    String kind = document.getString("kind");
                                                    String name = document.getString("name");
                                                    String addr = document.getString("addr");
                                                    Double latitude = document.getDouble("latitude");
                                                    Double longitude = document.getDouble("longitude");
                                                    String mem_weak = document.getString("mem_weak");
                                                    String mem_register = document.getString("mem_register");
                                                    Log.d("메시지", "document.getId() : " + sf_id);
                                                    Safety safety = new Safety(sf_id, kind, name, addr, latitude, longitude, mem_weak, mem_register);
                                                    adpsafety.add(safety);
                                                    adpsafety.notifyDataSetChanged();
                                                }
                                            } else {
                                                Log.d("메시지", "Error getting documents: ", task.getException());
                                                return;
                                            }
                                        }
                                    });
                        }
                    }
                });

        return root;
    }

    public void refreshFragment() {
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.detach(this).attach(this).commitAllowingStateLoss();
    }

    public void updateSafety(String sf_id) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("rfgAdd", false);
        bundle.putString("rfgId", sf_id);
        Log.d("메시지", "ㅎㅎ " + sf_id);
        String fragmentTag = new SafetyEditFragment().getClass().getSimpleName();
        Fragment fragmentClass = new SafetyEditFragment();
        ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass, bundle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.safety_add:
                count = 0;
                db.collection("safety").whereEqualTo("mem_register", user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        count = count+1;
                                    }
                                    if(count >= 3){
                                        Toast.makeText(getActivity(), "최대 등록 갯수 3개를 초과했습니다", Toast.LENGTH_SHORT).show();
                                    } else{
                                        Toast.makeText(getActivity(), "화면의 장소를 길게 눌러 선택하세요", Toast.LENGTH_SHORT).show();
                                        String fragmentTag = new SearchMapFragment().getClass().getSimpleName();
                                        Fragment fragmentClass = new SearchMapFragment();
                                        ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);
                                    }
                                } else {
                                    Log.d("메시지", "Error getting documents: ", task.getException());
                                    return;
                                }
                            }
                        });
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}