package com.lucaszoka.taskmaster_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lucaszoka.taskmaster_v2.model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    MenuItem groupsItem;
    Menu navMenu;
    ImageView foto2;
    TextView profName,profPoints;
    MaterialCardView mcvHeader;
    Uri profileImage;
    GoogleSignInClient gsc;
    SharedPreferences sPref;
    DrawerLayout drawerLayout;
    DatabaseReference dbRef;
    User user;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        sPref = getSharedPreferences("SPX", MODE_PRIVATE);
        if (Uri.parse(sPref.getString("imageURL", null)) != null) {
            profileImage = Uri.parse(sPref.getString("imageURL", null));
        }

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(sPref.getString("uID",null));
        updateToken();

        //pega e instancia o sidebar
        drawerLayout = findViewById(R.id.main_drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        Toolbar toolbar = findViewById(R.id.main_toolbar);

        //não lembro o que faz, deve ser pra abrir o navbar/sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //cria e linka o navcontroler no nav_menu
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        assert navHostFragment != null;
        NavController  navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        profName = (TextView) header.findViewById(R.id.header_username);
        profName.setText(sPref.getString("nome", null));
        foto2 = (ImageView) header.findViewById(R.id.imageProfile);
        profPoints = (TextView) header.findViewById(R.id.userPoints);
        mcvHeader = (MaterialCardView) header.findViewById(R.id.materialCardViewHeader);
        mcvHeader.setCardBackgroundColor(sPref.getInt("MGColor", this.getColor(R.color.teal_700)));

        navMenu = navigationView.getMenu();
        groupsItem = navMenu.findItem(R.id.nav_group);

        if(sPref.contains("MGName")){
            groupsItem.setTitle(sPref.getString("MGName", null));
            mcvHeader.setCardBackgroundColor(sPref.getInt("MGColor", this.getColor(R.color.teal_700)));
        }

        header.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                exitGroupDialog();
                return false;
            }
        });

        Picasso.get().load(profileImage).into(foto2);




        updateC();
        /*Log.d("USER INFO", "procurando user");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Log.d("USER INFO", "User: " + user);
                //Log.d("USER INFO", "User name: " + user.getName());
                Map<String, Integer> uPoints = new HashMap<>();
                if (user.getPoints() != null) {
                    uPoints = user.getPoints();
                    Log.d("PONTOS", "uPoints = " + uPoints);


                    if (uPoints != null) {
                        if (sPref.contains("MGID")) {
                            for (String gID : uPoints.keySet()) {
                                if (gID.equals(sPref.getString("MGID", null))) {
                                    refreshPoints(String.valueOf(uPoints.get(gID)));
                                }
                            }
                        } else {
                            refreshPoints(String.valueOf(uPoints.get("default")));
                        }
                    }
                    //profPoints.setText(user.getPoints() + " pontos");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    //essa função é publica pq esta sendo usada no xml do nav_menu o botão de logout no onClick
    public void logoutDialog(MenuItem menuItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deslogar ?");
        builder.setMessage("Tem certeza que deseja deslogar ?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            try {
                //desloga duh
                gsc.signOut();
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = sPref.edit();
                editor.remove("MGName");
                editor.remove("MGID");
                editor.remove("MGColor");
                editor.commit();

                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }catch(Exception e){
                e.printStackTrace();
            }
        });


        builder.setNegativeButton("Não", (dialogInterface, i) -> {

        });
        builder.create().show();

    }

    public void exitGroupDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Trocar Grupo?");
        builder.setMessage("Mostrar apenas suas tarefas?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            try {
                SharedPreferences.Editor editor = sPref.edit();
                editor.remove("MGName");
                editor.remove("MGID");
                editor.remove("MGColor");
                editor.commit();

                refreshMenu("Groups", this.getColor(R.color.teal_700));
                updateC();
            }catch(Exception e){
                e.printStackTrace();
            }
        });


        builder.setNegativeButton("Não", (dialogInterface, i) -> {

        });
        builder.create().show();

    }

    public void updateToken(){
        String token = String.valueOf(FirebaseMessaging.getInstance().getToken());
        String uID = FirebaseAuth.getInstance().getUid();

        if(uID != null){
            FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("token").setValue(token);
        }
    }
    public void refreshMenu(String groupName, int color){
        groupsItem.setTitle(groupName);
        mcvHeader.setCardBackgroundColor(color);
    }
    public void refreshPoints(String points){
        profPoints.setText(points);
    }

    public void updateC(){
        Log.d("USER INFO", "procurando user");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Log.d("USER INFO", "User: " + user);
                Map<String, Integer> uPoints = new HashMap<>();
                if (user.getPoints() != null) {
                    uPoints = user.getPoints();
                    Log.d("PONTOS", "uPoints = " + uPoints);


                    if (uPoints != null) {
                        if (sPref.contains("MGID")) {
                            Log.d("PONTOS", "MGID: " + sPref.getString("MGID", null));
                            if(uPoints.containsKey(sPref.getString("MGID", null))){
                                refreshPoints(String.valueOf(uPoints.get(sPref.getString("MGID", null))));
                            } else {
                                refreshPoints("0");
                            }
                        } else {
                            refreshPoints(String.valueOf(uPoints.get("default")));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendEmail(String rcvEmail, String uEmail, String tTitle,Integer type, boolean isLate){
        String sndEmail = "TaskMasterApp.mailler@gmail.com";
        String sndPassword = "apnb knos gwek hmxy";
        String sHost = "smtp.gmail.com";

        String mTitle;
        String mText;

        if(type==0){
            mTitle = "Tarefa Concluida!";
            if(isLate){
                mText = "A Tarefa: " + tTitle + " foi concluida COM ATRASO pelo usuário: " + uEmail;
            }else{
                mText = "A Tarefa: " + tTitle + " foi concluida pelo usuário: " + uEmail;
            }
        } else {
            mTitle = "Recompensa Resgatada!";
            mText = "A Recompensa: " + tTitle + " foi Resgatada pelo usuário: " + uEmail;
        }

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", sHost);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sndEmail, sndPassword);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(rcvEmail));
            mimeMessage.setSubject(mTitle);
            mimeMessage.setText(mText);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}