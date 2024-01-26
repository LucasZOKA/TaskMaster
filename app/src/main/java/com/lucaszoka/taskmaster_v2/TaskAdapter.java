package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaszoka.taskmaster_v2.model.Reward;
import com.lucaszoka.taskmaster_v2.model.Task;
import com.lucaszoka.taskmaster_v2.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder>{

    private Context mContext;

    Boolean isLate = false;
    String lateTime;
    private List<Task> taskList;
    DatabaseReference dbRefTask, dbRefUser;
    User user;

    Animation translate_anim;
    String[] categories,priorities;
    SharedPreferences sPref;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.mContext = context;
        this.taskList = taskList;
    }

    //mudando o adpater pra fazer ele trocar a lista quando pedir em outro lugar, no caso no calendario
    public void changeList(List<Task> list){
        this.taskList = list;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row,parent,false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        sPref = mContext.getSharedPreferences("SPX", MODE_PRIVATE);
        dbRefTask = FirebaseDatabase.getInstance().getReference("tasks").child(taskList.get(position).getId());
        dbRefUser= FirebaseDatabase.getInstance().getReference("users");
        dbRefUser.child(sPref.getString("uID",null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.task_titulo_txt.setText(taskList.get(position).getTitle());
        holder.task_desc_txt.setText(taskList.get(position).getDescription());
        holder.task_fim_txt.setText(taskList.get(position).getDateEnd());
        holder.task_pontos_txt.setText(String.valueOf(taskList.get(position).getPoints()));
        String cat = String.valueOf(taskList.get(position).getCategory());
        String pri = String.valueOf(taskList.get(position).getDifficulty());
        switch (cat){
            case "Nenhuma":
                holder.catIcon.setImageResource(R.drawable.ic_task);
                break;
            case "Educação":
                holder.catIcon.setImageResource(R.drawable.ic_book);
                break;
            case "Trabalho":
                holder.catIcon.setImageResource(R.drawable.ic_suitcase);
                break;
            case "Lazer":
                holder.catIcon.setImageResource(R.drawable.ic_tree);
                break;
            case "Domestico":
                holder.catIcon.setImageResource(R.drawable.ic_home);
                break;
        }

        switch (pri){
            case "Nenhuma":
                holder.priorityCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.nulo));
                holder.priorityIcon.setCardBackgroundColor(mContext.getResources().getColor(R.color.nulo));
                break;
            case "Baixa":
                holder.priorityCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.taskDifficulty_easyBG));
                holder.priorityIcon.setCardBackgroundColor(mContext.getResources().getColor(R.color.taskDifficulty_easy));
                break;
            case "Média":
                holder.priorityCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.taskDifficulty_mediumBG));
                holder.priorityIcon.setCardBackgroundColor(mContext.getResources().getColor(R.color.taskDifficulty_medium));
                break;
            case "Alta":
                holder.priorityCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.taskDifficulty_hardBG));
                holder.priorityIcon.setCardBackgroundColor(mContext.getResources().getColor(R.color.taskDifficulty_hard));
                break;
        }


        holder.mainLayout.setOnClickListener(view -> {

            int taskpoints = taskList.get(position).getPoints();
            String sTaskDate = taskList.get(position).getDateEnd();

            SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String sTargetDate = mFormatter.format(Calendar.getInstance().getTime());

            Date taskDate = null;
            Date targetDate;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                taskDate = format.parse(sTaskDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                targetDate = format.parse(sTargetDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Log.d("Calendario", "data da tarefa: "+taskDate + " Data final: " + targetDate);
            assert taskDate != null;
            if(taskDate.before(targetDate)){
                Log.d("calendario", "a tarefa expirou");
                Toast.makeText(mContext, "A tarefa expirou! Você ganhará metade dos pontos.", Toast.LENGTH_LONG).show();
                if(!taskList.get(position).getDifficulty().equals("Nenhuma")) {
                    taskpoints = taskpoints / 2;
                    isLate = true;
                }
            }
            confirmDialog(mContext, taskpoints, taskList.get(position).getId(), position, taskList.get(position));
        });

        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Fragment newFragment = new TaskDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("task",taskList.get(holder.getAdapterPosition()));
                newFragment.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
                // Replace whatever is in the fragment_container view with this fragment
                transaction.replace(R.id.navHostFragment, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    void confirmDialog(Context context, int pontos, String id, int position, Task task){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Concluir tarefa ?");
        builder.setMessage("tenha certeza que a tarefa está concluida");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            dbRefTask = FirebaseDatabase.getInstance().getReference("tasks").child(id);

            /*int userPoints;
            userPoints = user.getPoints();
            Log.d("USER POINTS", "ponts =" + userPoints);
            userPoints += pontos;
            Log.d("USER POINTS", "ponts =" + userPoints);*/

            Map<String, Integer> uPoints = new HashMap<>();
            uPoints = user.getPoints();
            Log.d("USER POINTS", "ponts =" + uPoints);
            int newPoints;
            if(task.getGroupID() != null){
                newPoints = pontos;
                if( uPoints.containsKey(task.getGroupID())) {
                    uPoints.put(task.getGroupID(), uPoints.get(task.getGroupID()) + pontos);
                    newPoints = uPoints.get(task.getGroupID()) + pontos;

                } else {
                    uPoints.put(task.getGroupID(), pontos);
                }
            } else {
                uPoints.put("default", uPoints.get("default") + pontos);
                ((MainActivity)mContext).refreshPoints(String.valueOf(pontos));
                newPoints = uPoints.get("default") + pontos;
            }


            ((MainActivity)mContext).refreshPoints(String.valueOf(newPoints));
            ((MainActivity)mContext).updateC();

            Log.d("USER POINTS", "ponts =" + uPoints);

            dbRefUser.child(sPref.getString("uID",null)).child("points").setValue(uPoints);

            dbRefTask.removeValue();
            this.notifyItemRemoved(position);
            ((MainActivity) mContext).sendEmail(task.getEmail(), user.getEmail(), task.getTitle(), 0, isLate);

            String nTitle = "Tarefa concluida";
            String nBody = "a tarefa foi concluida";

            /*FCMsend.pushNotification(
                    context,
                    "com.google.android.gms.tasks.zzw@1d51fa0",
                    nTitle,
                    nBody
            );*/
        });


        builder.setNegativeButton("Não", (dialogInterface, i) -> {

        });
        builder.create().show();

    }
}

