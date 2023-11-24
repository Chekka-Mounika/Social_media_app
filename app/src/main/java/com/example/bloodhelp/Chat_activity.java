package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bloodhelp.Adapters.ChatItem_Viewholder;
import com.example.bloodhelp.Adapters.Feed_post_ViewHolder;
import com.example.bloodhelp.DataModels.All_userMember;
import com.example.bloodhelp.DataModels.PostData_member;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Chat_activity extends AppCompatActivity {
    EditText editText;
    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference prof_reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText=findViewById(R.id.ac_chat_search_user);
        recyclerView=findViewById(R.id.ac_chat_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Chat_activity.this));
        prof_reference=database.getReference().child("All users");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String query=editText.getText().toString();
                Query search=prof_reference.orderByChild("name").startAt(query).endAt(query +"\uf0ff");

                prof_reference=database.getReference().child("All users");
                FirebaseRecyclerOptions<All_userMember> options=new FirebaseRecyclerOptions.Builder<All_userMember>()
                        .setQuery(search, All_userMember.class)
                        .build();


                FirebaseRecyclerAdapter<All_userMember , ChatItem_Viewholder> firebaseRecyclerAdapter=
                        new FirebaseRecyclerAdapter<All_userMember, ChatItem_Viewholder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ChatItem_Viewholder holder, int position, @NonNull All_userMember model) {
                                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                String name_=model.getName();
                                String url_=model.getUrl();
                                String uid_=model.getUid();
                                String bio_= model.getBio();
                                holder.setItem(getApplication(),  name_,  uid_,  bio_ ,  url_);
                                holder.send_message.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i=new Intent(Chat_activity.this , MessageToUSer.class);
                                        i.putExtra("name" , name_);
                                        i.putExtra("url",url_);
                                        i.putExtra("uid" , uid_);
                                        startActivity(i);
                                    }
                                });

                            }
                            @NonNull
                            @Override
                            public ChatItem_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_profile_item,parent,false);
                                return new ChatItem_Viewholder(view);
                            }
                        };

                recyclerView.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.startListening();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        prof_reference=database.getReference().child("All users");
        FirebaseRecyclerOptions<All_userMember> options=new FirebaseRecyclerOptions.Builder<All_userMember>()
                .setQuery(prof_reference, All_userMember.class)
                .build();


        FirebaseRecyclerAdapter<All_userMember , ChatItem_Viewholder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<All_userMember, ChatItem_Viewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ChatItem_Viewholder holder, int position, @NonNull All_userMember model) {
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        String name_=model.getName();
                        String url_=model.getUrl();
                        String uid_=model.getUid();
                        String bio_= model.getBio();
                        holder.setItem(getApplication(),  name_,  uid_,  bio_ ,  url_);
                        holder.send_message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(Chat_activity.this , MessageToUSer.class);
                                i.putExtra("name" , name_);
                                i.putExtra("url",url_);
                                i.putExtra("uid" , uid_);
                                startActivity(i);
                            }
                        });

                    }
                    @NonNull
                    @Override
                    public ChatItem_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_profile_item,parent,false);
                        return new ChatItem_Viewholder(view);
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}