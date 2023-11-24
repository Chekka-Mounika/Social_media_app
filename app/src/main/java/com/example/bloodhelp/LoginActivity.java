package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView textView;
    EditText emailEt , passEt ;
    Button login_btn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    CheckBox checkBox;
    TextView forgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView=findViewById(R.id.register_redirect);
        forgot=findViewById(R.id.login_forgotpass);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(i);
            }
        });

        emailEt=findViewById(R.id.login_email_et);
        passEt=findViewById(R.id.login_password_et);
        progressBar=findViewById(R.id.progress_bar_register);
        checkBox=findViewById(R.id.login_checkbox);
        login_btn=findViewById(R.id.button_login);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   // confirmEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                   // confirmEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        mAuth=FirebaseAuth.getInstance();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEt.getText().toString();
                String password=passEt.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);
                                sendToMain();}
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                                String error=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error : "+error,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPass();
            }
        });
    }

    private void forgotPass() {
        String email=emailEt.getText().toString();
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Forgot password")
                .setMessage("Do you want t o send verificaton email to your account")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(LoginActivity.this, "Failed to send email. Enter correct email", Toast.LENGTH_SHORT).show();
                           }
                       });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create();
        builder.show();
    }

    private void sendToMain() {
        Intent i=new Intent(LoginActivity.this , MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            sendToMain();
        }
    }
}