class TaskViewHolder extends RecyclerView.ViewHolder{

    TextView task_titulo_txt, task_desc_txt, task_fim_txt,task_pontos_txt;
    MaterialCardView priorityCard,priorityIcon;
    AppCompatImageView catIcon;
    //Animation translate_anim;
    String[] categories,priorities;;
    SharedPreferences sPref;
    Context mContext;
    ConstraintLayout mainLayout;
    Animation translate_anim;
    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        categories = itemView.getResources().getStringArray(R.array.CATEGORIAS);
        priorities = itemView.getResources().getStringArray(R.array.PRIORIDADES);
        task_titulo_txt = itemView.findViewById(R.id.title_txt);
        task_desc_txt = itemView.findViewById(R.id.description_txt);
        task_fim_txt = itemView.findViewById(R.id.dataEnd_txt);
        priorityCard = itemView.findViewById(R.id.materialCardView_bg);
        priorityIcon = itemView.findViewById(R.id.materialCardView_img);
        catIcon = itemView.findViewById(R.id.catImg);
        mainLayout = itemView.findViewById(R.id.mainLayout);
        task_pontos_txt = itemView.findViewById(R.id.pontos_txt);
        //animação do recyclerview
        translate_anim = AnimationUtils.loadAnimation(mContext, R.anim.translate_anim);
        mainLayout.setAnimation(translate_anim);
    }

    public interface MyCallback {
        void onCallback(User user);
    }
}
