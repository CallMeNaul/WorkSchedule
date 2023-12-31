package com.workschedule.appDevelopmentProject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.workschedule.appDevelopmentProject.NavigationFragment.GroupFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.HomeFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.InfoFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.PomodoroFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.SettingFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.ShareFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_GROUP = 2;
    private static final int FRAGMENT_POMODORO = 3;
    private static final int FRAGMENT_SHARE = 4;
    private static final int FRAGMENT_INFO = 5;
    private static final int FRAGMENT_SETTING = 6;
    private static final int POMODORO_SETTING_REQUEST_CODE = 10;
    private int currentFragment = FRAGMENT_POMODORO;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView userNameTV, userEmailTV;
    public FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private GroupFragment groupFragment;
    private PomodoroFragment pomodoroFragment;
    private InfoFragment infoFragment;
    private SettingFragment settingFragment;
    private ShareFragment shareFragment;
    private ConstraintLayout forwardLayout;
    private ArrayList<String> tvCounterArray;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference userReference = database.getReference("User");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeFragment = new HomeFragment();
        groupFragment = new GroupFragment();
        pomodoroFragment = new PomodoroFragment();
        infoFragment = new InfoFragment();
        settingFragment = new SettingFragment();
        shareFragment = new ShareFragment();

        tvCounterArray = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidebar_navigationview);
        View sidebar = navigationView.getHeaderView(0);
        userNameTV = sidebar.findViewById(R.id.userNameTV);
        userEmailTV = sidebar.findViewById(R.id.userEmailTV);

        if (user != null) {
            userNameTV.setText(getString(R.string.hello) + ", " + user.getEmail());
            userEmailTV.setText(user.getEmail());
            userReference.child(user.getUid()).child("email").setValue(user.getEmail());
            userReference.child(user.getUid()).child("UID").setValue(user.getUid());
            userReference.child(user.getUid()).child("Name").setValue(user.getDisplayName());
        } else {
            Toast.makeText(MainActivity.this, getText(R.string.relogin), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ReplaceFragment(new PomodoroFragment(), R.id.content_fr2);
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
            ReplaceFragment(homeFragment, R.id.content_fr);
            currentFragment = FRAGMENT_HOME;
            Menu menu = toolbar.getMenu();
            menu.clear();
            getMenuInflater().inflate(R.menu.home_action, menu);
            toolbar.setTitle("WSche");
        }
        else if(id == R.id.nav_group && FRAGMENT_GROUP != currentFragment) {
            ReplaceFragment(groupFragment, R.id.content_fr1);
            currentFragment = FRAGMENT_GROUP;
        }
        else if(id == R.id.nav_pomodoro && FRAGMENT_POMODORO != currentFragment) {
            ReplaceFragment(pomodoroFragment, R.id.content_fr2);
            currentFragment = FRAGMENT_POMODORO;
            Menu menu = toolbar.getMenu();
            menu.clear();
            getMenuInflater().inflate(R.menu.poromodo_action, menu);
            toolbar.setTitle("Pomodoro");
        }
        else if(id == R.id.nav_share && FRAGMENT_SHARE != currentFragment) {
            ReplaceFragment(shareFragment, R.id.content_fr1);
            currentFragment = FRAGMENT_SHARE;
        }
        else if(id == R.id.nav_info && FRAGMENT_INFO != currentFragment) {
            ReplaceFragment(infoFragment, R.id.content_fr1);
            currentFragment = FRAGMENT_INFO;
        }
        else if(id == R.id.nav_setting && FRAGMENT_SETTING != currentFragment) {
            ReplaceFragment(settingFragment, R.id.content_fr1);
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
    public void ReplaceFragment(Fragment fr, int containerViewId) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fr);
        forwardLayout = findViewById(containerViewId);
        forwardLayout.bringToFront();
        fragmentTransaction.commit();
    }
    public void PomodoroReport() { startActivity(new Intent(this, PomodoroReportActivity.class));}
    public void PomodoroSetting() {
        Intent intent = new Intent(this, PomodoroSettingActivity.class);
        startActivityForResult(intent, POMODORO_SETTING_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(POMODORO_SETTING_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            tvCounterArray = data.getStringArrayListExtra("returnCounter");
            boolean autoValue = data.getBooleanExtra("returnAutoBreak", false);
            Log.i("mAutoBr", String.valueOf(autoValue));
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("newCounter", tvCounterArray);
            bundle.putBoolean("newAutoBreak", autoValue);
            autoValue = data.getBooleanExtra("returnAutoPomodoro", false);
            Log.i("mAutopo", String.valueOf(autoValue));
            bundle.putBoolean("newAutoPomodoro", autoValue);
            autoValue = data.getBooleanExtra("returnAutoTick", false);
            Log.i("mAutotick", String.valueOf(autoValue));
            bundle.putBoolean("newAutoTick", autoValue);
            autoValue = data.getBooleanExtra("returnAutoChangeTask", false);
            Log.i("mAutoch", String.valueOf(autoValue));
            bundle.putBoolean("newAutoChangeTask", autoValue);

            pomodoroFragment.setArguments(bundle);
            FragmentTransaction frTransaction = getSupportFragmentManager().beginTransaction();
            frTransaction.replace(R.id.content_fr2, pomodoroFragment);
            frTransaction.commit();
        }
    }

    public ArrayList<String> getTextViewCounterArrayList() { return tvCounterArray; }
}