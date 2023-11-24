package com.example.bloodhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class F2_menu_bottomsheet extends BottomSheetDialogFragment {

    CardView c1 , c2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=getLayoutInflater().inflate(R.layout.f2_menu_bottom_options , null);
       c1=view.findViewById(R.id.f2_saved_ques);
       c2=view.findViewById(R.id.f2_your_ques);
       c1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity() , SavedQues_Activity.class));
           }
       });
       c2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity() , YourQues_Activity.class));
           }
       });

        //---------------------
        return view;
    }
}
