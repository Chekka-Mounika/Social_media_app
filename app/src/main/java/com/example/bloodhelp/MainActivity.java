package com.example.bloodhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bloodhelp.navigation_fragments.Fragmemt2;
import com.example.bloodhelp.navigation_fragments.Fragment1;
import com.example.bloodhelp.navigation_fragments.Fragment3;
import com.example.bloodhelp.navigation_fragments.Fragment4;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    Button button;
    FirebaseAuth auth;
    BottomNavigationView bottomNavigationView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Fragment1()).commit();
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_profile) {
            selectedFragment = new Fragment1();
        } else if (itemId == R.id.navigation_home) {
            selectedFragment = new Fragment3();
        } else if (itemId == R.id.navigation_ask) {
            selectedFragment = new Fragmemt2();
        }else if(itemId==R.id.navigation_requests){
            selectedFragment = new Fragment4();
        }
        // It will help to replace the
        // one fragment to other.
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
        }
        return true;
    };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null){
            Intent i=new Intent(MainActivity.this , LoginActivity.class);
            startActivity(i);
            finish();
        }
    }


}