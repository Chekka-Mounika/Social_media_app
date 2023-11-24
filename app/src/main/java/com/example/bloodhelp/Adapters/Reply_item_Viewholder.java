package com.example.bloodhelp.Adapters;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodhelp.R;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Reply_item_Viewholder  extends RecyclerView.ViewHolder {
    ImageView img_;
    TextView time_;
    TextView name_;
    TextView answer_;
    public TextView upvotebtn_;
    public TextView no_votes_;

    String q_key;
    //-----------------------------
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference UpVotesRef;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String current_userid=user.getUid();

    public Reply_item_Viewholder(@NonNull View itemView) {
        super(itemView);
        img_ = itemView.findViewById(R.id.reply_to_image);
        time_ = itemView.findViewById(R.id.reply_to_time);
        name_ = itemView.findViewById(R.id.reply_to_name);
        answer_ = itemView.findViewById(R.id.reply_to_answer);
        upvotebtn_ = itemView.findViewById(R.id.reply_to_upvote);
        no_votes_ = itemView.findViewById(R.id.reply_to_no_votes);
    }

    public void setItem(Application application , String name, String uid, String answer, String url, String time ,String qkey ){
        name_.setText(name);
        answer_.setText(answer);
        Picasso.get().load(url).into(img_);
        time_.setText(time);
        this.q_key=qkey;
        upVoteChecker(qkey);
    }


    public void upVoteChecker(String qkey) {
        UpVotesRef=firebaseDatabase.getReference().child("UpVotes").child(qkey);
        UpVotesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(current_userid)){
                    upvotebtn_.setText("UNVOTE");

                }else{
                    upvotebtn_.setText("VOTE");

                }
                no_votes_.setText( (int)snapshot.getChildrenCount()+" votes"   );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addRemoveVote(String k) {
        UpVotesRef=firebaseDatabase.getReference().child("UpVotes").child(k);
        UpVotesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(current_userid)){
                     UpVotesRef.child(current_userid).removeValue();
                     upvotebtn_.setText("VOTE");

                }else{
                    UpVotesRef.child(current_userid).setValue(true);
                    upvotebtn_.setText("UNVOTE");

                }
                no_votes_.setText(  (int)snapshot.getChildrenCount()+" votes"    );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}