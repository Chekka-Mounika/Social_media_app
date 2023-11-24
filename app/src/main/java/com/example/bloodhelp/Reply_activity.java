package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodhelp.Adapters.Ques_item_ViewHolder;
import com.example.bloodhelp.Adapters.Reply_item_Viewholder;
import com.example.bloodhelp.DataModels.AnswerMember;
import com.example.bloodhelp.DataModels.QuestionMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Reply_activity extends AppCompatActivity {
    ImageView asker,replier;
    TextView time , name , question;
    TextView replybtn;
    public String reply_msg_time;
    //-----------
    EditText replyMsg;
    RecyclerView recyclerView;
    String uid_ , question_ , name_ , time_ , privacy_ , postkey_;
    //------------

    DocumentReference r1 , r2cu;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String cu_id=user.getUid();
    String cu_name , cu_url;
    //----------
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    ImageView btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        asker=findViewById(R.id.ques_reply_image);
        time=findViewById(R.id.ques_reply_time);
        name=findViewById(R.id.ques_reply_name);
        question=findViewById(R.id.ques_reply_question);

        recyclerView=findViewById(R.id.ques_reply_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Reply_activity.this));
        replier=findViewById(R.id.ques_reply_asking_photo);

        replyMsg=findViewById(R.id.ques_reply_edittext);
        btn=findViewById(R.id.ques_reply_btnr);
        //-----------

        //-----------
        Bundle extra=getIntent().getExtras();
        if(extra!=null){
            uid_=extra.getString("uid");
            question_=extra.getString("question");
            name_=extra.getString("name");
            time_=extra.getString("time");
            privacy_=extra.getString("privacy");
            postkey_=extra.getString("post_key");
            name.setText(name_);
            question.setText(question_);
            time.setText(time_);
        }else{
            Toast.makeText(Reply_activity.this, "Oops", Toast.LENGTH_SHORT).show();
        }

        r1=db.collection("user").document(uid_);
        r2cu=db.collection("user").document(cu_id);
        databaseReference=firebaseDatabase.getReference("All Replies");
        //-------------------------------

        //-------------------------------
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=replyMsg.getText().toString();
                AnswerMember a=new AnswerMember();
                a.setName(cu_name);
                a.setUrl(cu_url);
                a.setTime(findTime());
                a.setUid(cu_id);
                a.setAnswer(msg);
                String replyloc=databaseReference.child(postkey_).push().getKey();
                a.setReply_key(replyloc);
                databaseReference.child(postkey_).child(replyloc).setValue(a);
                Toast.makeText(Reply_activity.this, "Reply added", Toast.LENGTH_SHORT).show();
               
            }
        });

        //-------------------------------------------------------------------------
        //retreiving data from firebase into replies recyclerView
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference postref=db.getReference().child("All Replies").child(postkey_);

        postref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                // Use the childrenCount as needed
                Toast.makeText((Context) Reply_activity.this, (int) childrenCount +"-----", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });
        //-----------------------------------------------------------------------------
        FirebaseRecyclerOptions<AnswerMember> options=new FirebaseRecyclerOptions.Builder<AnswerMember>()
                .setQuery(postref,AnswerMember.class)
                .build();
        FirebaseRecyclerAdapter<AnswerMember , Reply_item_Viewholder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<AnswerMember, Reply_item_Viewholder>(options) {

                    @NonNull
                    @Override
                    public Reply_item_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_to_ques_item,parent,false);
                        return new Reply_item_Viewholder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull Reply_item_Viewholder holder, int position, @NonNull AnswerMember model) {
                        String name=model.getName();
                        String url=model.getUrl();
                        String time=model.getTime();
                        String uid=model.getUid();
                        String ans=model.getAnswer();
                        String qkey=model.getReply_key();
                        holder.setItem(getApplication() ,name ,uid , ans , url ,time ,qkey);

                        holder.upvotebtn_.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.addRemoveVote(qkey);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        r1.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String url=task.getResult().getString("uri");
                            Picasso.get().load(url).into(asker);
                        }
                    }
                });

        r2cu.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String url=task.getResult().getString("uri");
                            cu_name=task.getResult().getString("name");
                            cu_url=task.getResult().getString("uri");
                            Picasso.get().load(url).into(replier);
                        }
                    }
                });


    }

    public String findTime(){
        Calendar cdate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate=currentdate.format(cdate.getTime());

        Calendar ctime=Calendar.getInstance();
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss");
        final String savetime=currenttime.format(ctime.getTime());
        reply_msg_time=savedate + ":"+savetime;
        return reply_msg_time;
    }
}