package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.lucaszoka.taskmaster_v2.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupDetailFragment extends BaseFragment {

    Button btnEditGroup;
    TextView tvName,tvCreator,tvMembers;
    Group group;
    SharedPreferences sPref;
    DatabaseReference dbRef;
    List<String> members;
    StringBuilder currentMembers;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public GroupDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupDetailFragment newInstance(String param1, String param2) {
        GroupDetailFragment fragment = new GroupDetailFragment();
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
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        currentMembers = new StringBuilder("");

        Bundle bundle = this.getArguments();
        if(bundle != null){
            group = (Group) bundle.getSerializable("group");
        }

        members.clear();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        btnEditGroup = view.findViewById(R.id.btnGroupEdit);
        tvName = view.findViewById(R.id.groupName);
        tvCreator = view.findViewById(R.id.groupCreator);
        tvMembers = view.findViewById(R.id.groupMembers);


        readNames(values -> {
            //checa se o members é do mesmo tamanho do grupo
            if(members.size() == group.getMembers().size()) {
                for (String member : members) {
                    Log.d("USER INFO", "add : " + member);
                    currentMembers.append(member).append("\n");
                    Log.d("USER INFO", "current members = " + currentMembers);
                }
            }
            tvMembers.setText(currentMembers);
        });

        Log.d("USER INFO", "final membro : " + members);

        tvName.setText(group.getName());
        tvCreator.setText(group.getCreatorEmail());


        /*if(!group.getCreatorEmail().equals(sPref.getString("email",null))){
            btnEditGroup.setVisibility(View.GONE);
        }*/
        //lembrar de fazer o editar no futuro
        btnEditGroup.setVisibility(View.GONE);

        return view;
    }

    public interface MyCallback {
        void onCallback(List<String> values);
    }
    public void readNames(MyCallback callback){
        for(String mID : group.getMembers().keySet()){
            dbRef.child(mID).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    members.add(snapshot.getValue(String.class));
                    Log.d("USER INFO", "membro : " + members);
                    callback.onCallback(members);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }
}