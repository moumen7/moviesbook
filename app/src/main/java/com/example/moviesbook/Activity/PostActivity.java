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

public class PostActivity extends AppCompatActivity {
    TextView tv;
    TextView tv2;
    ImageView img;
    Uri ImageData;
    EditText editText;
    FirebaseFirestore db;
    ImageButton imageButton;
    SharedPreferences sp;
    private static AtomicInteger ID_GENERATOR = new AtomicInteger(1000);
    private StorageReference Folder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        db = FirebaseFirestore.getInstance();
        tv2 = findViewById(R.id.uploadbutt);
        img = findViewById(R.id.imagepost);
        editText = findViewById(R.id.edit_text_description);
        sp = getSharedPreferences("user", MODE_PRIVATE);
        imageButton = findViewById(R.id.uploadpic);
        Folder = FirebaseStorage.getInstance().getReference("Images");
    }

    public void uploadpic(View view) {
        if(imageButton.getId() == view.getId())
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
    public void post(View view) {
        if (tv2.getId() == view.getId()) {
            if (img.getDrawable() == null && editText.getText().toString().equals(""))
            {
                Toast.makeText(PostActivity.this,"post can`t be empty",Toast.LENGTH_LONG).show();
            }
            else
            {
                ProgressDialog pd = new ProgressDialog(PostActivity.this);
                pd.setMessage("loading");
                pd.show();
                final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final Date date = new Date();
                final Query query = db.collection("Users").whereEqualTo("id" , sp.getString("ID", ""));
                final String id = getAlphaNumericString(8);
                if(img.getDrawable()!=null) {
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
                                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                Friend ChatUser = snapshot.toObject(Friend.class);
                                                if (ChatUser.getId() != null) {
                                                    if (ChatUser.getId().equals(sp.getString("ID", ""))) {
                                                        String s = ChatUser.getImage();
                                                        if (s.equals(null)) {
                                                            post.put("userimage", null);
                                                        } else {
                                                            post.put("userimage", s);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    Map<String, Boolean> likers = new HashMap<>();
                                    ArrayList<HashMap<String, String>> commenters = new ArrayList<>();
                                    post.put("username", sp.getString("username",""));
                                    post.put("userid", sp.getString("ID",""));
                                    post.put("usedid", getIntent().getStringExtra("ID"));
                                    post.put("usedtitle", getIntent().getStringExtra("title"));
                                    post.put("postdesc", editText.getText().toString());
                                    post.put("Image", String.valueOf(uri));
                                    post.put("Date",formatter.format(date));
                                    post.put("likes", 0);
                                    post.put("comments", 0);
                                    post.put("Likers", likers);
                                    post.put("Commenters", commenters);
                                    post.put("Postid",id);
                                    byte[] array = new byte[7]; // length is bounded by 7
                                    new Random().nextBytes(array);
                                    fb.collection("Posts").document(id).set(post);
                                    Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
                else
                {
                    final Map<String, Object> post = new HashMap<>();
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Friend ChatUser = snapshot.toObject(Friend.class);
                                if (ChatUser.getId() != null) {
                                    if (ChatUser.getId().equals(sp.getString("ID", ""))) {
                                        String s = ChatUser.getImage();
                                        if (s.equals(null)) {
                                            post.put("userimage", null);
                                        } else {
                                            post.put("userimage", s);
                                        }
                                    }
                                }
                            }
                        }
                    });

                    Map<String, Boolean> likers = new HashMap<>();
                    ArrayList<Map<String, Object>> comments = new ArrayList<>();
                    post.put("username", sp.getString("username",""));
                    post.put("userid", sp.getString("ID",""));
                    post.put("usedid", getIntent().getStringExtra("ID"));
                    post.put("usedtitle", getIntent().getStringExtra("title"));
                    post.put("postdesc", editText.getText().toString());
                    post.put("Image", null);
                    post.put("Date",formatter.format(date));
                    post.put("likes", 0);
                    post.put("comments", 0);
                    post.put("Likers", likers);
                    post.put("Postid",id);
                    db.collection("Posts").document(id).set(post);
                    Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        }
    }
    public String getextension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(ImageData));
    }
}
