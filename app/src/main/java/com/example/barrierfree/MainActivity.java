package com.example.barrierfree;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.barrierfree.service.LocationService;
import com.example.barrierfree.ui.bottomNV.BottomAlert;
import com.example.barrierfree.ui.find.FindFragment;
import com.example.barrierfree.ui.find.SearchMapFragment;
import com.example.barrierfree.ui.member.ListViewMemAdpater;
import com.example.barrierfree.ui.member.MemberInfoFragment;
import com.example.barrierfree.ui.member.MemberInfoUpdateFragment;
import com.example.barrierfree.ui.member.SafetyFragment;
import com.example.barrierfree.ui.settings.SettingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    Bitmap bitmap;
    public static Context mContext;

    private String fragmentTag;
    private Fragment fragmentClass;
    BottomNavigationView bottomNavigationView;

    NavigationView navigationView;
    private AppBarConfiguration mAppBarConfiguration;

    private ListViewMemAdpater adprequest, adpapply;
    private final int GET_GALLERY_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.leftNV);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_safety, R.id.nav_setting, R.id.nav_userInfo)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                menuitem.setChecked(false);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawers();
                int id = menuitem.getItemId();
                Fragment fragment = null;
                String title = getString(R.string.app_name);

                if (id == R.id.nav_dangerous) {
                    fragmentTag = new FindFragment().getClass().getSimpleName();
                    fragmentClass = new FindFragment();
                    replaceFragment(fragmentTag, fragmentClass);
                    Toast.makeText(getApplicationContext(), "위험정보", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_safety) {
                    fragmentTag = new SearchMapFragment().getClass().getSimpleName();
                    fragmentClass = new SearchMapFragment();
                    replaceFragment(fragmentTag, fragmentClass);
                    Toast.makeText(getApplicationContext(), "안심장소", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_setting) {
                    fragmentTag = new SettingFragment().getClass().getSimpleName();
                    fragmentClass = new SettingFragment();
                    replaceFragment(fragmentTag, fragmentClass);
                    Toast.makeText(getApplicationContext(), "사용자 설정", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_userInfo) {
                    fragmentTag = new MemberInfoFragment().getClass().getSimpleName();
                    fragmentClass = new MemberInfoFragment();
                    replaceFragment(fragmentTag, fragmentClass);
                }
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, fragment);
                    ft.commit();
                }
                return true;
            }
        });

        //Nav_header_main
        //navigationView.setNavigationItemSelectedListener(this);
        View nav_header_view = navigationView.getHeaderView(0);

        TextView userName = (TextView) nav_header_view.findViewById(R.id.txt_user_name);
        TextView userEmail = (TextView) nav_header_view.findViewById(R.id.txt_user_email);
        RoundImageView userProfileImg = (RoundImageView) nav_header_view.findViewById(R.id.img_user);

        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(mContext).load(user.getPhotoUrl().toString()).into(userProfileImg);
            userProfileImg.setRectRadius(100f);
        }

        bottomNavigationView = findViewById(R.id.bottomNV);
        //Set up the view you're seeing for the first time.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new SearchMapFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.bottomNV_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottomNV_dangerous: {
                        fragmentTag = new FindFragment().getClass().getSimpleName();
                        fragmentClass = new FindFragment();
                        replaceFragment(fragmentTag, fragmentClass);
                        return true;
                    }
                    case R.id.bottomNV_alter: {
                        fragmentTag = new BottomAlert().getClass().getSimpleName();
                        fragmentClass = new BottomAlert();
                        replaceFragment(fragmentTag, fragmentClass);
                        return true;
                    }
                    case R.id.bottomNV_home: {
                        fragmentTag = new SearchMapFragment().getClass().getSimpleName();
                        fragmentClass = new SearchMapFragment();
                        replaceFragment(fragmentTag, fragmentClass);
                        return true;
                    }
                    case R.id.bottomNV_safety: {
                        fragmentTag = new SafetyFragment().getClass().getSimpleName();
                        fragmentClass = new SafetyFragment();
                        replaceFragment(fragmentTag, fragmentClass);
                        return true;
                    }
//                    case R.id.bottomNV_help: {
//                        return true;
//                    }
                    default:
                        return false;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                return;
            }
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            db.collection("connection").whereEqualTo("connect", true).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getString("mem_weak").equals(user.getUid())) {
                                        Log.d("메시지", "Background Service 실행");
                                        //Background Service 실행
                                        startService(new Intent(MainActivity.this, LocationService.class));
                                        return;
                                    }
                                }
                            } else {
                                Log.d("메시지", "Error getting documents: ", task.getException());
                                return;
                            }
                        }
                    });
        }
    }

    public void stopLocationService() {
        stopService(new Intent(MainActivity.this, LocationService.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void replaceFragment(String fragmentTag, Fragment fragmentClass) {
        getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().addToBackStack(fragmentTag)
                .replace(R.id.nav_host_fragment, fragmentClass).commitAllowingStateLoss();
    }

    public void replaceFragment(String fragmentTag, Fragment fragmentClass, Bundle bundle) {
        fragmentClass.setArguments(bundle);
        getSupportFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().addToBackStack(fragmentTag)
                .replace(R.id.nav_host_fragment, fragmentClass).commitAllowingStateLoss();
    }

    @Override //갤러리에서 이미지 불러온 후 행동
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {
            int request = requestCode & 0xffff;
            //stickyny.tistory.com/86 [Mindware 깍두기]
            // Check which request we're responding to
            Fragment fragment = new MemberInfoUpdateFragment();
            fragment.onActivityResult(request, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("메시지","StartACTIVITY");
                    stopLocationService();

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        db.collection("connection").whereEqualTo("connect", true).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.getString("mem_weak").equals(user.getUid())) {
                                                    Log.d("메시지", "Background Service 실행");
                                                    //Background Service 실행
                                                    startService(new Intent(MainActivity.this, LocationService.class));
                                                    return;
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
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}