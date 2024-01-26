package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Group;
import com.lucaszoka.taskmaster_v2.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupCreateFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tvMembers;
    EditText edTitle, edMemberEmail;
    AlertDialog alert;
    Button btnUploadGroup;
    ImageButton btnAddMember;
    SharedPreferences sPref;
    DatabaseReference dbRef;
    List<String> members,membersId;
    ImageView ivGroupColor;
    User user;
    int gColor;

    public GroupCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupCreateFragment newInstance(String param1, String param2) {
        GroupCreateFragment fragment = new GroupCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        sPref = bContext.getSharedPreferences("SPX",MODE_PRIVATE);
        members = new ArrayList<>();
        membersId = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        dbRef.orderByChild("id").equalTo(sPref.getString("uID",null)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Log.d("INFO USER TASK", "user name: " + user.getName());
                Log.d("INFO USER TASK", "user: " + user.getPoints());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_create, container, false);

        int[] colorsA = getResources().getIntArray(R.array.colorsList);

        ArrayList<Integer> colorsList = new ArrayList<>();
        for (int i = 0; i < colorsA.length; i++){
            colorsList.add(colorsA[i]);
        }

        tvMembers = view.findViewById(R.id.tvMemberList);
        edTitle = view.findViewById(R.id.groupName);
        edMemberEmail = view.findViewById(R.id.edMemberEmail);
        btnAddMember = view.findViewById(R.id.btnAddMember);
        btnUploadGroup = view.findViewById(R.id.btnUploadGroup);
        ivGroupColor = view.findViewById(R.id.colorPicker_btn);



        btnAddMember.setOnClickListener(view1 -> {
            String member = edMemberEmail.getText().toString().trim().toLowerCase();
            if(member.equals("")){
                Toast.makeText(bContext, "Adicione um email", Toast.LENGTH_SHORT).show();
            }else{
                dbRef.orderByChild("email").equalTo(member).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot ds : snapshot.getChildren()){
                                String mEmail = ds.child("email").getValue(String.class);
                                if(!mEmail.equals(sPref.getString("email",null))){
                                    addMember(member);
                                    membersId.add(ds.child("id").getValue(String.class));
                                }else{
                                    Toast.makeText(bContext, "Voce não pode adicionar seu proprio email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText(bContext, "usuario não existe :(", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        ivGroupColor.setBackgroundColor(bContext.getColor(R.color.blue_800));
        ivGroupColor.setTag(bContext.getColor(R.color.blue_800));

        ivGroupColor.setOnClickListener( view1 -> {
            selectColor(colorsList, ivGroupColor);
        });

        btnUploadGroup.setOnClickListener(view1 -> {
            membersId.add(sPref.getString("uID",null));
            uploadGroup(membersId);

            Fragment newFragment = new GroupListFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
            transaction.commit();
        });

        return view;
    }

    public void addMember(String member){
        members.add(member);
        StringBuilder currentMembers = new StringBuilder("Membros: \n");
        for(int i = 0 ; i < members.size() ; i++){
            currentMembers.append(members.get(i)).append(" \n");
        }
        tvMembers.setText(currentMembers.toString());
        edMemberEmail.getText().clear();
    }

    public void uploadGroup(List<String> members){
        DatabaseReference dbRefGroup = FirebaseDatabase.getInstance().getReference("groups");
        final String id = dbRefGroup.push().getKey();
        String groupName = edTitle.getText().toString().trim();
        HashMap<String, Boolean> groupMembers = new HashMap<>();
        for(int i = 0; i<members.size(); i++){
            //mudar pra true apenas o lider
            if(members.get(i).equals(sPref.getString("uID",null))){
                groupMembers.put(members.get(i),true);
            } else {
                groupMembers.put(members.get(i),false);
            }

        }
        String creatorEmail = sPref.getString("email",null);
        int colorRef = (int) ivGroupColor.getTag();

        Group group = new Group(id,groupName,creatorEmail,groupMembers,colorRef);
        dbRefGroup.child(id).setValue(group).addOnSuccessListener(unused -> Toast.makeText(bContext, "group uploaded", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(bContext, "error on upload group", Toast.LENGTH_SHORT).show());
        updateUser(members,id);

    }
    public void updateUser(List<String> members, String groupId){

        for(int i = 0 ; i < members.size() ; i++){
            String id = members.get(i);
            dbRef.child(members.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,Boolean> groups = new HashMap<>();
                    if(snapshot.child("groups").getValue() != null){
                        groups = (HashMap<String, Boolean>) snapshot.child("groups").getValue();
                        if(id.equals(sPref.getString("uID",null))){
                            groups.put(groupId,true);
                        } else {
                            groups.put(groupId,false);
                        }

                    }else{
                        if(id.equals(sPref.getString("uID",null))){
                            groups.put(groupId,true);
                        } else {
                            groups.put(groupId,false);
                        }
                    }
                    dbRef.child(id).child("groups").setValue(groups);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void selectColor(ArrayList<Integer> colorArrayList, ImageView colorBtn){
        GridView gridView = new GridView(bContext);
        ColorAdapter adapter = new ColorAdapter(bContext, colorArrayList, colorBtn, alert);

        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setSelector(R.color.example_4_grey);

        AlertDialog.Builder builder = new AlertDialog.Builder(bContext);
        builder.setView(gridView);
        builder.setTitle("Selecione uma cor");
        alert = builder.create();
        adapter.resetAlert(alert);
        alert.show();
    }
}