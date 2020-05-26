package com.example.barrierfree;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.barrierfree.ui.find.FindFragment;
import com.example.barrierfree.ui.settings.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    //BottomNV
    BottomNavigationView bottomNavigationView;
    BottomNVTest1 bottomNVTest1;
    BottomAlert bottomNVTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.leftNV);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_favorite, R.id.nav_logout, R.id.nav_notice, R.id.nav_center, R.id.nav_user)
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

                if( id == R.id.nav_find) {
                    fragment = new FindFragment();
                    title = "Homes";
                    Toast.makeText(getApplicationContext(), "길찾기", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_dangerous) {
                    Toast.makeText(getApplicationContext(), "위험정보 안내", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_board) {
                    Toast.makeText(getApplicationContext(), "게시판", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_favorite) {
                    Toast.makeText(getApplicationContext(), "즐겨찾기한 장소", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_user) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_layout,new SettingFragment()).commitAllowingStateLoss();
                    Toast.makeText(getApplicationContext(), "사용자 설정", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_logout) {
                    Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_notice) {
                    Toast.makeText(getApplicationContext(), "공지사항", Toast.LENGTH_LONG).show();
                }else if(id == R.id.nav_center) {
                    Toast.makeText(getApplicationContext(), "고객센터", Toast.LENGTH_LONG).show();
                }
                if(fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_layout, fragment);
                    ft.commit();
                }
                return true;
            }
        });

        //BottomNV
        bottomNavigationView = findViewById(R.id.bottomNV);
        //프래그먼트 생성
        bottomNVTest1 = new BottomNVTest1();
        bottomNVTest2 = new BottomAlert();
        //제일 처음 띄워줄 뷰를 세팅
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout,bottomNVTest1).commitAllowingStateLoss();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bottomNV_tab1:{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout,bottomNVTest1).commitAllowingStateLoss();
                        return true;
                    }
                    case R.id.bottomNV_tab2:{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_layout,bottomNVTest2).commitAllowingStateLoss();
                        return true;
                    }
                    default: return false;
                }
            }
        });
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
}