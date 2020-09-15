package com.example.barrierfree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.barrierfree.ui.bottomNV.BottomAlert;
import com.example.barrierfree.ui.bottomNV.BottomNVTest1;
import com.example.barrierfree.ui.find.FindFragment;
import com.example.barrierfree.ui.find.MapFragment;
import com.example.barrierfree.ui.member.ListViewMemberAdpater;
import com.example.barrierfree.ui.member.MemberInfoFragment;
import com.example.barrierfree.ui.settings.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Bitmap bitmap;

    private String fragmentTag;
    private Fragment fragmentClass;
    BottomNavigationView bottomNavigationView;
    BottomNVTest1 bottomNVTest1;
    BottomAlert bottomNVTest2;

    private AppBarConfiguration mAppBarConfiguration;

    private ListViewMemberAdpater adprequest, adpapply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.leftNV);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_safety, R.id.nav_notice, R.id.nav_center, R.id.nav_setting, R.id.nav_userInfo)
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

                if (id == R.id.nav_find) {
                    fragment = new FindFragment();
                    title = "Homes";
                    Toast.makeText(getApplicationContext(), "길찾기", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_dangerous) {
                    Toast.makeText(getApplicationContext(), "위험정보", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_board) {
                    fragment = new MapFragment();
                    fragmentTag = new MapFragment().getClass().getSimpleName();
                    fragmentClass = new MapFragment();
                    Toast.makeText(getApplicationContext(), "연결계정위치", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_safety) {
                    Toast.makeText(getApplicationContext(), "안심장소", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_setting) {
                    fragmentTag = new SettingFragment().getClass().getSimpleName();
                    fragmentClass = new SettingFragment();
                    replaceFragment(fragmentTag, fragmentClass);
                    Toast.makeText(getApplicationContext(), "사용자 설정", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_notice) {
                    Toast.makeText(getApplicationContext(), "공지사항", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_userInfo) {
                    fragmentTag = new MemberInfoFragment().getClass().getSimpleName();
                    fragmentClass = new MemberInfoFragment();
                    replaceFragment(fragmentTag, fragmentClass);
                } else if (id == R.id.nav_center) {
                    Toast.makeText(getApplicationContext(), "고객센터", Toast.LENGTH_LONG).show();
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

        user = mAuth.getCurrentUser();
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    //현재로그인한 사용자 정보를 통해 PhotoUrl 가져오기
                    if (user.getPhotoUrl() == null)
                        return;
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        if (user.getPhotoUrl() == null) {
            userProfileImg.setImageResource(R.drawable.ic_defaultuser);
        } else {
            mThread.start();
            try {
                mThread.join();
                userProfileImg.setImageBitmap(bitmap);
                userProfileImg.setRectRadius(100f);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        bottomNavigationView = findViewById(R.id.bottomNV);
        //Set up the view you're seeing for the first time.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new BottomNVTest1()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottomNV_tab1: {
                        fragmentTag = new BottomNVTest1().getClass().getSimpleName();
                        fragmentClass = new BottomNVTest1();
                        replaceFragment(fragmentTag, fragmentClass);

                        return true;
                    }
                    case R.id.bottomNV_tab2: {
                        fragmentTag = new BottomAlert().getClass().getSimpleName();
                        fragmentClass = new BottomAlert();
                        replaceFragment(fragmentTag, fragmentClass);

                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
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
}