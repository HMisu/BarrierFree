package com.example.barrierfree;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.barrierfree.ui.find.FindFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.leftNV);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_favorite, R.id.nav_user, R.id.nav_help, R.id.nav_notice, R.id.nav_center)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
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
                        Toast.makeText(getApplicationContext(), "사용자 설정", Toast.LENGTH_LONG).show();
                    }else if(id == R.id.nav_help) {
                        Toast.makeText(getApplicationContext(), "도움말", Toast.LENGTH_LONG).show();
                    }else if(id == R.id.nav_notice) {
                        Toast.makeText(getApplicationContext(), "공지사항", Toast.LENGTH_LONG).show();
                    }else if(id == R.id.nav_center) {
                        Toast.makeText(getApplicationContext(), "고객센터", Toast.LENGTH_LONG).show();
                    }
                    if(fragment != null) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.nav_host_fragment, fragment);
                        ft.commit();
                    }
                    return true;
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
