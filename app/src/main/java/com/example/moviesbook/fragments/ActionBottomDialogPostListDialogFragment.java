package com.example.moviesbook.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviesbook.Activity.followersorfollowing;
import com.example.moviesbook.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
public class ActionBottomDialogPostListDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {
    FirebaseFirestore db ;
    TextView tv;

    public static final String TAG = "ActionBottomDialog";
    SharedPreferences sp2 ;
    private ItemClickListener mListener;

    public static ActionBottomDialogPostListDialogFragment newInstance() {
        return new ActionBottomDialogPostListDialogFragment();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_item_list_dialog_item, container, false);
        tv = view.findViewById(R.id.textView);
        sp2 = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        if(getArguments().getString("choice").equals("true"))
        {
            tv.setText("remove from list");
        }
        return view;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textView).setOnClickListener(this);

        db= FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.textView:
                if(!getArguments().getString("choice").equals("true"))
                {
                    Toast.makeText(getContext(), getArguments().getString("choice"), Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete List")
                            .setMessage("Are you sure you want to delete this Post?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("Posts").document(getArguments().getString("ID")
                                    ).delete();
                                    Snackbar.make(getView(), "Post deleted", Snackbar.LENGTH_LONG)
                                            .setAction("CLOSE", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                            .show();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(
                                            ActionBottomDialogPostListDialogFragment.this).commit();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                {

                    db.collection(getArguments().getString("MB")).document(getArguments().getString("ID"))
                            .update("users", FieldValue.arrayRemove(getArguments().getString("ID2")));
                    int one = getArguments().getString("ID2").length();
                    String put = getArguments().getString("ID2").substring(sp2.getString("ID","").length() , one);
                    if(put.equals(""))
                    {
                        put = "favorites122";
                        db.collection(getArguments().getString("MB")).document(getArguments().getString("ID"))
                                .update("favs", FieldValue.increment(-1));
                    }
                    db.collection("Users").document(sp2.getString("ID",""))
                            .collection(getArguments().getString("MB") + "List").document(put)
                            .update("number", FieldValue.increment(-1));
                                    Snackbar.make(getView(), "Removed", Snackbar.LENGTH_LONG)
                                            .setAction("CLOSE", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                            .show();
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(
                                            ActionBottomDialogPostListDialogFragment.this).commit();

                }
                break;

        }
    }



    public interface ItemClickListener {
        void onItemClick(String item);
    }
}