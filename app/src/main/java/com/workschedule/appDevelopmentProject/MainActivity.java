package com.workschedule.appDevelopmentProject;

import static android.content.ContentValues.TAG;

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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.workschedule.appDevelopmentProject.NavigationFragment.GroupFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.HomeFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.InfoFragment;
import com.workschedule.appDevelopmentProject.NavigationFragment.PomodoroFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private double pomodoroCounter;
    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_GROUP = 2;
    private static final int FRAGMENT_POMODORO = 3;
    private static final int FRAGMENT_INFO = 4;
    private static final int POMODORO_SETTING_REQUEST_CODE = 10;
    private int currentFragment = FRAGMENT_HOME;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView userNameTV, userEmailTV;
    private ImageView userAvtIM;
    public FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private GroupFragment groupFragment;
    private PomodoroFragment pomodoroFragment;
    private ConstraintLayout forwardLayout;
    private ArrayList<String> tvCounterArray;
    private SharedPreferences totalTimePreferences;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");;
    private DatabaseReference userReference = database.getReference("User");
    private static boolean isNewUser = true;
    private int onlineDay;
    private LocalDate lastOnlineDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalTimePreferences = getSharedPreferences("pomodoroTotalTime", MODE_PRIVATE);

        // mặc định lúc mới tạo người dùng
        onlineDay = 1;
        lastOnlineDay = LocalDate.now();
        pomodoroCounter = 0;
        // Lấy onlineDay, lastOnlineDay, pomodoroCounter từ Realtime Database

        LocalDate today = LocalDate.now();
        if(lastOnlineDay.isBefore(today)) {
            onlineDay++;
            lastOnlineDay = today;
        }
        // Cập nhật lại lên Realtime Database

        // Kéo xuống dưới cùng có hàm setPomodoroCounter, cập nhật lên realtime
        // database cái pomodoroCounter khi gọi hàm đó luôn

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeFragment = new HomeFragment();
        groupFragment = new GroupFragment();
        pomodoroFragment = new PomodoroFragment();
        tvCounterArray = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.sidebar_navigationview);
        View sidebar = navigationView.getHeaderView(0);
        userNameTV = sidebar.findViewById(R.id.userNameTV);
        userEmailTV = sidebar.findViewById(R.id.userEmailTV);
        userAvtIM = sidebar.findViewById(R.id.img_main_avatar);

        if (user != null) {
            if (user.getDisplayName() == null || user.getDisplayName() == ""){
                userNameTV.setText(getString(R.string.hello) + " " + user.getEmail());
                userReference.child(user.getUid()).child("Name").setValue(user.getEmail());
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(user.getEmail())
                        .build();
                user.updateProfile(profileUpdates);
            } else {
                userNameTV.setText(getString(R.string.hello) + " " + user.getDisplayName());
            }

            userReference.child(user.getUid()).child("UID").setValue(user.getUid());
            userReference.child(user.getUid()).child("email").setValue(user.getEmail());
            userEmailTV.setText(user.getEmail());
            if (user.getPhotoUrl() != null){
                setUserAvtIM(user.getPhotoUrl());
            } else {
                userReference.child(user.getUid()).child("Avt").setValue(null);
            }
        } else {
            Toast.makeText(MainActivity.this, getText(R.string.relogin), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ReplaceFragment(homeFragment, R.id.content_fr);
        navigationView.setCheckedItem(R.id.nav_home);
    }
    public void setUserNameTV(String name){
        userNameTV.setText(getString(R.string.hello) + ", " + name);
    }
    public void setUserAvtIM(Uri mImageUri){
        Picasso.get().load(mImageUri)
                .resize(150,150)
                .transform(new RoundedTransformation())
                .into(userAvtIM);
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
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.calendar_action) {
            openDatePicker();
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
            ConstraintLayout po = findViewById(R.id.content_fr2);
            po.setVisibility(View.GONE);
            po = findViewById(R.id.content_fr1);
            po.setVisibility(View.GONE);
        }
        else if(id == R.id.nav_group && FRAGMENT_GROUP != currentFragment) {
            ReplaceFragment(groupFragment, R.id.content_fr1);
            currentFragment = FRAGMENT_GROUP;
            Menu menu = toolbar.getMenu();
            menu.clear();
            toolbar.setTitle(getString(R.string.group));
            ConstraintLayout po = findViewById(R.id.content_fr2);
            po.setVisibility(View.GONE);
            po = findViewById(R.id.content_fr);
            po.setVisibility(View.GONE);
        }
        else if(id == R.id.nav_pomodoro && FRAGMENT_POMODORO != currentFragment) {
            ReplaceFragment(pomodoroFragment, R.id.content_fr2);
            currentFragment = FRAGMENT_POMODORO;
            Menu menu = toolbar.getMenu();
            menu.clear();
            getMenuInflater().inflate(R.menu.poromodo_action, menu);
            toolbar.setTitle("Pomodoro");
            ConstraintLayout po = findViewById(R.id.content_fr1);
            po.setVisibility(View.GONE);
            po = findViewById(R.id.content_fr);
            po.setVisibility(View.GONE);
        }
        else if(id == R.id.nav_info && FRAGMENT_INFO != currentFragment) {
            ReplaceFragment(new InfoFragment(), R.id.content_fr1);
            currentFragment = FRAGMENT_INFO;
            Menu menu = toolbar.getMenu();
            menu.clear();
            toolbar.setTitle("Thông tin người dùng");
            ConstraintLayout po = findViewById(R.id.content_fr2);
            po.setVisibility(View.GONE);
            po = findViewById(R.id.content_fr);
            po.setVisibility(View.GONE);
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
        forwardLayout.setVisibility(View.VISIBLE);
        fragmentTransaction.commit();
    }
    public void PomodoroSetting() {
        Intent intent = new Intent(this, PomodoroSettingActivity.class);
        startActivityForResult(intent, POMODORO_SETTING_REQUEST_CODE);
    }
    public void openDatePicker(){

        // on below line we are getting
        // our day, month and year.
        int year = CalendarUtils.selectedDate.getYear();
        int month = CalendarUtils.selectedDate.getMonthValue() - 1;
        int day = CalendarUtils.selectedDate.getDayOfMonth();
        int style = AlertDialog.THEME_DEVICE_DEFAULT_DARK;

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, style, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // on below line we are setting date to our edit text.
                CalendarUtils.selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                homeFragment.setWeekView();
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(POMODORO_SETTING_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            tvCounterArray = data.getStringArrayListExtra("returnCounter");
            boolean autoValue = data.getBooleanExtra("returnAutoBreak", false);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("newCounter", tvCounterArray);
            bundle.putBoolean("newAutoBreak", autoValue);
            autoValue = data.getBooleanExtra("returnAutoPomodoro", false);
            bundle.putBoolean("newAutoPomodoro", autoValue);
            autoValue = data.getBooleanExtra("returnAutoTick", false);
            bundle.putBoolean("newAutoTick", autoValue);
            autoValue = data.getBooleanExtra("returnAutoChangeTask", false);
            bundle.putBoolean("newAutoChangeTask", autoValue);

            pomodoroFragment.setArguments(bundle);
            FragmentTransaction frTransaction = getSupportFragmentManager().beginTransaction();
            frTransaction.replace(R.id.content_fr2, pomodoroFragment);
            frTransaction.commit();
        }
    }

    public ArrayList<String> getTextViewCounterArrayList() { return tvCounterArray; }
    public String GetUserEmail() {
        return user.getEmail();
    }
    public FirebaseUser getUser() {
        return user;
    }

    public DatabaseReference getUserReference() {
        return userReference;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setPomodoroCounter(double pomodoroCounter) {
        this.pomodoroCounter = pomodoroCounter;
    }
    public double getPomodoroCounter() { return pomodoroCounter;
    }
    public int getOnlineDay() { return onlineDay; }
}