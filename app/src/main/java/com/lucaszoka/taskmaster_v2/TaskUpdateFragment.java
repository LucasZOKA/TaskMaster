package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.Task;
import com.lucaszoka.taskmaster_v2.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskUpdateFragment extends BaseFragment {

    //Terminar de puxar e editar grupo, puxar data

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int points, taskPoints;
    EditText title, description, edDateEnd;
    Spinner sGroup, sPriority, sCategory;
    LinearLayout btnDataEnd;
    Button editTask_btn, deleteTask_btn;
    SharedPreferences sPref;
    FirebaseDatabase dbInstance;
    DatabaseReference dbRefUser, dbRefGroup, dbRefTask;
    List<String> groupNames, groupIDs;
    String sGroupID, priority, gName;
    HashMap<String,Boolean> uGroups;

    String taskTitle, taskDescription, taskCategory, taskPriority, taskDateEnd, taskGroup, taskID, taskEmail;

    Task mTask;
    User user;

    public TaskUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskUpdateFragment newInstance(String param1, String param2) {
        TaskUpdateFragment fragment = new TaskUpdateFragment();
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
            mTask = (Task) bundle.getSerializable("task");
        }

        sPref = bContext.getSharedPreferences("SPX",MODE_PRIVATE);

        groupIDs = new ArrayList<>();
        groupNames = new ArrayList<>();
        uGroups = new HashMap<>();

        dbInstance = FirebaseDatabase.getInstance();


        dbRefUser = dbInstance.getReference("users");
        dbRefGroup = dbInstance.getReference("groups");
        dbRefTask = dbInstance.getReference("tasks").child(mTask.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_update, container, false);

        editTask_btn = view.findViewById(R.id.btn_edit);
        deleteTask_btn = view.findViewById(R.id.btn_delete);
        title = view.findViewById(R.id.ed_title);
        description = view.findViewById(R.id.ed_description);
        edDateEnd = view.findViewById(R.id.ed_DateEnd);
        sCategory = view.findViewById(R.id.spin_Category);
        sPriority = view.findViewById(R.id.spin_Priority);
        sGroup = view.findViewById(R.id.spin_Group);
        btnDataEnd = view.findViewById(R.id.btn_dataEnd);

        title.setText(mTask.getTitle());
        description.setText(mTask.getDescription());

        sCategory.setSelection(((ArrayAdapter)sCategory.getAdapter()).getPosition(mTask.getCategory()));
        sPriority.setSelection(((ArrayAdapter)sPriority.getAdapter()).getPosition(mTask.getDifficulty()));
        edDateEnd.setText(mTask.getDateEnd());

        groupNames.add("Nenhum");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(bContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sGroup.setAdapter(adapter);

        dbRefUser.child(sPref.getString("uID",null)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("groups").getValue() != null) {
                    Log.d("cFIREBASE INFO", "pegando Grupos");
                    uGroups = (HashMap<String, Boolean>) snapshot.child("groups").getValue();
                    Log.d("cFIREBASE INFO", "uGroups depois da pesquisa: " + uGroups);

                    getLeaderGroupIds(new MyCallback() {
                        @Override
                        public void onCallback(List<String> values) {

                            readGroupNames(new MyCallback() {
                                @Override
                                public void onCallback(List<String> values) {
                                    if(groupNames.size() == (groupIDs.size() + 1)) {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(bContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupNames);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sGroup.setAdapter(adapter);
                                        if (gName!=null) {
                                            sGroup.setSelection(((ArrayAdapter) sGroup.getAdapter()).getPosition(gName));
                                        } else {
                                            sGroup.setSelection(0);
                                        }
                                    }
                                }
                            });

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                priority = sPriority.getSelectedItem().toString().trim();
                switch (priority){
                    case "Nenhuma":
                        points = 0;
                        break;
                    case "Baixa":
                        points = 10;
                        break;
                    case "Média":
                        points = 50;
                        break;
                    case "Alta":
                        points = 100;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edDateEnd.setOnClickListener(view1-> showDatePicker());

        editTask_btn.setOnClickListener(view1 -> {
            updateData();
            /*// Create new fragment and transaction
            Fragment newFragment = new TaskListFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
            transaction.commit();*/
        });

        if(mTask.getEmail().equals(sPref.getString("email",null))) {
            deleteTask_btn.setVisibility(View.VISIBLE);
            deleteTask_btn.setOnClickListener(view1 -> {
                dbRefTask.removeValue();

                Fragment newFragment = new TaskListFragment();
                FragmentTransaction transaction = ((AppCompatActivity)bContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.navHostFragment, newFragment);

                // Commit the transaction
                transaction.commit();
            });
        }else {
            deleteTask_btn.setVisibility(View.GONE);
        }

        return view;
    }

    public void updateData(){
        Task task;
        taskTitle = title.getText().toString().trim();
        taskDescription = description.getText().toString().trim();
        taskCategory = sCategory.getSelectedItem().toString().trim();
        taskPriority = sPriority.getSelectedItem().toString().trim();
        taskDateEnd = edDateEnd.getText().toString().trim();
        taskPoints = points;
        taskEmail = mTask.getEmail();
        taskID = mTask.getId();
        taskGroup = mTask.getGroupID();


        if(!sGroup.getSelectedItem().equals("Nenhum")){
            int sPosition = (sGroup.getSelectedItemPosition() - 1);
            taskGroup = groupIDs.get(sPosition);

            dbRefTask.child("groupID").setValue(taskGroup);
            task = new Task(taskPoints, taskID, taskTitle, taskCategory, taskDescription, taskPriority, taskDateEnd, taskEmail, taskGroup);
        } else {
            taskGroup = "";
            dbRefTask.child("groupID").removeValue();
            task = new Task(taskPoints, taskID, taskTitle, taskCategory, taskDescription, taskPriority, taskDateEnd, taskEmail);
        }
        dbRefTask.child("title").setValue(taskTitle);
        dbRefTask.child("description").setValue(taskDescription);
        dbRefTask.child("points").setValue(taskPoints);
        dbRefTask.child("category").setValue(taskCategory);
        dbRefTask.child("difficulty").setValue(taskPriority);
        dbRefTask.child("dateEnd").setValue(taskDateEnd);

        Fragment newFragment = new TaskDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("task",task);
        newFragment.setArguments(bundle);

        FragmentTransaction transaction = ((AppCompatActivity)bContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
        // Replace whatever is in the fragment_container view with this fragment
        transaction.replace(R.id.navHostFragment, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    public interface MyCallback {
        void onCallback(List<String> values);
    }

    public void getLeaderGroupIds(MyCallback callback){

        Log.d("cFIREBASE INFO", "checando se é lider do grupo");
        for (String key : uGroups.keySet()){
            if(Boolean.TRUE.equals(uGroups.get(key))){
                Log.d("cFIREBASE INFO", "adicionando id do grupo no array");
                groupIDs.add(key);
                Log.d("cFIREBASE INFO", "groupIDS : " + groupIDs);
            }
        }
        Log.d("cFIREBASE INFO", "groupIDS final: " + groupIDs);
        callback.onCallback(groupIDs);

    }


    public void readGroupNames(MyCallback callback){
        for(String ids : groupIDs){
            dbRefGroup.child(ids).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(ids.equals(mTask.getGroupID())){

                    gName = "" + snapshot.child("name").getValue(String.class);
                    Log.d("cFIREBASE INFO", "nome do grupo: " + snapshot.child("name").getValue(String.class));

                    }
                    groupNames.add(snapshot.child("name").getValue(String.class));
                    Log.d("cFIREBASE INFO", "groupNames dentro da busca: " + groupNames);
                    Log.d("cFIREBASE INFO", "nome do grupo: " + gName + " Id do grupo: " + ids);
                    callback.onCallback(groupNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void showDatePicker(){
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data de inicio").build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->{
            edDateEnd.setText("" + materialDatePicker.getHeaderText());
        });

        materialDatePicker.show(getParentFragmentManager(), "TAG");
    }
}