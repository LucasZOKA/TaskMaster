package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lucaszoka.taskmaster_v2.model.Group;

import org.w3c.dom.Text;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder>{
    private Context mContext;
    private List<Group> groupList;

    Menu menu;

    SharedPreferences sPref;

    public GroupAdapter(Context mContext, List<Group> groupList) {
        this.mContext = mContext;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row,parent,false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        sPref = mContext.getSharedPreferences("SPX",MODE_PRIVATE);

        holder.groupTitle.setText(groupList.get(position).getName());
        holder.groupSize.setText(String.valueOf(groupList.get(position).getMembers().size()));

        holder.groupColorCard.setBackgroundColor(groupList.get(holder.getAdapterPosition()).getgColor());

        holder.groupCard.setOnLongClickListener(view -> {
            Fragment newFragment = new GroupDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("group",groupList.get(holder.getAdapterPosition()));
            newFragment.setArguments(bundle);

            FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
            // Replace whatever is in the fragment_container view with this fragment
            transaction.replace(R.id.navHostFragment, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

            return false;
        });

        holder.groupCard.setOnClickListener(view -> {

            ((MainActivity)mContext).refreshMenu(groupList.get(holder.getAdapterPosition()).getName(), groupList.get(holder.getAdapterPosition()).getgColor());
            ((MainActivity)mContext).updateC();


            SharedPreferences.Editor editor = sPref.edit();
            editor.putString("MGName", groupList.get(holder.getAdapterPosition()).getName());
            editor.putString("MGID", groupList.get(holder.getAdapterPosition()).getId());
            editor.putInt("MGColor", groupList.get(holder.getAdapterPosition()).getgColor());
            editor.apply();

            Toast.makeText(mContext, "grupo atual: " + groupList.get(holder.getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

class GroupViewHolder extends RecyclerView.ViewHolder {
    TextView groupTitle;
    TextView groupSize;
    CardView groupCard;
    CardView groupColorCard;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        groupTitle = itemView.findViewById(R.id.groupTitle);
        groupSize = itemView.findViewById(R.id.tv_groupSize);
        groupCard = itemView.findViewById(R.id.groupCard);
        groupColorCard = itemView.findViewById(R.id.groupCardColor);
    }

}
