package com.example.bloodhelp.Adapters;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodhelp.DataModels.MessageMember;
import com.example.bloodhelp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MessageChat_Adapter extends RecyclerView.Adapter<MessageChat_Adapter.ViewHolder>{
    List<MessageMember> messageMemberList;
    Context context;
    //-------------------------
    DatabaseReference msgRef;
    String current_uid;
    String others_uid;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    //---------------------------
    int MSG_TYPE_LEFT=0;
    int MSG_TYPE_RIGHT=1;
    //---------------------------
    public MessageChat_Adapter(Context c , List<MessageMember> m , String current_uid , String others_uid){
        this.messageMemberList=m;
        this.context=c;
        this.current_uid=current_uid;
        this.others_uid=others_uid;
        msgRef= firebaseDatabase.getReference().child("Message").child(current_uid).child(others_uid);
    }

    @NonNull
    @Override
    public MessageChat_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.message_layout_left , parent , false);
            return new MessageChat_Adapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.message_layout_right , parent , false);
            return new MessageChat_Adapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageChat_Adapter.ViewHolder holder, int position) {
        MessageMember msg=messageMemberList.get(position);
        holder.setITem(msg);

    }
    @Override
    public int getItemViewType(int position) {
        if(messageMemberList.get(position).getSenderid().equals(current_uid)){
            return MSG_TYPE_LEFT;
        }
        else {
            return MSG_TYPE_RIGHT;
        }
    }

    @Override
    public int getItemCount() {
        return messageMemberList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        ImageView image;
        public ImageButton playaudiobtn;
        LinearLayout linla;


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text= itemView.findViewById(R.id.msg_layout_text);
            image=itemView.findViewById(R.id.msg_layout_img);
            linla=itemView.findViewById(R.id.msg_layout_audio);
            playaudiobtn= itemView.findViewById(R.id.msg_layout_audio_btn);
        }

        public void setITem(MessageMember message){
            String type=message.getType();

                if (type.equals("txt")) {
                 text.setVisibility(View.VISIBLE);
                 image.setVisibility(View.GONE);
                 linla.setVisibility(View.GONE);
                 text.setText(message.getMessage());
                } else if (type.equals("img")) {
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.GONE);
                    linla.setVisibility(View.GONE);
                    Picasso.get().load(message.getMessage()).into(image);
                } else if (type.equals("audio")) {
                    linla.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);

                }


        }
    }
}
