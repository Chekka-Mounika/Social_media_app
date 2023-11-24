package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.bloodhelp.Adapters.MessageChat_Adapter;
import com.example.bloodhelp.DataModels.MessageMember;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageToUSer extends AppCompatActivity {
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1001;
    RecyclerView recyclerView;
    ImageButton sendbtn , cambtn , micbtn;
    EditText msg;
    TextView typingStatus;
    Boolean typeChecker=false;


    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference root1 , root2 , msgref , typingRef;
    MessageMember member;

    TextView uname;
    ImageView profimg;
    String others_name , others_url , others_id;
    //-----------------------------------------------------------
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String current_uid=user.getUid();
    StorageReference cloudAudio;
    //-----------------------------------------------------------
    private int PICK_IMAGE=1;
    Uri imageUri;
    UploadTask uploadTask;
    //-----------------------------------------------------------
        MediaRecorder mediaRecorder;
        public final String filename="recorded.3gp";
        String file= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + filename;
    //-----------------------------------------------------------
        List<MessageMember> messageList;
        MessageChat_Adapter messageChat_adapter;
    //-----------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_to_user);

         sendbtn=findViewById(R.id.ac_message_sendbtn);
         cambtn=findViewById(R.id.ac_message_camera);
         msg=findViewById(R.id.ac_message_typed_msg);
         uname=findViewById(R.id.ac_message_username);
         profimg=findViewById(R.id.ac_message_profilephoto);
        micbtn=findViewById(R.id.ac_message_mic);
        typingStatus=findViewById(R.id.ac_message_typing_status);


        Bundle bundle=getIntent().getExtras();
        if(bundle !=null){
            others_url=bundle.getString("url");
            others_name=bundle.getString("name");
            others_id=bundle.getString("uid");
            Picasso.get().load(others_url).into(profimg);
            uname.setText(others_name);
        }else{
            Toast.makeText(this, "User missing", Toast.LENGTH_SHORT).show();
        }

        member=new MessageMember();
        recyclerView=findViewById(R.id.ac_message_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageToUSer.this));

        //-----------------------
        root1=firebaseDatabase.getReference().child("Message").child(current_uid).child(others_id);
        root2=firebaseDatabase.getReference().child("Message").child(others_id).child(current_uid);
        typingRef= firebaseDatabase.getReference().child("Typing");
        cloudAudio= FirebaseStorage.getInstance().getReference().child("Message_audios");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        } 
        /*mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(file);*/

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        cambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i , PICK_IMAGE);
            }
        });
        micbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createDialogue();
            }
        });

        typingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(current_uid).hasChild(others_id)){
                    typingStatus.setVisibility(View.VISIBLE);
                }else{
                    typingStatus.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                typeChecker=true;
                typingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(others_id).hasChild(current_uid)){

                        }else{
                            typingRef.child(others_id).child(current_uid).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
                    typingRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            typingRef.child(others_id).child(current_uid).removeValue();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });


    }

    private void createDialogue() {
        LayoutInflater inflater=LayoutInflater.from(MessageToUSer.this);
        View view=inflater.inflate(R.layout.recorder_layout , null);
        TextView status=view.findViewById(R.id.record_layout_status);
        Button start=view.findViewById(R.id.record_layout_start);
        Button stop=view.findViewById(R.id.record_layout_stop);
        Button send_file=view.findViewById(R.id.record_layout_sendfile);

        AlertDialog alertDialog=new AlertDialog.Builder(MessageToUSer.this)
                .setView(view)
                .create();
        alertDialog.show();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    status.setText("Audio recording started............");
                } catch (IOException e) {
                    Toast.makeText(MessageToUSer.this, "Unable to record", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.release();
                status.setText("Recording stopped");
            }
        });

        send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri audiofile=Uri.fromFile(new File(file));
                final StorageReference reference=cloudAudio.child(System.currentTimeMillis()+filename);
                uploadTask=reference.putFile(audiofile);
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
                            member.setType("audio");
                            member.setSenderid(current_uid);
                            member.setRecieverid(others_id);
                            String id=root1.push().getKey();
                            root1.child(id).setValue(member);

                            String id1=root2.push().getKey();
                            root2.child(id1).setValue(member);

                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    Toast.makeText(MessageToUSer.this, "file sent", Toast.LENGTH_SHORT).show();
                                }
                            },1000);
                        }
                    }
                });
            }
        });


    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==PICK_IMAGE || resultCode==RESULT_OK || data!=null || data.getData()!=null){
                imageUri=data.getData();
                String url_=imageUri.toString();
                Intent i=new Intent(MessageToUSer.this , SendImgTochat.class);
                i.putExtra("u" , url_);
                i.putExtra("n",others_name);
                i.putExtra("id",others_id);
                i.putExtra("cuid",current_uid);
                startActivity(i);
            }
        }catch(Exception e){
            Toast.makeText(this , "Error : "+"Please select image",Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onStart() {
        recyclerView=findViewById(R.id.ac_message_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageToUSer.this));
        super.onStart();



         MessageMember m = new MessageMember();
        msgref=FirebaseDatabase.getInstance().getReference().child("Message").child(current_uid).child(others_id);
        messageList=new ArrayList<>();
        msgref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            messageList.clear();
            for(DataSnapshot s : snapshot.getChildren()){
                    MessageMember mi = s.getValue(MessageMember.class);
                    messageList.add(mi);
                }
                messageChat_adapter=new MessageChat_Adapter(MessageToUSer.this , messageList , current_uid , others_id);
                recyclerView.setAdapter(messageChat_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendMessage() {
        String m=msg.getText().toString();
        Calendar cdate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("dd-MMMM-yyyy");
        final String savedate=currentdate.format(cdate.getTime());
        //------------------------------
        Calendar ctime=Calendar.getInstance();
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:ss");
        final String savetime=currenttime.format(ctime.getTime());
        String time=savedate + ":"+savetime;
        //------------------------------

        if(m.isEmpty()){
            Toast.makeText(this, "Empty message", Toast.LENGTH_SHORT).show();
        }else{
            member.setData(savedate);
            member.setTime(savetime);
            member.setMessage(m);
            member.setType("txt");
            member.setSenderid(current_uid);
            member.setRecieverid(others_id);
            String id=root1.push().getKey();
            root1.child(id).setValue(member);

            String id1=root2.push().getKey();
            root2.child(id1).setValue(member);
            msg.setText("");
        }



    }
}