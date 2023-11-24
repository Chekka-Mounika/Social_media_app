package com.example.bloodhelp.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bloodhelp.Adapters.Feed_post_ViewHolder;
import com.example.bloodhelp.Adapters.Ques_item_ViewHolder;
import com.example.bloodhelp.Chat_activity;
import com.example.bloodhelp.CreatePost_Act;
import com.example.bloodhelp.DataModels.PostData_member;
import com.example.bloodhelp.DataModels.QuestionMember;
import com.example.bloodhelp.R;
import com.example.bloodhelp.Reply_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Fragment3 extends Fragment {
    //----------------------------
    ImageView add;
    //----------------------------
    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference Allposts , LikesRef;

    ImageView chat;

    //----------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //=======================================

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_3, container, false);
        add=view.findViewById(R.id.f3_addpostbtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity() , CreatePost_Act.class));
            }
        });
        chat=view.findViewById(R.id.chat_btn);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity() , Chat_activity.class);
                startActivity(i);
            }
        });
        recyclerView=view.findViewById(R.id.feed_recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Allposts=database.getReference().child("All posts");
        LikesRef=database.getReference().child("PostLikes");

        FirebaseRecyclerOptions<PostData_member> options=new FirebaseRecyclerOptions.Builder<PostData_member>()
                .setQuery(Allposts, PostData_member.class)
                .build();

        FirebaseRecyclerAdapter<PostData_member , Feed_post_ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<PostData_member, Feed_post_ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Feed_post_ViewHolder holder, int position, @NonNull PostData_member model) {
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        String id=user.getUid();
                        String post_key=model.getPostid();
                        String p_name=model.getName();
                        String p_url=model.getUrl();
                        String p_postUri=model.getPosturi();
                        String p_desc=model.getDesc();
                        String p_type=model.getType();
                        String p_time=model.getTime();
                        String p_uid=model.getUid();
                        String p_pstid=model.getPostid();
                        holder.setItem(getActivity() , p_name ,p_url ,p_postUri ,p_desc ,p_type , p_time,p_uid,p_pstid);

                        holder.likebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(post_key!=null){
                                        if(snapshot.child(post_key).hasChild(id)){
                                            LikesRef.child(post_key).child(id).removeValue();
                                            holder.likebtn.setImageResource(R.drawable.dislike_border);
                                            Toast.makeText(getActivity(), "you unliked the post", Toast.LENGTH_SHORT).show();
                                        }else{
                                            LikesRef.child(post_key).child(id).setValue(true);
                                            Toast.makeText(getActivity(), "you liked the post", Toast.LENGTH_SHORT).show();
                                            holder.likebtn.setImageResource(R.drawable.baseline_like_24);
                                        }}
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public Feed_post_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
                        return new Feed_post_ViewHolder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}