package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucaszoka.taskmaster_v2.model.IconAdapter;
import com.lucaszoka.taskmaster_v2.model.Reward;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardUpdateFragment extends BaseFragment {
    EditText edTitle, edDescription, edPoints;

    Button btnUpdate, btnDelete;
    Context mContext;
    AlertDialog alert;
    String rewardTitle,rewardDescription,rewardId,rewardEmail;
    int rewardPoints,rewardIsPermanent, iconRef, colorRef;
    DatabaseReference dbRef;
    Reward mReward;
    CheckBox cbPermanent;
    SharedPreferences sPref;
    ImageView ivRewardIcon, ivRewardColor;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RewardUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardUpdateFragment newInstance(String param1, String param2) {
        RewardUpdateFragment fragment = new RewardUpdateFragment();
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
            mReward = (Reward) bundle.getSerializable("reward");
        }

        mContext = this.getContext();

        sPref = mContext.getSharedPreferences("SPX",MODE_PRIVATE);
        rewardIsPermanent = mReward.getIs_permanent();
        dbRef = FirebaseDatabase.getInstance().getReference("rewards").child(mReward.getId());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reward_update, container, false);

        edTitle = view.findViewById(R.id.ed_title);
        edDescription = view.findViewById(R.id.ed_description);
        edPoints = view.findViewById(R.id.ed_points);
        cbPermanent = view.findViewById(R.id.cbPermanent);
        btnUpdate = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        ivRewardIcon = view.findViewById(R.id.rewardImage);
        ivRewardColor = view.findViewById(R.id.colorPicker_btn);

        edTitle.setText(mReward.getTitle());
        edDescription.setText(mReward.getDescription());
        edPoints.setText(String.valueOf(mReward.getPoints()));
        ivRewardIcon.setImageResource(mReward.getIconRef());
        ivRewardIcon.setTag(mReward.getIconRef());
        ivRewardColor.setBackgroundColor(mReward.getColorRef());
        ivRewardColor.setTag(mReward.getColorRef());

        if (rewardIsPermanent == 1) {
            cbPermanent.setChecked(true);
        } else {
            cbPermanent.setChecked(false);
        }

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
        ArrayList<Integer> iconsList = new ArrayList<>();
        for (int i = 0; i < iconsA.length; i++){
            iconsList.add(iconsA[i]);
        }

        int[] colorsA = getResources().getIntArray(R.array.colorsList);
        ArrayList<Integer> colorsList = new ArrayList<>();
        for (int i = 0; i < colorsA.length; i++){
            colorsList.add(colorsA[i]);
        }

        ivRewardIcon.setOnClickListener(view1 -> {
            selectIcon(iconsList, ivRewardIcon);
        });

        ivRewardColor.setOnClickListener( view1 -> {
            selectColor(colorsList, ivRewardColor);
        });

        btnUpdate.setOnClickListener(view1 -> {
            if (cbPermanent.isChecked()){
                rewardIsPermanent = 1;
            } else {
                rewardIsPermanent = 0;
            }
            updateData();
        });
        if(mReward.getEmail().equals(sPref.getString("email",null))) {
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(view1 -> {
            dbRef.removeValue();

            Fragment newFragment = new RewardListFragment();
            FragmentTransaction transaction = ((AppCompatActivity)bContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
            transaction.commit();
            });
        }else {
            btnDelete.setVisibility(View.GONE);
        }

        return view;
    }

    public void updateData(){
        rewardTitle = edTitle.getText().toString().trim();
        rewardDescription = edDescription.getText().toString().trim();
        rewardPoints = Integer.parseInt(edPoints.getText().toString().trim());
        rewardEmail = mReward.getEmail();
        rewardId = mReward.getId();
        iconRef = (int) ivRewardIcon.getTag();
        colorRef = (int) ivRewardColor.getTag();
        Reward reward = new Reward(
                rewardId,
                rewardPoints,
                rewardTitle,
                rewardDescription,
                rewardEmail,
                rewardIsPermanent,
                iconRef,
                colorRef);
        dbRef.child("title").setValue(rewardTitle);
        dbRef.child("description").setValue(rewardDescription);
        dbRef.child("points").setValue(rewardPoints);
        dbRef.child("is_permanent").setValue(rewardIsPermanent);
        dbRef.child("iconRef").setValue(iconRef);
        dbRef.child("colorRef").setValue(colorRef);

        Fragment newFragment = new RewardDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("reward",reward);
        newFragment.setArguments(bundle);

        FragmentTransaction transaction = ((AppCompatActivity)bContext)
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true);
        // Replace whatever is in the fragment_container view with this fragment
        transaction.replace(R.id.navHostFragment, newFragment);

        // Commit the transaction
        transaction.commit();
    }

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
}