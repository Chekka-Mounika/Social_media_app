package com.example.bloodhelp.Adapters;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodhelp.R;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Feed_post_ViewHolder extends RecyclerView.ViewHolder {
    public ImageView image_prof , imag_post;
    public TextView name , desc , likes , comments , time , likesNumber , commentsNumber;
    public ImageButton likebtn , commentbtn , optionsbtn;
    int likesCount;
    String typeOfMedia;
    String post_id;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference likesRef=firebaseDatabase.getReference("PostLikes");
    public Feed_post_ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setItem(FragmentActivity activity , String name ,String  url ,String  posturi ,String  desc ,String  type ,String  time ,String  uid ,String postid){
        image_prof=itemView.findViewById(R.id.feed_post_layout_profile_image);
        imag_post=itemView.findViewById(R.id.feed_post_layout_imagepst);
        this.name=itemView.findViewById(R.id.feed_post_layout_name);
        this.desc=itemView.findViewById(R.id.feed_post_layout_desc);
        this.likes=itemView.findViewById(R.id.feed_post_layout_likes_number);
        this.comments=itemView.findViewById(R.id.feed_post_layout_comments_number);
        this.time=itemView.findViewById(R.id.feed_post_layout_time);
        this.post_id=postid;
        this.likebtn=itemView.findViewById(R.id.feed_post_layout_likebtn);
        this.commentbtn=itemView.findViewById(R.id.feed_post_layout_commentbtn);
        this.optionsbtn=itemView.findViewById(R.id.feed_post_layout_morebtn);
        this.likesNumber=itemView.findViewById(R.id.feed_post_layout_likes_number);
        this.commentsNumber=itemView.findViewById(R.id.feed_post_layout_comments_number);
        this.typeOfMedia=type;
        SimpleExoPlayer exoPlayer;
        PlayerView playerView=itemView.findViewById(R.id.feed_post_layout_exoplayer);
        if(typeOfMedia != null && typeOfMedia.equals("iv")){
            Picasso.get().load(url).into(image_prof);
            Picasso.get().load(posturi).into(imag_post);
            this.time.setText(time);
            this.desc.setText(desc);
            this.name.setText(name);
            imag_post.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.INVISIBLE);
            LikePostChecker();
        }else if(typeOfMedia != null && typeOfMedia.equals("vv")){
            Picasso.get().load(url).into(image_prof);
            this.time.setText(time);
            this.desc.setText(desc);
            this.name.setText(name);
            imag_post.setVisibility(View.INVISIBLE);
            playerView.setVisibility(View.VISIBLE);


            try{
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(activity);
                exoPlayer = new SimpleExoPlayer.Builder(activity)
                        .setTrackSelector(new DefaultTrackSelector(activity))
                        .build();

                Uri videoUri = Uri.parse(posturi);
                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(activity,
                        Util.getUserAgent(activity, "video"));
                ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem);


                playerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(false);
                LikePostChecker();
            }catch (Exception e){
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }


            //--

        }
    }


    public void LikePostChecker(){

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String cu_id=firebaseUser.getUid();
        likesRef=firebaseDatabase.getReference("PostLikes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(post_id!=null) {
                    if (snapshot.child(post_id).hasChild(cu_id)) {
                        likebtn.setImageResource(R.drawable.baseline_like_24);
                        likesCount = (int) snapshot.child(post_id).getChildrenCount();
                        likesNumber.setText(Integer.toString(likesCount));

                    } else {
                        likebtn.setImageResource(R.drawable.dislike_border);
                        likesCount = (int) snapshot.child(post_id).getChildrenCount();
                        likesNumber.setText(Integer.toString(likesCount));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
