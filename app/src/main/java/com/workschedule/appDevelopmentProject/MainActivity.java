package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workschedule.appDevelopmentProject.NavigationFragment.GroupFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.HomeFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.InfoFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.PomodoroFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.SettingFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.ShareFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_GROUP = 2;
    private static final int FRAGMENT_POMODORO = 3;
    private static final int FRAGMENT_SHARE = 4;
    private static final int FRAGMENT_INFO = 5;
    private static final int FRAGMENT_SETTING = 6;
    private int currentFragment = FRAGMENT_POMODORO;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView userNameTV, userEmailTV;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidebar_navigationview);
        View sidebar = navigationView.getHeaderView(0);
        userNameTV = (TextView) sidebar.findViewById(R.id.userNameTV);
        userEmailTV = (TextView) sidebar.findViewById(R.id.userEmailTV);

        if (user != null) {
            userNameTV.setText("Xin chào " + user.getEmail());
            userEmailTV.setText(user.getEmail());
        } else {
            Toast.makeText(MainActivity.this, "Đăng nhập lại", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ReplaceFragment(new PomodoroFragment());
        navigationView.setCheckedItem(R.id.nav_pomodoro);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentFragment == FRAGMENT_HOME) {
            getMenuInflater().inflate(R.menu.home_action, menu);
            toolbar.setTitle("WSche");
        }
        else if(currentFragment == FRAGMENT_POMODORO) {
            getMenuInflater().inflate(R.menu.poromodo_action, menu);
            toolbar.setTitle("Pomodoro");
        }
        else getMenuInflater().inflate(R.menu.home_action, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.calendar_action) {
            // hehe chọn ngày tháng Week View ở đây nhe
        } else if(id == R.id.report_action) {
            PomodoroReport();
        } else if (id == R.id.setting_action) {
            PomodoroSetting();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home && FRAGMENT_HOME != currentFragment) {
            ReplaceFragment(new HomeFragment());
            currentFragment = FRAGMENT_HOME;
            Menu menu = toolbar.getMenu();
            menu.clear();
            getMenuInflater().inflate(R.menu.home_action, menu);
            toolbar.setTitle("WSche");
        }
        else if(id == R.id.nav_group && FRAGMENT_GROUP != currentFragment) {
            ReplaceFragment(new GroupFragment());
            currentFragment = FRAGMENT_GROUP;
        }
        else if(id == R.id.nav_pomodoro && FRAGMENT_POMODORO != currentFragment) {
            ReplaceFragment(new PomodoroFragment());
            currentFragment = FRAGMENT_POMODORO;
            Menu menu = toolbar.getMenu();
            menu.clear();
            getMenuInflater().inflate(R.menu.poromodo_action, menu);
            toolbar.setTitle("Pomodoro");
        }
        else if(id == R.id.nav_share && FRAGMENT_SHARE != currentFragment) {
            ReplaceFragment(new ShareFragment());
            currentFragment = FRAGMENT_SHARE;
        }
        else if(id == R.id.nav_info && FRAGMENT_INFO != currentFragment) {
            ReplaceFragment(new InfoFragment());
            currentFragment = FRAGMENT_INFO;
        }
        else if(id == R.id.nav_setting && FRAGMENT_SETTING != currentFragment) {
            ReplaceFragment(new SettingFragment());
            currentFragment = FRAGMENT_SETTING;
        }
        else if(id == R.id.nav_logout) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void ReplaceFragment(Fragment fr) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_fr, fr);
        fragmentTransaction.commit();
    }
    public void PomodoroReport() { startActivity(new Intent(this, PomodoroReportActivity.class));}
    public void PomodoroSetting() { startActivity(new Intent(this, PomodoroSettingActivity.class));}
}