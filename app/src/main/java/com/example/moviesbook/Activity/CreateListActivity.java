package com.example.moviesbook.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesbook.Friend;
import com.example.moviesbook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateListActivity extends AppCompatActivity {
   Button CreateList;
   Button AddImage;
   ImageView img;
    Uri ImageData;
    EditText editText;
    FirebaseFirestore db;
    SharedPreferences sp;
    private static AtomicInteger ID_GENERATOR = new AtomicInteger(1000);
    private StorageReference Folder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        db = FirebaseFirestore.getInstance();
        CreateList = findViewById(R.id.Create);
        AddImage = findViewById(R.id.AddImage);
        img = findViewById(R.id.listImage);
        editText = findViewById(R.id.Listname);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        Folder = FirebaseStorage.getInstance().getReference("Images");
    }

    public void uploadpic(View view) {
        if(AddImage.getId() == view.getId())
        {
            filechooser();
        }
    }
    public void filechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {

            ImageData = data.getData();
            img.setImageURI(ImageData);

        }
    }
    static String getAlphaNumericString(int n)
    {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (n > 0)) {

                r.append(ch);
                n--;
            }
        }

        // return the resultant string
        return r.toString();
    }
    public void post(View view)
    {

            if (editText.getText().toString().equals(""))
            {
                Toast.makeText(CreateListActivity.this,"List name can't be empty",Toast.LENGTH_LONG).show();
            }
            else if (editText.getText().toString().length() > 17)
            {
                Toast.makeText(CreateListActivity.this,"List name can't be more than 18 characters"
                        ,Toast.LENGTH_LONG).show();
            }
            else
            {
                ProgressDialog pd = new ProgressDialog(CreateListActivity.this);
                pd.setMessage("loading");
                pd.show();
                final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final Date date = new Date();
                final String id = getAlphaNumericString(8);

                    final StorageReference imgname = Folder.child(System.currentTimeMillis()
                            + "." + getextension(ImageData));

                    imgname.putFile(ImageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgname.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseFirestore fb = FirebaseFirestore.getInstance();
                                    final Map<String, Object> post = new HashMap<>();
                                    Map<String, Boolean> likers = new HashMap<>();
                                    ArrayList<HashMap<String, String>> commenters = new ArrayList<>();
                                    post.put("Name", editText.getText().toString());
                                    if(img.getDrawable()!=null)
                                        post.put("Image", String.valueOf(uri));
                                    else
                                        post.put("Image", null);
                                    post.put("Date",formatter.format(date));
                                    post.put("number", 0);
                                    post.put("ID",id);
                                    byte[] array = new byte[7]; // length is bounded by 7
                                    new Random().nextBytes(array);
                                    if(getIntent().hasExtra("Movie"))
                                    {
                                        fb.collection("Users").document(sp.getString("ID", ""))
                                                .collection("MoviesList").document(id).set(post);
                                    }
                                    else
                                    {
                                        fb.collection("Users").document(sp.getString("ID", ""))
                                                .collection("BooksList").document(id).set(post);;
                                    }
                                    Intent intent = new Intent(CreateListActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });

            }

    }
    public String getextension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(ImageData));
    }
}
