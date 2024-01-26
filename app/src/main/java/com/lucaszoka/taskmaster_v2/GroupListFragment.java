package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Group;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FloatingActionButton createGroup_fab, exitGroup_fab;
    RecyclerView recyclerView;
    GroupAdapter groupAdapter;
    List<Group> groupList;
    List<String> groupIds;
    DatabaseReference dbRef, dbRefGroup;
    ValueEventListener valueEventListener;
    SharedPreferences sPref;
    String userEmail;
    User user;

    public GroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance(String param1, String param2) {
        GroupListFragment fragment = new GroupListFragment();
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
        user = new User();
        groupList = new ArrayList<>();
        groupIds = new ArrayList<>();
        groupAdapter = new GroupAdapter(bContext, groupList);
        dbRefGroup = FirebaseDatabase.getInstance().getReference("groups");
        dbRef = FirebaseDatabase.getInstance().getReference("users").child(sPref.getString("uID",null)).child("groups");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        createGroup_fab = view.findViewById(R.id.createGroup_btn);
        exitGroup_fab = view.findViewById(R.id.exitGroup_btn);
        recyclerView = view.findViewById(R.id.rv_group_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(bContext, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(groupAdapter);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                Log.d("USER INFO", "pegando grupos");
                Map uGroups = (Map) snapshot.getValue();
                if(uGroups != null){
                    Log.d("USER INFO", "grupos: " + uGroups);
                    groupIds.clear();
                    groupIds.addAll(uGroups.keySet());
                }
                groupList.clear();
                Log.d("USER INFO", "procurando grupos");
                for (int i = 0; i < groupIds.size(); i++) {
                    String id = groupIds.get(i);
                    Log.d("USER INFO", "grupo: " + id);
                    dbRefGroup.child(id).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("USER INFO", "add group pra lista");
                            groupList.add(snapshot.getValue(Group.class));
                            //isso ta funfando
                            Log.d("USER INFO", "tamanho da lista: " + groupList.size());

                            
                            //!!!notify n ta funfando, ver como resolver!!!
                            groupAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        createGroup_fab.setOnClickListener(view1 -> {
            // Create new fragment and transaction
            Fragment newFragment = new GroupCreateFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        });

        exitGroup_fab.setOnClickListener(view1 -> {
            ((MainActivity)bContext).exitGroupDialog();
        });

        return view;
    }

    @Override
    public void onResume() {
        reloadAdapter();
        super.onResume();
    }

    void reloadAdapter(){
        groupAdapter.notifyDataSetChanged();
    }
}