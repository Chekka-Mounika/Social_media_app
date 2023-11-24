package com.example.bloodhelp.Adapters;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodhelp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ChatItem_Viewholder extends RecyclerView.ViewHolder{
    public ImageView imageView;
    public TextView send_message , name , status;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String cu_uid=user.getUid();


    public ChatItem_Viewholder(@NonNull View itemView) {
        super(itemView);
    }
    public void setItem(Application activity, String name, String uid, String bio, String url){
        imageView=itemView.findViewById(R.id.ac_chat_profitem_image);
        send_message=itemView.findViewById(R.id.ac_chat_profitem_sendmsg);
        this.name=itemView.findViewById(R.id.ac_chat_profitem_name);
        this.status=itemView.findViewById(R.id.ac_chat_profitem_status);


        if(uid.equals(cu_uid)){
            send_message.setVisibility(View.INVISIBLE);
            this.name.setText(name);
            Picasso.get().load(url).into(imageView);
        }else{
            send_message.setVisibility(View.VISIBLE);
            this.name.setText(name);
            Picasso.get().load(url).into(imageView);
        }
    }

}
