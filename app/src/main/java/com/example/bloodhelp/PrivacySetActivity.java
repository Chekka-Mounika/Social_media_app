package com.example.bloodhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

public class PrivacySetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextView p_status;
    Spinner p_spinner;
    Button btn;
    String[] status={"Select option","Public","Private"};
    //-------------
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference;
    //------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_set);
        p_status=findViewById(R.id.privacy_status);
        p_spinner=findViewById(R.id.privacy_spinner);
        btn=findViewById(R.id.privacy_setbtn);

        ArrayAdapter arrayAdapter=new ArrayAdapter(this , android.R.layout.simple_spinner_item,status);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        p_spinner.setAdapter(arrayAdapter);
        p_spinner.setOnItemSelectedListener(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePrivacy();

            }
        });
    }

    private void SavePrivacy() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        documentReference=firebaseFirestore.collection("user").document(uid);
        final String value_p=p_spinner.getSelectedItem().toString();
        if(value_p=="Select option"){
            Toast.makeText(PrivacySetActivity.this , "Choose option from dropdown",Toast.LENGTH_SHORT).show();
        }else {
            documentReference.update("privacy",value_p)
                    .addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void  onSuccess(Void aVoid){
                           Toast.makeText(PrivacySetActivity.this , "Privacy rules updated successfully",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){
                            Toast.makeText(PrivacySetActivity.this , "Error in updating rules",Toast.LENGTH_SHORT).show();
                        }
                    });
            }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this , "Please select any field",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        documentReference=firebaseFirestore.collection("user").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String privacy_result=task.getResult().getString("privacy");
                    p_status.setText(privacy_result);
                }
            }
        });
    }
}