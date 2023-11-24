package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.bloodhelp.DataModels.PostData_member;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatePost_Act extends AppCompatActivity {
    ImageView imageView;
    ProgressBar progressBar;
    EditText decri;
    VideoView videoView;
    Button chooseFile , uploadFile;
    //------------------------------------
    private Uri selectedUri;
    private static final int  PICK_FILE=1;
    //------------------------------------
    String url,name;
    //------------------------------------
    StorageReference storageReference= FirebaseStorage.getInstance().getReference("User posts");

    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String uid=user.getUid();
    //------------------------------------
    MediaController mediaController;
    String type;
    UploadTask uploadTask;
    //------------------------------------
    PostData_member postData_member=new PostData_member();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        mediaController=new MediaController(this);
         imageView=findViewById(R.id.iv_post);
         progressBar=findViewById(R.id.progrssbar_post);
         decri=findViewById(R.id.desc_post);
         videoView=findViewById(R.id.vv_post);
         chooseFile=findViewById(R.id.btn_choose_post);
         uploadFile=findViewById(R.id.btn_add_post);

         //'''''''''''''''''''''''''
        db1=firebaseDatabase.getReference("All images");
        db2=firebaseDatabase.getReference("All videos");
        db3=firebaseDatabase.getReference("All posts");
         //'''''''''''''''''''''''''
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FnChooseFile();
            }
        });


    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void FnChooseFile() {
//        Intent intent=new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/* video/*");
//        startActivityForResult(intent , PICK_FILE);
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/* video/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
//        startActivityForResult(intent, PICK_FILE);
        Intent intent = new Intent();
        intent.setType("*/*"); // Set the MIME type to allow all types of files
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooserIntent = Intent.createChooser(intent, "Select File");
        chooserIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(chooserIntent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==PICK_FILE || resultCode==RESULT_OK || data!=null || data.getData()!=null){
                selectedUri=data.getData();
                if(selectedUri.toString().contains("image")){
                    Picasso.get().load(selectedUri).into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.INVISIBLE);
                    type="iv";
                }else if(selectedUri.toString().contains("video")){
                    videoView.setMediaController(mediaController);
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(selectedUri);
                    videoView.start();
                    type="vv";
                }

            }
        }catch(Exception e){
            Toast.makeText(this , "Error : "+"Please select image",Toast.LENGTH_SHORT).show();
        }
    }
    private void doPost() {

        Calendar cdate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate=currentdate.format(cdate.getTime());
        //------------------------------
        Calendar ctime=Calendar.getInstance();
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss");
        final String savetime=currenttime.format(ctime.getTime());
        String time=savedate + ":"+savetime;
        //------------------------------
        String desc=decri.getText().toString();
        if(!TextUtils.isEmpty(desc) && selectedUri!=null){
            progressBar.setVisibility(View.VISIBLE);
           StorageReference fileRef=storageReference.child(System.currentTimeMillis() + getFileExt(selectedUri));
           uploadTask=fileRef.putFile(selectedUri);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                            if(type.equals("iv")){
                                postData_member.setName(name);
                                postData_member.setUrl(url);
                                postData_member.setUid(uid);
                                postData_member.setTime(time);
                                postData_member.setType("iv");
                                postData_member.setPosturi(downloadUri.toString());
                                postData_member.setDesc(desc);

                                String k=db1.child(uid).push().getKey();
                                postData_member.setPostid(k);
                                db1.child(uid).child(k).setValue(postData_member); //[[[[[[[[[[[[[[[[[[[[[[

                                String j=db3.child(uid).push().getKey();
                                db3.child(uid).child(j).setValue(postData_member);  //[[[[[[[[[[[[[[[[[[[[[
                                Toast.makeText(CreatePost_Act.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }else if(type.equals("vv")){
                                postData_member.setName(name);
                                postData_member.setUrl(url);
                                postData_member.setUid(uid);
                                postData_member.setTime(time);
                                postData_member.setType("vv");
                                postData_member.setPosturi(downloadUri.toString());
                                postData_member.setDesc(desc);

                                String k=db2.child(uid).push().getKey();
                                postData_member.setPostid(k);
                                db2.child(uid).child(k).setValue(postData_member); //[[[[[[[[[[[[[[[[[[[[[[

                                String j=db3.child(uid).push().getKey();
                                db3.child(uid).child(j).setValue(postData_member);  //[[[[[[[[[[[[[[[[[[[[[
                                Toast.makeText(CreatePost_Act.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);


                            }else{
                                Toast.makeText(CreatePost_Act.this, "error", Toast.LENGTH_SHORT).show();
                            }
                    }
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference=firebaseFirestore.collection("user").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    name=task.getResult().getString("name");
                    url=task.getResult().getString("uri");
                }
            }
        });
    }
}