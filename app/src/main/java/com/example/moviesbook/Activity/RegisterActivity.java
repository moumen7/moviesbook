package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.example.moviesbook.User;

import com.example.moviesbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    EditText Email, Password, Name,ConfirmPassword;

    FirebaseAuth auth;
    User current_user;
    SharedPreferences sharedPreferences;
    FirebaseUser user;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        getSupportActionBar().hide();
         firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Email = findViewById(R.id.email);
        Name = findViewById(R.id.username);
        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        Password = findViewById(R.id.Password);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    public void LognIn(View view) {
        onBackPressed();
        finish();

    }
    public void Sign_Up(View view) {
        String My_email = Email.getText().toString();
        String My_password = Password.getText().toString();
        String My_Username = Name.getText().toString();
        if(TextUtils.isEmpty(Email.getText()))
        {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password.getText()))
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name.getText()))
        {
            Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show();
        }
        else if(My_Username.length() < 4 && My_Username.length() < 20)
        {
            Toast.makeText(this, "Username must be between 4 to 19 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            current_user = new User(My_email, "0", My_Username);
            firestore.collection("Usernames").document(current_user.getUsername())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                         if (!task.getResult().exists())
                                                         {
                                                             Map<String, Object> input = new HashMap();
                                                             final Map<String, Object> number = new HashMap();
                                                             input.put("username", current_user.getUsername());
                                                             firestore.collection("Usernames").document(current_user.getUsername())
                                                                     .set(input);

                                                             auth.createUserWithEmailAndPassword(current_user.getEmail(), String.valueOf(Password.getText())).
                                                                     addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<AuthResult> task) {
                                                                             if (task.isSuccessful()) {
                                                                                 user = auth.getCurrentUser();
                                                                                 current_user.setId(user.getUid());
                                                                                 firestore.collection("Users").document(user.getUid()).set(current_user)
                                                                                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                             @Override
                                                                                             public void onSuccess(Void aVoid) {
                                                                                                 number.put("number", 0);
                                                                                                 firestore.collection("Users").document(current_user.getId())
                                                                                                         .collection("MoviesList").document("favorites122")
                                                                                                         .set(number);

                                                                                                 firestore.collection("Users").document(current_user.getId())
                                                                                                         .collection("BooksList").document("favorites122")
                                                                                                         .set(number);
                                                                                                 SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                                 editor.putString("ID", current_user.getId());
                                                                                                 editor.putString("username", current_user.getUsername());
                                                                                                 editor.putString("email", current_user.getEmail());
                                                                                                 editor.putBoolean("in", true);
                                                                                                 editor.commit();
                                                                                                 // Toast.makeText(SIGN_UP.this, "Save", Toast.LENGTH_SHORT).show();
                                                                                                 progressDialog.dismiss();
                                                                                                 Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                                                                                 startActivity(intent);
                                                                                                 finish();

                                                                                             }
                                                                                         }).addOnFailureListener(new OnFailureListener() {

                                                                                     @Override
                                                                                     public void onFailure(@NonNull Exception e) {
                                                                                         Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                     }
                                                                                 });
                                                                             }
                                                                         }
                                                                     }).addOnFailureListener(new OnFailureListener() {
                                                                 @Override
                                                                 public void onFailure(@NonNull Exception e) {

                                                                     progressDialog.dismiss();
                                                                     Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                 }
                                                             });
                                                         } else {

                                                             Email.setText("");
                                                             Password.setText("");
                                                             Name.setText("");
                                                             Toast.makeText(RegisterActivity.this, "Username exists", Toast.LENGTH_SHORT).show();
                                                             progressDialog.hide();
                                                         }
                                                     }
                                                 }
                    );
            }


        }



}
