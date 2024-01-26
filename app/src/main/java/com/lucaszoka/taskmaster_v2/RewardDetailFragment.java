package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucaszoka.taskmaster_v2.model.Reward;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardDetailFragment extends Fragment {

    TextView tvTitle, tvDescription, tvPoints, tvPermanent;
    Button btnEdit;
    Reward reward;
    Context mContext;
    String rewardId,rewardEmail;
    SharedPreferences sPref;
    int rewardIsPermanent;
    ImageView ivRewardImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RewardDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardDetailFragment newInstance(String param1, String param2) {
        RewardDetailFragment fragment = new RewardDetailFragment();
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
            reward = (Reward) bundle.getSerializable("reward");
        }

        mContext = this.getContext();
        sPref = mContext.getSharedPreferences("SPX",MODE_PRIVATE);

        rewardId = reward.getId();
        rewardEmail = reward.getEmail();
        rewardIsPermanent = reward.getIs_permanent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reward_detail, container, false);

        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        tvPoints = view.findViewById(R.id.tv_points);
        btnEdit = view.findViewById(R.id.btn_Edit);
        tvPermanent = view.findViewById(R.id.tv_isPermanent);
        ivRewardImage = view.findViewById(R.id.rewardImage);


        tvTitle.setText(reward.getTitle());
        tvDescription.setText(reward.getDescription());
        tvPoints.setText(String.valueOf(reward.getPoints()));
        ivRewardImage.setImageResource(reward.getIconRef());

        if(rewardIsPermanent == 0){
            tvPermanent.setVisibility(View.INVISIBLE);
        }

        if(Objects.equals(rewardEmail, sPref.getString("email", null))){
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(view1 -> {
                Fragment newFragment = new RewardUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("reward",reward);
                newFragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
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