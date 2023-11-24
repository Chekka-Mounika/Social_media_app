
package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bloodhelp.DataModels.QuestionMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskQactivity extends AppCompatActivity {
    EditText editText;
    Button btn;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference AllQ;
    DatabaseReference UserQ;
    //---------------------
    QuestionMember member;
    FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    String current_uid=firebaseUser.getUid();
    //---------------------
    String name,url,privacy,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_qactivity);
        editText=findViewById(R.id.askq_edittext);
        btn=findViewById(R.id.askq_submit);
        documentReference=firebaseFirestore.collection("user").document(current_uid);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String id=user.getUid();
        AllQ=firebaseDatabase.getReference("All ques");
        UserQ=firebaseDatabase.getReference("User ques").child(id);
        member=new QuestionMember();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question=editText.getText().toString();

                Calendar cdate=Calendar.getInstance();
                SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                final String savedate=currentdate.format(cdate.getTime());
                //------------------------------
                Calendar ctime=Calendar.getInstance();
                SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss");
                final String savetime=currenttime.format(ctime.getTime());
                String time=savedate + ":"+savetime;
                //-----------------------------
                if(!TextUtils.isEmpty(question) && !TextUtils.isEmpty(question.trim())){
                    member.setQuestion(question);
                    member.setName(name);
                    member.setUrl(url);
                    member.setUserid(uid);
                    member.setPrivacy(privacy);
                    member.setTime(time);

                    String uq_id=UserQ.push().getKey();
                    UserQ.child(uq_id).setValue(member);

                    String aq_id=AllQ.push().getKey();
                    member.setKey(uq_id);
                    AllQ.child(aq_id).setValue(member);
                    Toast.makeText(AskQactivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AskQactivity.this, "Empty field", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    name=task.getResult().getString("name");
                    url=task.getResult().getString("uri");
                    privacy=task.getResult().getString("privacy");
                    uid=current_uid;
                }
            }
        });
    }
}