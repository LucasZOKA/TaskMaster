package com.lucaszoka.taskmaster_v2;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RewardAdapter extends RecyclerView.Adapter<RewardViewHolder> {
    private Context context;
    private List<Reward> rewardList;
    DatabaseReference dbRefReward, dbRefUser;
    User user;
    SharedPreferences sPref;
    int mPosition;

    public RewardAdapter(Context context, List<Reward> rewardList) {
        this.context = context;
        this.rewardList = rewardList;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_row,parent,false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        mPosition = holder.getAdapterPosition();
        Reward current_reward = rewardList.get(mPosition);
        sPref = context.getSharedPreferences("SPX", MODE_PRIVATE);
        dbRefReward = FirebaseDatabase.getInstance().getReference("rewards").child(rewardList.get(position).getId());
        dbRefUser= FirebaseDatabase.getInstance().getReference("users");
        dbRefUser.child(sPref.getString("uID",null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Map<String, Integer> uPoints = new HashMap<>();
                uPoints = user.getPoints();

                if(current_reward.getGroupID() != null){
                    if (uPoints.containsKey(current_reward.getGroupID())){
                        if(uPoints.get(current_reward.getGroupID()) < current_reward.getPoints()){
                            holder.reward_btn.setVisibility(View.GONE);
                        }
                    } else {
                        holder.reward_btn.setVisibility(View.GONE);
                    }
                } else {
                    if(uPoints.get("default") < current_reward.getPoints()){
                        holder.reward_btn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.reward_title_tv.setText(current_reward.getTitle());
        holder.reward_points_tv.setText(String.valueOf(current_reward.getPoints()));
        holder.reward_mcv.setCardBackgroundColor(current_reward.getColorRef());
        holder.reward_img_iv.setImageResource(current_reward.getIconRef());


        holder.reward_mcv.setOnLongClickListener(view -> {
            Fragment newFragment = new RewardDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("reward",current_reward);
            newFragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
            return false;
        });

        holder.reward_btn.setOnClickListener(view -> {
            confirmReward(current_reward, user, mPosition);
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    void confirmReward(Reward reward, User user, int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Resgatar recompensa ?");
        builder.setMessage("Tem certeza que deseja adquirir a recompensa " + reward.getTitle() + " ?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {

            dbRefReward = FirebaseDatabase.getInstance().getReference("rewards").child(reward.getId());

            Map<String, Integer> uPoints = new HashMap<>();
            uPoints = user.getPoints();
            Log.d("USER POINTS", "points =" + uPoints);

            if(reward.getGroupID() != null){
                uPoints.put(reward.getGroupID(), uPoints.get(reward.getGroupID()) - reward.getPoints());
            } else {
                uPoints.put("default", uPoints.get("default") - reward.getPoints());
            }

            dbRefUser.child(sPref.getString("uID",null)).child("points").setValue(uPoints);

            if(reward.getIs_permanent() == 0) {
                dbRefReward.removeValue();
                this.notifyItemRemoved(position);
            }

            ((MainActivity) context).sendEmail(reward.getEmail(), user.getEmail(), reward.getTitle(), 1, false);

        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {

        });
        builder.create().show();
    }
}



class RewardViewHolder extends RecyclerView.ViewHolder{
    TextView reward_title_tv,reward_points_tv;
    ImageView reward_img_iv;
    MaterialCardView reward_mcv;
    MaterialButton reward_btn;

    public RewardViewHolder(@NonNull View itemView) {
        super(itemView);

        reward_title_tv = itemView.findViewById(R.id.reward_title);
        reward_points_tv = itemView.findViewById(R.id.reward_points);
        reward_img_iv = itemView.findViewById(R.id.rewardCardImg);
        reward_mcv = itemView.findViewById(R.id.rewardCard);
        reward_btn = itemView.findViewById(R.id.btn_get);

    }
}
