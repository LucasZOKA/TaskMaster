package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FloatingActionButton createTask_fab;
    RecyclerView recyclerView;
    List<Task> taskList;
    TaskAdapter taskAdapter;
    DatabaseReference dbRef;
    ValueEventListener valueEventListener;
    SharedPreferences sPref;
    String userEmail;

    public TaskListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
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

        sPref = getContext().getSharedPreferences("SPX",MODE_PRIVATE);
        userEmail = sPref.getString("email",null);
        taskList = new ArrayList<>();

        taskAdapter = new TaskAdapter(this.getContext(), taskList);
        dbRef = FirebaseDatabase.getInstance().getReference("tasks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        createTask_fab = view.findViewById(R.id.createTask_btn);
        recyclerView = view.findViewById(R.id.rv_task_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(bContext, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(taskAdapter);

        if(sPref.contains("MGName")){
            valueEventListener = dbRef.orderByChild("groupID").equalTo(sPref.getString("MGID",null)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    taskList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Task task = itemSnapshot.getValue(Task.class);
                        taskList.add(task);
                    }
                    taskAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {

            valueEventListener = dbRef.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    taskList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Task task = itemSnapshot.getValue(Task.class);
                        taskList.add(task);
                    }
                    taskAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        createTask_fab.setOnClickListener(view1 -> {
            // Create new fragment and transaction
            Fragment newFragment = new TaskCreateFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
            transaction.commit();
        });

        return  view;
    }
}