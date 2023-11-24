package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bloodhelp.Adapters.Ques_item_ViewHolder;
import com.example.bloodhelp.DataModels.QuestionMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class SavedQues_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference SaveListRef;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String id=user.getUid();
    //--------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_ques);
        recyclerView=findViewById(R.id.saved_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SaveListRef=firebaseDatabase.getReference("SavesList").child(id);


        FirebaseRecyclerOptions<QuestionMember> options=new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(SaveListRef,QuestionMember.class)
                .build();
        FirebaseRecyclerAdapter<QuestionMember , Ques_item_ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<QuestionMember, Ques_item_ViewHolder>(options) {
                    @NonNull
                    @Override
                    public Ques_item_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_question_item,parent,false);
                        return new Ques_item_ViewHolder(view);

                    }
                    @Override
                    protected void onBindViewHolder(@NonNull Ques_item_ViewHolder holder, int position, @NonNull QuestionMember model) {
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        String id=user.getUid();
                        String post_key=getRef(position).getKey();
                        holder.setSavedItem(getApplication() , model.getName() , model.getUrl() , model.getUserid() , model.getKey() ,
                                model.getQuestion() , model.getTime() ,model.getPrivacy());
                        String que=getItem(position).getQuestion();
                        String name=getItem(position).getName();
                        String url=getItem(position).getUrl();
                        String time=getItem(position).getTime();
                        String privacy=getItem(position).getPrivacy();
                        String uid=getItem(position).getUserid();

                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

}