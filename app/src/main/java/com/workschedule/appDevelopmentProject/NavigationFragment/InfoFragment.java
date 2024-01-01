package com.workschedule.appDevelopmentProject.NavigationFragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.workschedule.appDevelopmentProject.MainActivity;
import com.workschedule.appDevelopmentProject.R;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private TextView changeUserName, changeUserAvt, changeUserPass;
    private TextView tvCumulativeHour, tvEmail, tvUserName;
    private MainActivity mainActivity;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference userReference = database.getReference("User");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        mainActivity = (MainActivity) getActivity();
        tvCumulativeHour = view.findViewById(R.id.tv_cumulative_hour);
        SharedPreferences share = mainActivity.getSharedPreferences("pomodoroTotalTime", MODE_PRIVATE);
        double hour = share.getFloat("pomodoroCounter", 0);
        if(hour < 1)
            tvCumulativeHour.setText(new DecimalFormat("0.0000").format(hour));
        else {
            int hours = (int) hour;
            tvCumulativeHour.setText(String.valueOf(hours));
        }
        tvEmail = view.findViewById(R.id.tv_email_main);
        tvEmail.setText(user.getEmail());
        tvUserName = view.findViewById(R.id.tv_username_main);
        tvUserName.setText(user.getDisplayName());

        changeUserAvt = view.findViewById(R.id.tv_change_avatar_main);
        changeUserName = view.findViewById(R.id.tv_change_username);
        changeUserPass = view.findViewById(R.id.tv_change_password);

        changeUserAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        changeUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeUsernameDialog();
            }
        });
        changeUserPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeUserPassDialog();
            }
        });


        return view;
    }

    private void openChangeUserPassDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_userpass);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        Button btnOK, btnCancel;
        EditText oldPassET, newPassET, confirmPassET;
        oldPassET = (EditText) dialog.findViewById(R.id.old_password);
        newPassET = (EditText) dialog.findViewById(R.id.new_password);
        confirmPassET = (EditText) dialog.findViewById(R.id.confirm_new_password);
        btnOK = dialog.findViewById(R.id.btn_ok_changeuserpass);
        btnCancel = dialog.findViewById(R.id.btn_cancel_changeuserpass);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = newPassET.getText().toString();
                String oldPassword = oldPassET.getText().toString();
                String confirmPassword = confirmPassET.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (!newPassword.equals(confirmPassword)) {
                                        Toast.makeText(getContext(), "Xác nhận lại mật khẩu!", Toast.LENGTH_SHORT).show();
                                    }
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "Sai mật khẩu cũ!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openChangeUsernameDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_username);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        Button btnOK, btnCancel;
        TextView newUserNameTV = (TextView) dialog.findViewById(R.id.username);
        newUserNameTV.setText(user.getDisplayName());
        btnOK = (Button) dialog.findViewById(R.id.btn_ok_changeusername);
        btnCancel = (Button) dialog.findViewById(R.id.btn_cancel_changeusername);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUserName = newUserNameTV.getText().toString();
                tvUserName.setText(newUserName);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUserName)
                        .build();
                user.updateProfile(profileUpdates);
                userReference.child(user.getUid()).child("Name").setValue(newUserName);
                mainActivity.setUserNameTV(newUserName);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}