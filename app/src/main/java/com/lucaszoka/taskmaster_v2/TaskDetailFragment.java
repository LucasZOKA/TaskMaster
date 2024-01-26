package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.Task;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskDetailFragment extends BaseFragment {

    TextView tvTitle, tvDescription, tvPriority, tvCategory, tvDateEnd, tvGroup;
    Button btnEdit;
    Task task;
    String taskId,taskEmail, taskGroupId, taskGroupName;
    SharedPreferences sPref;
    DatabaseReference dbRefGroup;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskDetailFragment newInstance(String param1, String param2) {
        TaskDetailFragment fragment = new TaskDetailFragment();
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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            task = (Task) bundle.getSerializable("task");
        }

        sPref = bContext.getSharedPreferences("SPX",MODE_PRIVATE);

        taskId = task.getId();
        Log.d("TASKINFO", taskId);
        taskEmail = task.getEmail();
        taskGroupId = task.getGroupID();
        Log.d("TASKINFO","task groupid: " + taskGroupId);
        dbRefGroup = FirebaseDatabase.getInstance().getReference("groups");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        tvPriority = view.findViewById(R.id.tv_Priority);
        tvCategory = view.findViewById(R.id.tv_Category);
        tvGroup = view.findViewById(R.id.tv_Group);
        tvDateEnd= view.findViewById(R.id.tv_DateEnd);
        btnEdit = view.findViewById(R.id.editTask_btn);

        tvTitle.setText(task.getTitle());
        tvDescription.setText(task.getDescription());
        tvPriority.setText(task.getDifficulty());
        tvCategory.setText(task.getCategory());
        tvDateEnd.setText(task.getDateEnd());

        if(taskGroupId != null){
        dbRefGroup.child(taskGroupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskGroupName = snapshot.child("name").getValue(String.class);
                tvGroup.setText(taskGroupName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }else{
            tvGroup.setText("Nenhum");
        }


        if(Objects.equals(taskEmail, sPref.getString("email", null))){
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(view1 -> {
                Fragment newFragment = new TaskUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("task",task);
                newFragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)bContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.navHostFragment, newFragment);

                // Commit the transaction
                transaction.commit();
            });
        }else{
            btnEdit.setVisibility(View.GONE);
        }

        return view;
    }
}