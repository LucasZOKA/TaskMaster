package com.lucaszoka.taskmaster_v2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    MaterialCardView btnLogar;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogar = findViewById(R.id.loginCardView);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        //não faço ideia do que essa parte faz, não fui eu q fiz, foi o lucas, mas ele n lembra que foi ele que fez
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnLogar.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);

        });
    }

    private void firebaseAuth(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();

                //pega as informações da conta google e coloca num hasmap pra dps salvar no firebase
                HashMap<String, Object> map = new HashMap<>();
                Map<String,Integer> points =  new HashMap<>();
                points.put("default", 0);

                map.put("id",user.getUid());
                map.put("name",user.getDisplayName());
                map.put("profile", user.getPhotoUrl().toString());
                map.put("email", user.getEmail());
                //lembrar de mudar os pontos pra map groupid e pontos
                map.put("points", points);

                //database.getReference().child("users").child(user.getUid()).setValue(map);


                //dando erro na hora do upload de um user novo, sem o check se existe ele funfa normal
                database.getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.exists()){
                            Log.d("FODEU", "Snapshot NÂO Existe");
                            //salva no firebase
                            //database.getReference().child("users").child(user.getUid()).setValue(map);
                            UpdateUser(user, map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Log.d("FODEU", "Snapshot Existe");
                SharedPreferences sPref = getSharedPreferences("SPX",MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("email", user.getEmail());
                editor.putString("uID",user.getUid());
                editor.putString("nome",user.getDisplayName());
                editor.putString("imageURL",user.getPhotoUrl().toString());
                editor.apply();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);


                /*//salva as infos do usuario na memoria local do aparelho pelo sharedprefs
                SharedPreferences sPref = getSharedPreferences("SPX",MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("email", user.getEmail());
                editor.putString("uID",user.getUid());
                editor.putString("nome",user.getDisplayName());
                editor.putString("imageURL",user.getPhotoUrl().toString());
                editor.apply();

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();*/

            }else{
                Toast.makeText(LoginActivity.this, "firebaseAuth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void UpdateUser (FirebaseUser user, HashMap<String, Object> map){

        database.getReference().child("users").child(user.getUid()).setValue(map);

        //salva as infos do usuario na memoria local do aparelho pelo sharedprefs
        SharedPreferences sPref = getSharedPreferences("SPX",MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("email", user.getEmail());
        editor.putString("uID",user.getUid());
        editor.putString("nome",user.getDisplayName());
        editor.putString("imageURL",user.getPhotoUrl().toString());
        editor.apply();



        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}