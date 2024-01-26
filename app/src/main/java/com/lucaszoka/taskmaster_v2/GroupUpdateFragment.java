package com.lucaszoka.taskmaster_v2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.lucaszoka.taskmaster_v2.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupUpdateFragment extends BaseFragment {

    //Lembrar de no futuro fazer esse frag

    TextView tvMembers;
    EditText edTitle, edMemberEmail;
    ImageView btnColor;
    Button btnAddMember, btnEditGroup, btnDeleteGroup;
    SharedPreferences sPref;
    DatabaseReference dbRef;
    List<String> members,membersId;
    User user;
    AlertDialog alert;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupUpdateFragment newInstance(String param1, String param2) {
        GroupUpdateFragment fragment = new GroupUpdateFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_update, container, false);

        int[] colorsA = getResources().getIntArray(R.array.colorsList);

        ArrayList<Integer> colorsList = new ArrayList<>();
        for (int i = 0; i < colorsA.length; i++){
            colorsList.add(colorsA[i]);
        }

        tvMembers = view.findViewById(R.id.tvMemberList);
        edTitle = view.findViewById(R.id.groupName);
        edMemberEmail = view.findViewById(R.id.edMemberEmail);
        btnAddMember = view.findViewById(R.id.btnAddMember);
        btnEditGroup = view.findViewById(R.id.btnEditGroup);
        btnDeleteGroup = view.findViewById(R.id.btnDeleteGroup);
        btnColor = view.findViewById(R.id.colorPicker_btn);

        return view;
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