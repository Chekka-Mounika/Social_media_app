package com.example.bloodhelp.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bloodhelp.Create_Profile;
import com.example.bloodhelp.F1_menu_bottomsheet;
import com.example.bloodhelp.MainActivity;
import com.example.bloodhelp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class Fragment1 extends Fragment {

    ImageView imageView;
    TextView username;
    TextView bio;
    ImageButton btn;
    ImageButton menu_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_1, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        username=getActivity().findViewById(R.id.f1_username);
        imageView=getActivity().findViewById(R.id.f1_image);
        bio=getActivity().findViewById(R.id.f1_bio);
        btn=getActivity().findViewById(R.id.f1_edit);
        menu_btn=getActivity().findViewById(R.id.f1_menu);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity() , Create_Profile.class);
                startActivity(i);

            }
        });
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                F1_menu_bottomsheet bottomsheet=new F1_menu_bottomsheet();
                bottomsheet.show(getFragmentManager() , "bottomsheet");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_id=firebaseUser.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        reference=firestore.collection("user").document(current_id);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String name_result=task.getResult().getString("name");
                    String bio_result=task.getResult().getString("bio");
                    String url=task.getResult().getString("uri");
                    Picasso.get().load(url).into(imageView);
                    username.setText(name_result);
                    bio.setText(bio_result);
                }else{
                    Intent i=new Intent(getActivity() , Create_Profile.class);
                    startActivity(i);
                }
            }
        });
    }
}