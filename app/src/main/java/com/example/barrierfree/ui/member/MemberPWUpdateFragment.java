package com.example.barrierfree.ui.member;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.barrierfree.MainActivity;
import com.example.barrierfree.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MemberPWUpdateFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View root;

    Button btnupdate;
    TextView pw1, pw2, chekcpw2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_member_pw, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        btnupdate = (Button) root.findViewById(R.id.btn_update);

        pw1 = (TextView) root.findViewById(R.id.edit_pw1);
        pw2 = (TextView) root.findViewById(R.id.edit_pw2);
        chekcpw2 = (TextView) root.findViewById(R.id.check_pw);

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pw1.getText().toString().equals("") || pw1.getText().toString() == null) {
                    Toast.makeText(getActivity(), "현재 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pw2.getText().toString().equals("") || pw2.getText().toString() == null) {
                    Toast.makeText(getActivity(), "변경할 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (chekcpw2.getText().toString().equals("") || chekcpw2.getText().toString() == null) {
                    Toast.makeText(getActivity(), "변경할 비밀번호를 한 번 더 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!pw2.getText().toString().trim().equals(chekcpw2.getText().toString().trim())){
                    Toast.makeText(getActivity(), "변경할 비밀번호와 확인란의 비밀번호가 불일치합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                String regex_pw = "^[A-Za-z0-9]{7,}$";
                if(!pw2.getText().toString().trim().matches(regex_pw)) {
                    Toast.makeText(getActivity(), "최소 7자, 최소 하나의 영문자 및 하나의 숫자로 구성하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pw1.getText().toString().trim());
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(pw2.getText().toString().trim())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "비밀번호가 변경되셨습니다.", Toast.LENGTH_SHORT).show();
                                                        String fragmentTag = new MemberInfoFragment().getClass().getSimpleName();
                                                        Fragment fragmentClass = new MemberInfoFragment();
                                                        ((MainActivity) getActivity()).replaceFragment(fragmentTag, fragmentClass);
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(getActivity(), "현재 비밀번호를 잘못입력하셨습니다.", Toast.LENGTH_SHORT).show();
                                }
                                Log.d("메시지", "User re-authenticated.");
                            }
                        });

            }
        });

        return root;
    }

    public void refreshFragment(){
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.detach(this).attach(this).commitAllowingStateLoss();
    }
}
