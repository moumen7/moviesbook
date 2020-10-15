package com.example.moviesbook.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.moviesbook.Activity.followersorfollowing;
import com.example.moviesbook.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
public class ActionBottomDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {
    FirebaseFirestore db ;


    public static final String TAG = "ActionBottomDialog";

    private ItemClickListener mListener;

    public static ActionBottomDialogFragment newInstance() {
        return new ActionBottomDialogFragment();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textView).setOnClickListener(this);
        view.findViewById(R.id.textView2).setOnClickListener(this);
        db= FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView:
                Toast.makeText(getContext(),getArguments().getString("choice"),Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete List")
                        .setMessage("Are you sure you want to delete this List?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Users").document(
                                        FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection(getArguments().getString("choice"))
                                        .document(getArguments().getString("ID")).delete();
                            }
                        })


                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.textView2:
                Intent intent = new Intent(getActivity(), followersorfollowing.class);
                intent.putExtra("Following",true);
                startActivity(intent);
                break;
        }
    }



    public interface ItemClickListener {
        void onItemClick(String item);
    }
}