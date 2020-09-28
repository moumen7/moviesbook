package com.example.moviesbook.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moviesbook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddPicActivity extends AppCompatActivity {
    ImageButton imageButton ;
    Uri ImageData;
    private StorageReference Folder;
    static public int ImageBack =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pic);
        imageButton = findViewById(R.id.upload);
        Folder = FirebaseStorage.getInstance().getReference("Images");
    }
    public void skip(View view) {

    }
    public void Uploadimg(View view)
    {
        filechooser();

    }
    public void filechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {

            ImageData = data.getData();
            imageButton.setImageURI(ImageData);
            final StorageReference imgname = Folder.child(System.currentTimeMillis()
                    + "." + getextension(ImageData));

            imgname.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgname.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                            FirebaseFirestore fb = FirebaseFirestore.getInstance();
                            fb.collection("Users").document(sp.getString("ID","")).
                                    update("image",String.valueOf(uri));
                            Intent intent = new Intent(AddPicActivity.this,AddActivity.class);
                            intent.putExtra("addpic",true);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            });
        }

    }
    public String getextension(Uri uri)
    {
        ContentResolver cr =getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(ImageData));
    }

}
