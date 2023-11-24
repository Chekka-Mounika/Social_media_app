package com.example.bloodhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Try extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth=FirebaseAuth.getInstance();
                auth.signOut();
                Intent i=new Intent(Try.this , LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Chekka").child("Android").setValue("pro");
        FirebaseDatabase.getInstance().getReference().child("Chekka").child("Web").setValue("intermediate");

        HashMap<String , Object> map=new HashMap<>();
        map.put("Name","Mounika");
        map.put("Email","chvsmouniks");
        FirebaseDatabase.getInstance().getReference().child("Chekka").child("web").updateChildren(map);
        // .setValue erases the all fields
        // .updateChildren updares only particular fields



    }
}