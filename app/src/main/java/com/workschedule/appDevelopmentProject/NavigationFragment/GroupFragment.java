package com.workschedule.appDevelopmentProject.NavigationFragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.LinearLayout;
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
import com.workschedule.appDevelopmentProject.MainActivity;
import com.workschedule.appDevelopmentProject.R;
import com.workschedule.appDevelopmentProject.User;
import com.workschedule.appDevelopmentProject.UserAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private LinearLayout memberViewList;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://wsche-appdevelopmentproject-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference userReference = database.getReference("User");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference groupReference = userReference.child(uid).child("Group");
    private ConstraintLayout rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        memberViewList = v.findViewById(R.id.lv_members);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        setGroupAdapter();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initWidgets(view);
        buttonAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDialogAddGroup(); }
        });

        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group.groupArrayList.clear();
                for (DataSnapshot groupPlanSnapshot: snapshot.getChildren()) {
                    String groupDate = groupPlanSnapshot.child("groupDate").getValue(String.class);
                    String groupName = groupPlanSnapshot.child("groupName").getValue(String.class);
                    String groupTime = groupPlanSnapshot.child("groupTime").getValue(String.class);
                    String groupMember = groupPlanSnapshot.child("groupMember").getValue(String.class);
                    String groupMaster = getGroupMater(groupMember);
                    String groupKey = groupPlanSnapshot.getKey();
                    Group group = new Group(groupKey, groupName, groupDate, groupTime, groupMember, groupMaster);
                    Group.groupArrayList.add(group);
                }
                setGroupAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User.userArrayList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String userName = userSnapshot.child("Name").getValue(String.class);
                    String userID = userSnapshot.getKey();
                    String userAvtString = userSnapshot.child("Avt").getValue(String.class);
                    User member;
                    if (userAvtString == null) {
                        member = new User(userID, userName, userEmail, null);
                    } else {
                        Uri userAvt = Uri.parse(userAvtString);
                        member = new User(userID, userName, userEmail, userAvt);
                    }
                    User.userArrayList.add(member);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
                String groupMaster = user.getEmail();
                Group group = new Group(groupKey, groupName, groupDate, groupTime, groupMember, groupMaster);
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
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(group.getGroupMember());
        ArrayList<String> members = new ArrayList<>();
        while (matcher.find()) {
            members.add(matcher.group());
        }
        Set<String> set = new HashSet<>(members);
        members.clear();
        members.addAll(set);
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

    public void openDialogEditGroup(String groupKey, String groupName, String groupDate, String groupTime, String groupMember, String groupMaster) {
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
        if (!groupMaster.equals(user.getEmail())){
            groupMemET.setKeyListener(null);
            groupMemET.setBackgroundColor(Color.GRAY);
            groupMemET.setTextColor(Color.BLACK);
        }

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
                String groupMaster = getGroupMater(groupMember);
                Group group = new Group(groupKey, groupName, groupDate, groupTime, groupMember, groupMaster);
                groupReference.child(groupKey).setValue(group);
                for (int i = 0; i < Group.groupArrayList.size(); i++) {
                    Group groupItem = Group.groupArrayList.get(i);
                    if (groupItem.getGroupID().equals(groupKey)) {
                        groupItem.setGroupName(groupName);
                        groupItem.setGroupMember(groupMember);
                        updateGroupToMember(groupItem);
                        break;
                    }
                }
                setGroupAdapter();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public String getGroupMater(String groupMember){
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(groupMember);
        ArrayList<String> members = new ArrayList<>();
        while (matcher.find()) {
            members.add(matcher.group());
        }
        Set<String> set = new HashSet<>(members);
        members.clear();
        members.addAll(set);
        return members.get(0);
    }

    public void updateGroupToMember(@NonNull Group group) {
        Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b");
        Matcher matcher = pattern.matcher(group.getGroupMember());
        ArrayList<String> members = new ArrayList<>();
        while (matcher.find()) {
            members.add(matcher.group());
        }
        Set<String> set = new HashSet<>(members);
        members.clear();
        members.addAll(set);
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
        groupAdapter = new GroupAdapter(GroupFragment.this, Group.groupArrayList);
        groupRecyclerView.setAdapter(groupAdapter);
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
            Group deletedGroup = Group.groupArrayList.get(viewHolder.getAdapterPosition());

            int index = viewHolder.getAdapterPosition();
            groupAdapter.removeItem(index);
            Snackbar snackbar = Snackbar.make(rootView, "Đã thoát nhóm " + deletedGroup.getGroupName(), 5000);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletedGroup.setGroupMember(deletedGroup.getGroupMember() + " " + user.getEmail());
                    groupReference.child(deletedGroup.getGroupID()).setValue(deletedGroup);
                    updateGroupToMember(deletedGroup);
                    groupAdapter.undoItem(deletedGroup, index);
                }
            }).show();
            deletedGroup.setGroupMember(deletedGroup.getGroupMember().replace(user.getEmail(), ""));
            groupReference.child(deletedGroup.getGroupID()).removeValue();
            updateGroupToMember(deletedGroup);
        }
    }
}
