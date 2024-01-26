package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.Task;
import com.lucaszoka.taskmaster_v2.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskCreateFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int points;
    EditText title, description, edDateEnd;
    Spinner sGroup, sPriority, sCategory;
    LinearLayout btnDataEnd;
    Button saveTask_btn;
    SharedPreferences sPref;
    DatabaseReference dbRef;
    DatabaseReference dbRefUser;
    List<String> groupNames, groupIDs;
    String sGroupID, priority;
    HashMap<String,Boolean> uGroups;

    Task task;
    User user;

    public TaskCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskCreateFragment newInstance(String param1, String param2) {
        TaskCreateFragment fragment = new TaskCreateFragment();
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

        groupIDs = new ArrayList<>();
        groupNames = new ArrayList<>();
        uGroups = new HashMap<>();


        dbRefUser = FirebaseDatabase.getInstance().getReference("users");
        dbRef = FirebaseDatabase.getInstance().getReference("groups");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_create, container, false);

        saveTask_btn = view.findViewById(R.id.saveTask_btn);
        title = view.findViewById(R.id.ed_title);
        description = view.findViewById(R.id.ed_description);
        edDateEnd = view.findViewById(R.id.ed_DateEnd);
        sCategory = view.findViewById(R.id.spin_Category);
        sPriority = view.findViewById(R.id.spin_Priority);
        sGroup = view.findViewById(R.id.spin_Group);
        btnDataEnd = view.findViewById(R.id.btn_dataEnd);

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
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                                bContext,
                                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                                groupNames);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sGroup.setAdapter(adapter);
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

        saveTask_btn.setOnClickListener(view1 -> {
            uploadTask();
            // Create new fragment and transaction
            Fragment newFragment = new TaskListFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
            transaction.commit();
        });

        return view;
    }

    private void uploadTask(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("tasks");

        final String id = dbRef.push().getKey();
        String taskTitle = title.getText().toString().trim();
        String taskDescription = description.getText().toString().trim();
        String category = sCategory.getSelectedItem().toString().trim();
        String dateEnd =  edDateEnd.getText().toString().trim();

        if(!sGroup.getSelectedItem().equals("Nenhum")){
            int sPosition = (sGroup.getSelectedItemPosition() - 1);
            sGroupID = groupIDs.get(sPosition);

            task = new Task(points, id, taskTitle, category, taskDescription, priority, dateEnd, sPref.getString("email",null), sGroupID);
        } else {
            task = new Task(points, id, taskTitle, category, taskDescription, priority, dateEnd, sPref.getString("email",null));
        }

        dbRef.child(id).setValue(task).addOnSuccessListener(unused ->
                        Toast.makeText(bContext, "task uploaded", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(bContext, "error on reward upload", Toast.LENGTH_SHORT).show());
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
            dbRef.child(ids).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    groupNames.add(snapshot.child("name").getValue(String.class));
                    Log.d("cFIREBASE INFO", "groupNames dentro da busca: " + groupNames);
                    callback.onCallback(groupNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void showDatePicker(){
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data final").build();

        materialDatePicker.addOnPositiveButtonClickListener(selection ->{
            String mDate = materialDatePicker.getHeaderText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
            LocalDate date = LocalDate.parse(mDate, formatter);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            System.out.println(date.format(formatter2));
            mDate = date.format(formatter2);

            edDateEnd.setText("" + mDate);

        });

        materialDatePicker.show(getParentFragmentManager(), "TAG");
    }
}