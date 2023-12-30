package com.workschedule.appDevelopmentProject.NavigationFragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workschedule.appDevelopmentProject.CalendarUtils;
import com.workschedule.appDevelopmentProject.Group;
import com.workschedule.appDevelopmentProject.GroupAdapter;
import com.workschedule.appDevelopmentProject.GroupTouchHelper;
import com.workschedule.appDevelopmentProject.GroupTouchListener;
import com.workschedule.appDevelopmentProject.R;
import com.workschedule.appDevelopmentProject.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment implements GroupTouchListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private RecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;
    private Button buttonAddGroup;
    private LocalTime time;
    private LocalDate date;
    ArrayList<Group> groupArrayList;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference userReference = database.getReference("User");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference groupReference = userReference.child(uid).child("Group");
    private static boolean isNew = true;
    private static boolean isNewUser = true;
    private ConstraintLayout rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        setGroupAdapter();
    }
    private void initWidgets(View v)
    {
        groupRecyclerView = v.findViewById(R.id.recyclerview_groups);

        groupRecyclerView.findViewById(R.id.recyclerview_groups);
        groupRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        groupRecyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(groupRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        groupRecyclerView.addItemDecoration(itemDecoration);
        groupRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleCallback = new GroupTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(groupRecyclerView);

        buttonAddGroup = v.findViewById(R.id.btn_add_group);

        rootView = v.findViewById(R.id.group_root_view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initWidgets(view);
        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isNew) {
                    for (DataSnapshot groupPlanSnapshot: snapshot.getChildren()) {
                        String groupDate = groupPlanSnapshot.child("groupDate").getValue(String.class);
                        String groupName = groupPlanSnapshot.child("groupName").getValue(String.class);
                        String groupTime = groupPlanSnapshot.child("groupTime").getValue(String.class);
                        String groupMember = groupPlanSnapshot.child("groupMember").getValue(String.class);
                        String groupKey = groupPlanSnapshot.getKey();
                        Group group = new Group(groupKey, groupName, groupDate, groupTime, groupMember);
                        Group.groupArrayList.add(group);
                    }
                    setGroupAdapter();
                    isNew = false;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isNewUser){
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String userID = userSnapshot.getKey();
                        User user = new User(userID, userEmail);
                        User.userArrayList.add(user);
                    }
                    isNewUser = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        buttonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddGroup();
                Log.d(TAG, "onClick() called with: view = [" + view + "]");
            }
        });
        return view;
    }
    public void openDialogAddGroup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.new_group_dialog);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        EditText groupNameET = dialog.findViewById(R.id.et_group_name);
        EditText groupMemET = dialog.findViewById(R.id.et_members);
        Button groupAddBT = dialog.findViewById(R.id.btn_add_group_dialog);
        ImageView groupExitIV = dialog.findViewById(R.id.img_exit_add_group);

        time = LocalTime.now();
        date = LocalDate.now();
        groupExitIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        groupAddBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameET.getText().toString();
                String groupMem = groupMemET.getText().toString();
                String groupDate = CalendarUtils.formattedDate(date);
                String groupTime = CalendarUtils.formattedTime(time);
                String groupKey = groupReference.push().getKey();
                String groupMember = user.getEmail() + " " + groupMem;
                Group group = new Group(groupKey, groupName, groupDate, groupTime, groupMember);
                addGroupToMember(group);
                Group.groupArrayList.add(group);
                groupReference.child(groupKey).setValue(group);
                setGroupAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addGroupToMember(Group group) {
        String[] members = group.getGroupMember().split(" ");
        for (String member : members) {
            boolean found = false;
            for (int j = 0; j < User.userArrayList.size(); j++) {
                if (member.equals(User.userArrayList.get(j).getUserEmail())) {
                    found = true;
                    userReference.child(User.userArrayList.get(j).getUserID()).child("Group").child(group.getGroupID()).setValue(group);
                }
            }
            if (!found) Toast.makeText(rootView.getContext(), "Không tìm thấy người dùng: " +  member, Toast.LENGTH_SHORT).show();
        }
    }

    public void openDialogEditGroup(String groupKey, String groupName, String groupDate, String groupTime, String groupMember) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.new_group_dialog);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        EditText groupNameET = dialog.findViewById(R.id.et_group_name);
        EditText groupMemET = dialog.findViewById(R.id.et_members);
        Button groupAddBT = dialog.findViewById(R.id.btn_add_group_dialog);
        ImageView groupExitIV = dialog.findViewById(R.id.img_exit_add_group);

        groupNameET.setText(groupName);
        groupMemET.setText(groupMember);

        groupExitIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        groupAddBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameET.getText().toString();
                String groupMember = groupMemET.getText().toString();
                Group group = new Group(groupKey, groupName, groupDate, groupTime, groupMember);
                updateGroupToMember(group);
                groupReference.child(groupKey).setValue(group);
                for (int i = 0; i < Group.groupArrayList.size(); i++) {
                    Group groupItem = Group.groupArrayList.get(i);
                    if (groupItem.getGroupID().equals(groupKey)) {
                        Group.groupArrayList.set(i, group);
                        break;
                    }
                }
                setGroupAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateGroupToMember(Group group) {
        String[] members = group.getGroupMember().split(" ");
        for (String member : members) {
            boolean found = false;
            for (int j = 0; j < User.userArrayList.size(); j++) {
                if (member.equals(User.userArrayList.get(j).getUserEmail())) {
                    found = true;
                    userReference.child(User.userArrayList.get(j).getUserID()).child("Group").child(group.getGroupID()).setValue(group);
                }
            }
            if (!found) Toast.makeText(rootView.getContext(), "Không tìm thấy người dùng: " +  member, Toast.LENGTH_SHORT).show();
        }
    }

    public void setGroupAdapter()
    {
        groupArrayList = Group.AllGroups();
        groupAdapter = new GroupAdapter(GroupFragment.this, groupArrayList);
        groupRecyclerView.setAdapter(groupAdapter);
    }
    public void deleteGroup(String id){
        Group groupDelete = Group.groupArrayList.get(0);
        String namePlanDeleted = groupDelete.getGroupName();
        final boolean[] permanent_deleleted = {true};
        for (int i = 0; i < Group.groupArrayList.size(); i++) {
            Group groupItem = Group.groupArrayList.get(i);
            if (groupItem.getGroupID().equals(id)) {
                groupDelete = groupItem;
                namePlanDeleted = groupItem.getGroupName();
                Group.groupArrayList.remove(groupItem);
                break;
            }
        }
        String members = groupDelete.getGroupMember();
        String email = user.getEmail();
        members.replace(email, "");
        groupDelete.setGroupMember(members);
        Log.d(TAG, "Member: " + groupDelete.getGroupMember());

        String message = getString(R.string.exited_group_plan) + namePlanDeleted;
        Snackbar snackbar = Snackbar.make(rootView, message, 5000);
        Group finalGroupDelete = groupDelete;
        snackbar.setTextColor(getContext().getColor(R.color.text_color))
                .setBackgroundTint(getContext().getColor(R.color.milk_white))
                .setActionTextColor(getResources().getColor(R.color.text_color))
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Group.groupArrayList.add(finalGroupDelete);
                        groupReference.child(id).setValue(finalGroupDelete);
                        permanent_deleleted[0] = false;
                        setGroupAdapter();
                    }
                });
        finalGroupDelete.setGroupMember(finalGroupDelete.getGroupMember().replace(user.getEmail(), ""));
        groupReference.child(id).removeValue();
        if (permanent_deleleted[0]) updateGroupToMember(finalGroupDelete);
        setGroupAdapter();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof GroupAdapter.ViewHolder) {
            ((GroupAdapter.ViewHolder) viewHolder).foreground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GroupAdapter.ViewHolder) viewHolder).background.setVisibility(View.VISIBLE);
                }
            });
            Group deletedGroup = groupArrayList.get(viewHolder.getAdapterPosition());

            int index = viewHolder.getAdapterPosition();
            groupAdapter.removeItem(index);
            Snackbar snackbar = Snackbar.make(rootView, "Delete", 5000);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupAdapter.undoItem(deletedGroup, index);
                }
            }).show();
            deleteGroup(deletedGroup.getGroupID());
        }
    }
}
