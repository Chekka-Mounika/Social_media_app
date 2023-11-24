package com.example.bloodhelp.Adapters;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodhelp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



public class Ques_item_ViewHolder extends RecyclerView.ViewHolder{
        public ImageView img_result;
        public TextView name_result , time_result , question_result ;
        public TextView delete_yourq;
        public TextView reply_btn;

        //------------------------
        public ImageButton savebtn;
        DatabaseReference save_reference;
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        public Ques_item_ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void setItem(FragmentActivity activity, String name , String url , String userid , String key , String question , String  time , String privacy ){
            img_result=itemView.findViewById(R.id.qitem_image);
            name_result=itemView.findViewById(R.id.qitem_name);
            time_result=itemView.findViewById(R.id.qitem_time);
            question_result=itemView.findViewById(R.id.qitem_question);
            reply_btn=itemView.findViewById(R.id.qitem_reply);
            Picasso.get().load(url).into(img_result);
            name_result.setText(name);
            time_result.setText(time);
            question_result.setText(question);

        }

        public void saveChecker(String post_key) {
            savebtn=itemView.findViewById(R.id.qitem_save);
            FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            String id=firebaseUser.getUid();
        save_reference=firebaseDatabase.getReference("Saves");
        save_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(post_key).hasChild(id)){
                    savebtn.setImageResource(R.drawable.baseline_turned_in_24);
                }else{
                    savebtn.setImageResource(R.drawable.baseline_turned_in_not_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
        public void setSavedItem(Application activity, String name , String url , String userid , String key , String question , String  time , String privacy){
        img_result=itemView.findViewById(R.id.saved_qitem_image);
        name_result=itemView.findViewById(R.id.saved_qitem_name);
        time_result=itemView.findViewById(R.id.saved_qitem_time);
        question_result=itemView.findViewById(R.id.saved_qitem_question);
        Picasso.get().load(url).into(img_result);
        name_result.setText(name);
        time_result.setText(time);
        question_result.setText(question);
        }

         public void setYourQuestionItem(Application activity, String name , String url , String userid , String key , String question , String  time , String privacy){
        img_result=itemView.findViewById(R.id.your_qitem_image);
        name_result=itemView.findViewById(R.id.your_qitem_name);
        time_result=itemView.findViewById(R.id.your_qitem_time);
        question_result=itemView.findViewById(R.id.your_qitem_question);
        delete_yourq=itemView.findViewById(R.id.your_qitem_delete);
        Picasso.get().load(url).into(img_result);
        name_result.setText(name);
        time_result.setText(time);
        question_result.setText(question);
    }
}

