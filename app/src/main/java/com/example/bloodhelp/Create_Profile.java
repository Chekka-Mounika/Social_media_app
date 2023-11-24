package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bloodhelp.DataModels.All_userMember;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class Create_Profile extends AppCompatActivity {

    EditText username;
    EditText email_;
    EditText bio_;
    EditText website_;
    Button button;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;

    UploadTask uploadTask;

    StorageReference storageReference;
    //------------------------------------------
    FirebaseDatabase database=FirebaseDatabase.getInstance();   //real time database
    DatabaseReference databaseReference;
    //-------------------------------------------
    FirebaseFirestore db=FirebaseFirestore.getInstance();//cloud storage
    DocumentReference documentReference;
    //-------------------------------------------

    private static  final int PICK_IMAGE=1;
    String current_userid;
    All_userMember member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        member=new All_userMember();

        button=findViewById(R.id.profile_update);
        imageView=findViewById(R.id.profile_image);
        username=findViewById(R.id.profile_username);
        email_=findViewById(R.id.profile_email);
        bio_=findViewById(R.id.profile_bio);
        website_=findViewById(R.id.profile_website);
        progressBar=findViewById(R.id.profile_progressbar);

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        current_userid=firebaseUser.getUid();
        documentReference=db.collection("user").document(current_userid);
        storageReference= FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference=database.getReference("All users");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(i,PICK_IMAGE);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==PICK_IMAGE || resultCode==RESULT_OK || data!=null || data.getData()!=null){
                imageUri=data.getData();
                Picasso.get().load(imageUri).into(imageView);
            }
        }catch(Exception e){
            Toast.makeText(this , "Error : "+"Please select image",Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadData() {

        String uname=username.getText().toString();
        String email=email_.getText().toString();
        String bio=bio_.getText().toString();
        String website=website_.getText().toString();

        if(!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(bio) && !TextUtils.isEmpty(website)
                        && imageUri!=null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
            uploadTask=reference.putFile(imageUri);
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
                        Uri downloadUri=task.getResult();
                        Map<String,String> profile=new HashMap<>();
                        profile.put("name",uname);
                        profile.put("email",email);
                        profile.put("bio",bio);
                        profile.put("uri",downloadUri.toString());
                        profile.put("website",website);
                        profile.put("privacy","Public");

                        member.setName(uname);
                        member.setBio(bio);
                        member.setUrl(downloadUri.toString());
                        member.setUid(current_userid);

                        databaseReference.child(current_userid).setValue(member);
                        documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(Create_Profile.this , "Profile created",Toast.LENGTH_SHORT).show();
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Intent i=new Intent(Create_Profile.this , MainActivity.class);
                                        startActivity(i);

                                    }
                                },2000);
                            }
                        });
                    }
                }
            });

        }else{
            Toast.makeText(this ,"All fields are required" ,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference1;
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String user_id=firebaseUser.getUid();
        documentReference1=FirebaseFirestore.getInstance().collection("user").document(user_id);
        if(documentReference1!=null){
           documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if(task.getResult().exists()){
                       String name_result=task.getResult().getString("name");
                       String bio_result=task.getResult().getString("bio");
                       String semail=task.getResult().getString("email");
                       String sweb=task.getResult().getString("website");
                       String url=task.getResult().getString("uri");
                       Uri ui=Uri.parse(url);
                       Picasso.get().load(ui).into(imageView);
                       username.setText(name_result);
                       bio_.setText(bio_result);
                       email_.setText(semail);
                       website_.setText(sweb);
                      // imageUri=Uri.parse(url);
                   }
               }
           });
        }
    }
}