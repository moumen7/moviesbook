package com.example.moviesbook.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moviesbook.Adapter.UserAdapter;
import com.example.moviesbook.Interfaces.ClickListener;
import com.example.moviesbook.R;
import com.example.moviesbook.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chats extends Fragment {

    private static final String TAG = "4";
    private RecyclerView recyclerView;
    private List<User> mUsers_toBe_AddedTo;
    User user =  new User();

    private Map<String, Pair<Timestamp,String>> usersList_fb;

    FirebaseUser fUser;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    UserAdapter adapter;
    int index;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Chats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chats.
     */
    // TODO: Rename and change types and number of parameters
    public static Chats newInstance(String param1, String param2) {
        Chats fragment = new Chats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersList_fb = new HashMap<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Task one = db.collection("Chats").whereEqualTo("user1",FirebaseAuth.getInstance().getCurrentUser().getUid()).get();
        Task two = db.collection("Chats").whereEqualTo("user2",FirebaseAuth.getInstance().getCurrentUser().getUid()).get();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(one,two);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots)
                {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        {
                               User user = (User)document.toObject(User.class);
                                index = document.getId().indexOf(fUser.getUid());

                                /// length of fUser id
                                int size_of_string = fUser.getUid().length();
                                Log.d(TAG, "index of fUser: " + index + " and size of fUser id: " + size_of_string);
                                Log.d(TAG, "fUser id: " + fUser.getUid());
                                /// if the room id doesn't start with the fUser id, make a substring starting at 0 and
                                // ends at the index of fUser id in the room id
                                if (index != 0) {
                                    Log.d(TAG, "other user id: " + document.getId().substring(0, index));
                                    usersList_fb.put(document.getId().substring(0, index),Pair.create(user.getDate(),user.getLastmessage()) );
                                }
                                /// if room id starts with the fUser id, make a substring starting at the index thats
                                /// equal to length of (fUser id)-1 to the end of the string
                                else {
                                    Log.d(TAG, "other user id: " + document.getId().substring(size_of_string, document.getId().length() - 1));
                                    usersList_fb.put(document.getId().substring(size_of_string, document.getId().length() - 1), Pair.create(user.getDate(),user.getLastmessage()));
                                }
                                //Log.d(TAG, "index of the other user: " + index);



                        }

                    }

                }
                readChats();
            }


        });


       return view;
    }
    private void readChats(){
        mUsers_toBe_AddedTo = new ArrayList<>();

        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mUsers_toBe_AddedTo.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {

                    User user = (User)document.toObject(User.class);

                        if(usersList_fb.containsKey(user.getId()))
                        {
                            if(mUsers_toBe_AddedTo.size()!=0)
                            {
                                for(User usr:new ArrayList<>(mUsers_toBe_AddedTo))
                                {
                                    if(!usr.getId().equals(user.getId())){
                                        if(!mUsers_toBe_AddedTo.contains(user)) {
                                            user.setDate(usersList_fb.get(user.getId()).first);
                                            user.setLastmessage(usersList_fb.get(user.getId()).second);
                                            mUsers_toBe_AddedTo.add(user);
                                        }
                                    }
                                }
                            }
                            else{
                                user.setDate(usersList_fb.get(user.getId()).first);
                                user.setLastmessage(usersList_fb.get(user.getId()).second);
                                mUsers_toBe_AddedTo.add(user);
                            }
                        }

                }
                Collections.sort(mUsers_toBe_AddedTo ,new Comparator<User>(){

                    public int compare(User o1, User o2)
                    {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                adapter = new UserAdapter(getContext(), mUsers_toBe_AddedTo, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }

                    @Override
                    public void onLongClicked(int position) {

                    }
                });
                recyclerView.setAdapter(adapter);

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


}