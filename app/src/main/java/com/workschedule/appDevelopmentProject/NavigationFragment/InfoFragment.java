package com.workschedule.appDevelopmentProject.NavigationFragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.workschedule.appDevelopmentProject.MainActivity;
import com.workschedule.appDevelopmentProject.R;
import com.squareup.picasso.Picasso;
import com.workschedule.appDevelopmentProject.RoundedTransformation;
import com.workschedule.appDevelopmentProject.Upload;

import java.text.DecimalFormat;
import java.time.Instant;

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
    private TextView changeUserName, changeUserAvt, changeUserPass;
    private TextView tvCumulativeHour, tvEmail, tvUserName;
    private ImageView imUserAvt;
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
        if (user.getDisplayName() == null) {
            tvUserName.setText(user.getEmail());
        }else {
            tvUserName.setText(user.getDisplayName());
        }
        imUserAvt = view.findViewById(R.id.img_avatar_main);
        if (user.getPhotoUrl()!=null)
        {
            mImageUri = user.getPhotoUrl();
            Picasso.get().load(mImageUri)
                    .resize(150, 150)
                    .transform(new RoundedTransformation())
                    .into(imUserAvt);
        }

        changeUserAvt = view.findViewById(R.id.tv_change_avatar_main);
        changeUserName = view.findViewById(R.id.tv_change_username);
        changeUserPass = view.findViewById(R.id.tv_change_password);

        changeUserAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeUserAvtDialog();
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


    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    public void openChangeUserAvtDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_useravt);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        mButtonChooseImage = dialog.findViewById(R.id.button_choose_image);
        mButtonUpload = dialog.findViewById(R.id.button_upload);
        mTextViewShowUploads = dialog.findViewById(R.id.text_view_show_uploads);
        mImageView = dialog.findViewById(R.id.image_view);
        mProgressBar = dialog.findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile(dialog);
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(Dialog dialog) {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(user.getUid()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            // Get the download URL
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    user.updateProfile(profileUpdates);
                                    userReference.child(user.getUid()).child("Avt").setValue(downloadUrl);
                                    Picasso.get().load(mImageUri).resize(150,150).transform(new RoundedTransformation())
                                            .into(imUserAvt);
                                    mainActivity.setUserAvtIM(mImageUri);
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }



}