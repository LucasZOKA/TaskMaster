package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
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
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardListFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FloatingActionButton createReward_fab;
    ViewPager2 viewPager2;
    RewardAdapter rewardAdapter;
    List<Reward> rewardList;
    DatabaseReference dbRef;
    ValueEventListener valueEventListener;
    SharedPreferences sPref;
    String userEmail;
    public RewardListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardListFragment newInstance(String param1, String param2) {
        RewardListFragment fragment = new RewardListFragment();
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
        rewardList = new ArrayList<>();

        rewardAdapter = new RewardAdapter(this.getContext(), rewardList);
        dbRef = FirebaseDatabase.getInstance().getReference("rewards");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_reward_list, container, false);

       createReward_fab = view.findViewById(R.id.createReward_btn);
       viewPager2 = view.findViewById(R.id.vp_reward_list);
       viewPager2.setAdapter(rewardAdapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        if(sPref.contains("MGName")){
            valueEventListener = dbRef.orderByChild("groupID").equalTo(sPref.getString("MGID",null)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    rewardList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Reward reward = itemSnapshot.getValue(Reward.class);
                        rewardList.add(reward);
                    }
                    rewardAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {

            valueEventListener = dbRef.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    rewardList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Reward reward = itemSnapshot.getValue(Reward.class);
                        rewardList.add(reward);
                    }
                    rewardAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

       createReward_fab.setOnClickListener(view1 -> {
           // Create new fragment and transaction
           Fragment newFragment = new RewardCreateFragment();
           FragmentTransaction transaction = getParentFragmentManager().beginTransaction().setReorderingAllowed(true);

            // Replace whatever is in the fragment_container view with this fragment
           transaction.replace(R.id.navHostFragment, newFragment);

            // Commit the transaction
           transaction.commit();
        });


       return view;
    }
}