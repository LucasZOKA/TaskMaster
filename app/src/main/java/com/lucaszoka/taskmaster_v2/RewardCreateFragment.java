package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.IconAdapter;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardCreateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText title, description, points;
    Spinner sGroup;
    AlertDialog alert;
    CheckBox permanent;
    Context mContext;
    Button saveReward_btn;
    SharedPreferences sPref;
    DatabaseReference dbRef;
    DatabaseReference dbRefUser;
    List<String> groupNames, groupIDs;
    String sGroupID;
    HashMap<String,Boolean> uGroups;
    ImageView rewardImage;

    ImageView colorsBtn;

    Reward reward;
    User user;

    public RewardCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardCreateFragment newInstance(String param1, String param2) {
        RewardCreateFragment fragment = new RewardCreateFragment();
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
        mContext = this.getContext();
        sPref = mContext.getSharedPreferences("SPX",MODE_PRIVATE);

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
        View view = inflater.inflate(R.layout.fragment_reward_create, container, false);

        saveReward_btn = view.findViewById(R.id.saveReward_btn);
        title = view.findViewById(R.id.ed_title);
        description = view.findViewById(R.id.ed_description);
        points = view.findViewById(R.id.ed_points);
        permanent = view.findViewById(R.id.cbPermanent);
        sGroup = view.findViewById(R.id.spin_Group);
        rewardImage = view.findViewById(R.id.rewardImage);
        colorsBtn = view.findViewById(R.id.colorPicker_btn);
        groupNames.add("Nenhum");

        int[] iconsA =  new int[]{
                R.drawable.ic_r_art,
                R.drawable.ic_r_bookshelf,
                R.drawable.ic_r_cart,
                R.drawable.ic_r_computer,
                R.drawable.ic_r_fashion,
                R.drawable.ic_r_filmreel,
                R.drawable.ic_r_gamecontroller,
                R.drawable.ic_r_money,
                R.drawable.ic_r_plane,
                R.drawable.ic_r_present,
                R.drawable.ic_r_skateboard,
                R.drawable.ic_r_tablet,
                R.drawable.ic_r_tv,
                R.drawable.ic_r_shop,
                R.drawable.ic_r_volume

        };
        //iconsA = getResources().getIntArray(R.array.iconsList);
        //Log.d("IAdapter", "R,array: " + R.array.iconsList);
        Log.d("IAdapter", "Lista de Refs: " + iconsA);

        ArrayList<Integer> iconsList = new ArrayList<>();
        for (int j : iconsA) {
            iconsList.add(j);
        }


        int[] colorsA = getResources().getIntArray(R.array.colorsList);

        ArrayList<Integer> colorsList = new ArrayList<>();
        for (int i = 0; i < colorsA.length; i++){
            colorsList.add(colorsA[i]);
        }

        Dialog dialog = new Dialog(mContext);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupNames);
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
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, groupNames);
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
        rewardImage.setImageResource(R.drawable.ic_r_present);
        rewardImage.setTag(R.drawable.ic_r_present);

        colorsBtn.setBackgroundColor(mContext.getColor(R.color.blue_800));
        colorsBtn.setTag(mContext.getColor(R.color.blue_800));

        rewardImage.setOnClickListener(view1 -> {
            selectIcon(iconsList, rewardImage);
        });

        colorsBtn.setOnClickListener( view1 -> {
            selectColor(colorsList, colorsBtn);
        });

        saveReward_btn.setOnClickListener(view1 -> {
            Log.d("Reward", "icone: "+ rewardImage.getTag());
            uploadReward();
            // Create new fragment and transaction
            Fragment newFragment = new RewardListFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
            transaction.commit();
        });


        return view;
    }

    //asudaudhad
    void selectIcon(ArrayList<Integer> iconArrayList, ImageView imgV){
        GridView gridView = new GridView(mContext);
        IconAdapter adapter = new IconAdapter(mContext, iconArrayList, imgV, alert);

        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setSelector(R.color.example_4_grey);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(gridView);
        builder.setTitle("Selecione um Icone");
        alert = builder.create();
        adapter.resetAlert(alert);
        alert.show();
        //builder.show();
    }

    void selectColor(ArrayList<Integer> colorArrayList, ImageView colorBtn){
        GridView gridView = new GridView(mContext);
        ColorAdapter adapter = new ColorAdapter(mContext, colorArrayList, colorBtn, alert);

        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setSelector(R.color.example_4_grey);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(gridView);
        builder.setTitle("Selecione uma cor");
        alert = builder.create();
        adapter.resetAlert(alert);
        alert.show();
    }
    private void uploadReward(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("rewards");


        final String id = dbRef.push().getKey();
        String rewardTitle = title.getText().toString().trim();
        String rewardDescription = description.getText().toString().trim();
        int rewardPoints = Integer.parseInt(points.getText().toString().trim());
        int iconRef = (int) rewardImage.getTag();
        int colorRef = (int) colorsBtn.getTag();

        int rewardPermanent;
        if(permanent.isChecked()){
            rewardPermanent = 1;
        }else{
            rewardPermanent = 0;
        }

        if(!sGroup.getSelectedItem().equals("Nenhum")){
            int sPosition = (sGroup.getSelectedItemPosition() - 1);
            sGroupID = groupIDs.get(sPosition);
            reward = new Reward(id,rewardTitle,rewardDescription,rewardPoints,rewardPermanent,sPref.getString("email",null), sGroupID, iconRef, colorRef);
        } else {
            reward = new Reward(id,rewardTitle,rewardDescription,rewardPoints,rewardPermanent,sPref.getString("email",null), iconRef, colorRef);
        }

        dbRef.child(id).setValue(reward).addOnSuccessListener(unused ->
                        Toast.makeText(mContext, "reward uploaded", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(mContext, "error on reward upload", Toast.LENGTH_SHORT).show());
    }

    public interface MyCallback {
        void onCallback(List<String> values);
    }

    public void getLeaderGroupIds(RewardCreateFragment.MyCallback callback){

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
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }
}