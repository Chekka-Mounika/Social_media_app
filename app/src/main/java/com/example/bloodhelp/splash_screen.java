package com.example.bloodhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class splash_screen extends AppCompatActivity {

    TextView t1,t2;
    ImageView imageView;
    long animeTime=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        t1=findViewById(R.id.textView1);
        t2=findViewById(R.id.textView2);
        imageView=findViewById(R.id.splash_logo);
/*
        ObjectAnimator animatory=ObjectAnimator.ofFloat(imageView,"y",40f);
        ObjectAnimator animatorname=ObjectAnimator.ofFloat(t1,"x",20f);
        animatory.setDuration(animeTime);
        animatorname.setDuration(animeTime);

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animatory,animatorname);
        animatorSet.start();
*/

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(splash_screen.this , LoginActivity.class);
                startActivity(i);
                finish();
            }
        },3000);


    }
}