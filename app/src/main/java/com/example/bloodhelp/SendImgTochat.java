package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloodhelp.DataModels.MessageMember;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SendImgTochat extends AppCompatActivity {

    ImageView image ;
    Button btn;
    ProgressBar progressBar;
    TextView textView;

    String imgUril , others_name , others_id , current_id;
    MessageMember member;
    Uri imguri;

    StorageReference cloud;
    DatabaseReference root1 , root2;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    UploadTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_img_tochat);
        image=findViewById(R.id.send_img_chat_iv);
        btn=findViewById(R.id.send_img_chat_btn);
        progressBar=findViewById(R.id.send_img_chat_progressbar);
        textView=findViewById(R.id.tt);


        cloud= FirebaseStorage.getInstance().getReference().child("Message_images");

        Bundle bundle= getIntent().getExtras();
        if(bundle !=null){
            others_name=bundle.getString("n");
            others_id=bundle.getString("id");
            current_id=bundle.getString("cuid");
            imgUril=bundle.getString("u");
        }else{
            Toast.makeText(this, "User missing", Toast.LENGTH_SHORT).show();
        }

        Picasso.get().load(imgUril).into(image);
        imguri=Uri.parse(imgUril);
        root1=firebaseDatabase.getReference().child("Message").child(current_id).child(others_id);
        root2=firebaseDatabase.getReference().child("Message").child(others_id).child(current_id);
        member=new MessageMember();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendImg();
            }
        });

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void sendImg() {
        if(imguri!=null){
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            final StorageReference reference=cloud.child(System.currentTimeMillis()+"."+getFileExt(imguri));
            uploadTask=reference.putFile(imguri);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl=task.getResult();
                        Calendar cdate=Calendar.getInstance();
                        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
                        final String savedate=currentdate.format(cdate.getTime());
                        //------------------------------
                        Calendar ctime=Calendar.getInstance();
                        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss");
                        final String savetime=currenttime.format(ctime.getTime());
                        String time=savedate + ":"+savetime;
                        member.setData(savedate);
                        member.setTime(savetime);
                        member.setMessage(downloadUrl.toString());
                        member.setType("img");
                        member.setSenderid(current_id);
                        member.setRecieverid(others_id);
                        String id=root1.push().getKey();
                        root1.child(id).setValue(member);

                        String id1=root2.push().getKey();
                        root2.child(id1).setValue(member);
                        textView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(SendImgTochat.this, "Image sent. You can go back now", Toast.LENGTH_SHORT).show();


                    }

                }
            });
        }else{
            Toast.makeText(this, "Please select something", Toast.LENGTH_SHORT).show();
        }
    }
}