package com.example.bloodhelp.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bloodhelp.Adapters.Ques_item_ViewHolder;
import com.example.bloodhelp.AskQactivity;
import com.example.bloodhelp.DataModels.QuestionMember;
import com.example.bloodhelp.F2_menu_bottomsheet;
import com.example.bloodhelp.R;
import com.example.bloodhelp.Reply_activity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragmemt2 extends Fragment {
    FloatingActionButton btn;
    ImageView imageView;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference , SaveRef , SaveListRef;
    Boolean SaveChecker;

    QuestionMember member;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fragmemt2, container, false);
        btn=view.findViewById(R.id.f2_floatbtn);
        imageView=view.findViewById(R.id.f2_image);
        //----------
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String id=user.getUid();
        documentReference=firebaseFirestore.collection("user").document(id);
        member=new QuestionMember();
        SaveRef=firebaseDatabase.getReference("Saves");
        SaveListRef=firebaseDatabase.getReference("SavesList").child(id);
        //--------

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity() , AskQactivity.class);
                startActivity(i);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                F2_menu_bottomsheet f2_menu_bottomsheet=new F2_menu_bottomsheet();
                f2_menu_bottomsheet.show(getFragmentManager() , "bottomf2");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView=getActivity().findViewById(R.id.f2_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseReference=firebaseDatabase.getReference().child("All ques");

        FirebaseRecyclerOptions<QuestionMember> options=new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember , Ques_item_ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<QuestionMember, Ques_item_ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Ques_item_ViewHolder holder, int position, @NonNull QuestionMember model) {
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        String id=user.getUid();
                        String post_key=getRef(position).getKey();
                        holder.setItem(getActivity() , model.getName() , model.getUrl() , model.getUserid() , model.getKey() ,
                                model.getQuestion() , model.getTime() ,model.getPrivacy());
                        String que=getItem(position).getQuestion();
                        String name=getItem(position).getName();
                        String url=getItem(position).getUrl();
                        String time=getItem(position).getTime();
                        String privacy=getItem(position).getPrivacy();
                        String uid=getItem(position).getUserid();
                        holder.saveChecker(post_key);
                        holder.savebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SaveChecker=true;
                                SaveRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(SaveChecker.equals(true)){
                                            if(snapshot.child(post_key).hasChild(id)){
                                                SaveRef.child(post_key).child(id).removeValue();
                                                delete(time);
                                                SaveChecker=false;
                                            }else{
                                                SaveRef.child(post_key).child(id).setValue(true);
                                                member.setName(name);
                                                member.setTime(time);
                                                member.setPrivacy(privacy);
                                                member.setUserid(uid);
                                                member.setUrl(url);
                                                member.setQuestion(que);
                                                String id=SaveListRef.push().getKey();
                                                SaveListRef.child(id).setValue(member);
                                                SaveChecker=false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        holder.reply_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(getActivity() , Reply_activity.class);
                                i.putExtra("uid",uid);
                                i.putExtra("question",que);
                                i.putExtra("name",name);
                                i.putExtra("time",time);
                                i.putExtra("privacy",privacy);
                                i.putExtra("post_key",model.getKey());
                                startActivity(i);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public Ques_item_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);
                       return new Ques_item_ViewHolder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public void delete(String time){
        Query query=SaveListRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                    Toast.makeText(getActivity() , "Deleted" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}