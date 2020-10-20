package com.example.moviesbook.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.moviesbook.Activity.HomeActivity;
import com.example.moviesbook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatelistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatelistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatelistFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "ActionBottomDialog";
    TextView Title;
    Button Create;
    EditText editText;
    Uri ImageData;
    SharedPreferences sp ;
    ImageView addImage;
    String id;
    private StorageReference Folder;
    ImageButton Close;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreatelistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment CreatelistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatelistFragment newInstance() {
        CreatelistFragment fragment = new CreatelistFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.createlist_frag, container, false);
        Title = v.findViewById(R.id.Title);
        editText = v.findViewById(R.id.Listname);
        Create = v.findViewById(R.id.Create);
        addImage = v.findViewById(R.id.listImage);
        sp = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        Folder = FirebaseStorage.getInstance().getReference("Images");
        Close = v.findViewById(R.id.close);
        Create.setOnClickListener(this);
        addImage.setOnClickListener(this);
        Close.setOnClickListener(this);
        if(getArguments().getString("act").equals("edit"))
        {
            Title.setText("   Edit List");
            editText.setText(getArguments().getString("name"));
            Picasso.get().load(getArguments().getString("image")).into(addImage);
        }
        else
        {
            Picasso.get().load("https://secure.meetupstatic.com/photos/event/3/d/8/1/highres_477615745.jpeg").into(addImage);
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == Close.getId())
        {
            getActivity().getSupportFragmentManager().beginTransaction().remove(
                    CreatelistFragment.this).commit();
        }
        else if(v.getId() == Create.getId())
        {
            if (editText.getText().toString().equals(""))
            {
                Toast.makeText(getActivity(),"List name can`t be empty",Toast.LENGTH_LONG).show();
            }
            else if (editText.getText().toString().length() > 17)
            {
                Toast.makeText(getActivity(),"List name can`t be more than 18 characters"
                        ,Toast.LENGTH_LONG).show();
            }
            else
            {
                ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage("loading");
                pd.show();
                final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final Date date = new Date();
                 id = getAlphaNumericString(8);
                if(ImageData != null)
                {
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
                                    if (addImage.getDrawable() != null)
                                        post.put("Image", String.valueOf(uri));




                                    if(getArguments().getString("act").equals("edit"))
                                    {
                                        id = getArguments().getString("ID");

                                    }
                                    else
                                    {
                                        post.put("number", 0);
                                        post.put("Date", formatter.format(date));
                                    }
                                    post.put("ID", id);
                                    byte[] array = new byte[7]; // length is bounded by 7
                                    new Random().nextBytes(array);
                                    if (getArguments().getString("choice").equals("Movie")) {
                                        fb.collection("Users").document(sp.getString("ID", ""))
                                                .collection("MoviesList").document(id).set(post, SetOptions.merge());
                                    } else {
                                        fb.collection("Users").document(sp.getString("ID", ""))
                                                .collection("BooksList").document(id).set(post, SetOptions.merge());

                                    }
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
                else
                {
                    FirebaseFirestore fb = FirebaseFirestore.getInstance();
                    final Map<String, Object> post = new HashMap<>();
                    Map<String, Boolean> likers = new HashMap<>();
                    ArrayList<HashMap<String, String>> commenters = new ArrayList<>();
                    post.put("Name", editText.getText().toString());
                    if(!getArguments().getString("act").equals("edit")) {
                        post.put("Image", "https://secure.meetupstatic.com/photos/event/3/d/8/1/highres_477615745.jpeg");
                        post.put("Date", formatter.format(date));
                        post.put("number", 0);
                    }
                    if(getArguments().getString("act").equals("edit"))
                    {
                        id = getArguments().getString("ID");

                    }

                    post.put("ID", id);
                    byte[] array = new byte[7]; // length is bounded by 7
                    new Random().nextBytes(array);
                    if (getArguments().getString("choice").equals("Movie")) {
                        fb.collection("Users").document(sp.getString("ID", ""))
                                .collection("MoviesList").document(id).set(post, SetOptions.merge());
                    } else {
                        fb.collection("Users").document(sp.getString("ID", ""))
                                .collection("BooksList").document(id).set(post, SetOptions.merge());
                        ;
                    }
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                }

            }
        }
        else if(addImage.getId() == v.getId())
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
            addImage.setImageURI(ImageData);

        }
    }
    public void post(View view)
    {



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
    public String getextension(Uri uri)
    {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(ImageData));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
