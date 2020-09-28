package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moviesbook.R;
import com.example.moviesbook.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LoginActivity extends AppCompatActivity {
    EditText Email;
    EditText Password;
    FirebaseAuth auth;
    FirebaseUser user;
    User currentuser;
    SharedPreferences sharedPreferences;
    FirebaseFirestore firestore;
    DocumentReference reference;
    EditText message;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
         firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email = findViewById(R.id.loginemail);
        Password = findViewById(R.id.loginpass);
        auth = FirebaseAuth.getInstance();
        firestore  = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        message = findViewById(R.id.message);

        progressDialog = new ProgressDialog(this);

    }
    public void Login(View view) {

        if(TextUtils.isEmpty(Email.getText().toString()))
        {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password.getText().toString()))
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            final String E = Email.getText().toString();
            String P = Password.getText().toString();
            progressDialog.setTitle("Signed in");
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(E,P)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                               @Override
                                               public void onComplete(@NonNull Task<AuthResult> task) {
                                                   if(task.isSuccessful()) {
                                                       user = auth.getCurrentUser();
                                                       reference = firestore.collection("Users").document(user.getUid());
                                                       reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                           @Override
                                                           public void onSuccess(DocumentSnapshot documentSnapshot) {


                                                               if((User)documentSnapshot.toObject(User.class)!=null)
                                                               {
                                                                   Toast.makeText(LoginActivity.this, "Here", Toast.LENGTH_LONG).show();
                                                                   currentuser = (User)documentSnapshot.toObject(User.class);
                                                                   SharedPreferences.Editor editor = sharedPreferences.edit();

                                                                   editor.putString("username" ,currentuser.getUsername());
                                                                   editor.putString("email" ,currentuser.getEmail());
                                                                   editor.putString("password" ,currentuser.getPassword());
                                                                   editor.putString("ID",currentuser.getId());
                                                                   editor.putBoolean("in",true);
                                                                   editor.commit();
                                                                   progressDialog.dismiss();
                                                                   Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                                                   intent.putExtra("addpic",true);
                                                                   startActivity(intent);
                                                                   finish();


                                                               }
                                                               else
                                                               {
                                                                   Toast.makeText(LoginActivity.this, "Here2", Toast.LENGTH_LONG).show();
                                                               }

                                                           }
                                                       });


                                                   }
                                                   else
                                                   {
                                                       progressDialog.dismiss();
                                                       Toast.makeText(LoginActivity.this, "Error At LOGIN_IN", Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           }
                    ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }

    }
    public void Signin (View view) {
        Intent ic = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(ic);
    }


}
