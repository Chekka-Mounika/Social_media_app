package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bloodhelp.Adapters.Ques_item_ViewHolder;
import com.example.bloodhelp.DataModels.QuestionMember;
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

public class YourQues_Activity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference UserQuesListRef;
    DatabaseReference AllQuesREf;
    RecyclerView recyclerView;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    String id=user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_ques);
        recyclerView=findViewById(R.id.your_ques_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AllQuesREf=firebaseDatabase.getReference("All ques");
        UserQuesListRef=firebaseDatabase.getReference("User ques").child(id);


        FirebaseRecyclerOptions<QuestionMember> options=new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(UserQuesListRef,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember , Ques_item_ViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<QuestionMember, Ques_item_ViewHolder>(options) {
                    @NonNull
                    @Override
                    public Ques_item_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.your_question_item,parent,false);
                        return new Ques_item_ViewHolder(view);

                    }
                    @Override
                    protected void onBindViewHolder(@NonNull Ques_item_ViewHolder holder, int position, @NonNull QuestionMember model) {
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                       // String id=user.getUid();
                        //String post_key=getRef(position).getKey();
                        holder.setYourQuestionItem(getApplication() , model.getName() , model.getUrl() , model.getUserid() , model.getKey() ,
                                model.getQuestion() , model.getTime() ,model.getPrivacy());

                        String time=getItem(position).getTime();
                        holder.delete_yourq.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delete(time);
                            }
                        });
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public void delete(String time){
        Query query=UserQuesListRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                    Toast.makeText(YourQues_Activity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query query1=AllQuesREf.orderByChild("time").equalTo(time);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                    Toast.makeText(YourQues_Activity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}