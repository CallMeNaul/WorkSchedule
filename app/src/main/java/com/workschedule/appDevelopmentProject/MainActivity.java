package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidebar_navigationview);
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
        }
        else if(currentFragment == FRAGMENT_POMODORO) {
            getMenuInflater().inflate(R.menu.poromodo_action, menu);
        }
        else getMenuInflater().inflate(R.menu.home_action, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.calendar_action) {
            // hehe chọn ngày tháng Week View ở đây nhe
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
    public void PomodoroReport() {
        startActivity(new Intent(this, PomodoroReportActivity.class));
    }

    public void PomodoroSetting() {
        final Dialog dialog = new Dialog(this);
        customDialog(dialog, R.layout.pomorodo_setting_dialog);

        EditText etShortBreak, etLongBreak;
        ImageView btnExit;
        Button btnSave;

        etShortBreak = dialog.findViewById(R.id.et_shortbreak);
        etLongBreak = dialog.findViewById(R.id.et_longbreak);
        btnExit = dialog.findViewById(R.id.btn_pomo_setting_exit);
        btnSave = dialog.findViewById(R.id.btn_pomo_setting_save);

        PomodoroFragment pomodoroFragment = new PomodoroFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fr_pomodoro, pomodoroFragment);
        transaction.commit();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pomodoroFragment.setBreak(etLongBreak.getText().toString(), etShortBreak.getText().toString());
                dialog.cancel();
            }
        });
        //String longg = pomodoroFragment.getLongBreak(),
                //shortt = pomodoroFragment.getShortBreak();

        etShortBreak.setText(pomodoroFragment.getShortBreak());
        etLongBreak.setText(pomodoroFragment.getLongBreak());

        dialog.show();
    }
    public void customDialog(Dialog dialog, int id) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(id);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
    }
}