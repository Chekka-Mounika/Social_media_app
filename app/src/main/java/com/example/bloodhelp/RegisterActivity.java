package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEt , passEt , confirmEt;
    Button register_btn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt=findViewById(R.id.register_email_et);
        passEt=findViewById(R.id.register_password_et);
        confirmEt=findViewById(R.id.register_confirm_password_et);
        register_btn=findViewById(R.id.button_register);
        progressBar=findViewById(R.id.progress_bar_register);
        checkBox=findViewById(R.id.register_checkbox);
        mAuth=FirebaseAuth.getInstance();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=emailEt.getText().toString();
                String password=passEt.getText().toString();
                String confirm_pass=confirmEt.getText().toString();
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_pass)){
                    if(password.equals(confirm_pass)){
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    sendToLogin();
                                }else{
                                    progressBar.setVisibility(View.INVISIBLE);

                                    String error=task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error : "+error,Toast.LENGTH_SHORT).show();
                                }
                            }


                        });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Password and confirm password \n did not matched",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this , "All fields are required" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void sendToMain() {
        Intent i=new Intent(RegisterActivity.this , MainActivity.class);
        startActivity(i);
        finish();
    }
    private void sendToLogin(){
        Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
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