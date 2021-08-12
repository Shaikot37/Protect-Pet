package com.example.protectpet;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.protectpet.fragment.BottomSheetProfileDetailUser;
import com.example.protectpet.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    String commentId;
    private String name,imgurl,userbio;
    private List<User> user_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebasedata;
    private BottomSheetProfileDetailUser bottomSheetProfileDetailUser;

    public CommentsRecyclerAdapter(List<Comments> commentsList, String commentId, List<User> user_list){

        this.commentsList = commentsList;
        this.commentId = commentId;
        this.user_list = user_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item, viewGroup, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context =viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        final String blogCommentId = commentsList.get(i).BlogCommentId;
        String commentMessage = commentsList.get(i).getMessage();
        viewHolder.setComment_message(commentMessage);


        final String blogPostId = commentsList.get(i).BlogCommentId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        final String blog_user_id = commentsList.get(i).getUser_id();

        //firebasedata = FirebaseDatabase.getInstance();
        //DatabaseReference fire = firebasedata.getReference("users/"+ blog_user_id).child("username");

        DatabaseReference fire = FirebaseDatabase.getInstance().getReference("Users").child(blog_user_id);

        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                name = user.getUsername();
                Log.d(name,"name");
                imgurl = user.getImageURL();
                userbio = user.getBio();

                //Glide.with(context.getApplicationContext()).load(user.getImageURL()).into(profile_image);


                //readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //final String name = fire.getKey();

        final String userName = commentsList.get(i).getUsername();
        final String userImage = commentsList.get(i).getImageURL();
        final String bio = commentsList.get(i).getBio();

        try {
            long millisecond = commentsList.get(i).getTimestamp().getTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String dateString = df.format("MM/dd/yyyy HH:mm", new Date(millisecond)).toString();
            viewHolder.setTime(dateString);
        } catch (Exception e) {

           // Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        firebaseFirestore.collection("Posts/" + commentId + "/Comments").addSnapshotListener((Activity) context,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {

                    int count = documentSnapshots.size();
                    viewHolder.updateCommentsCount(count);


                } else {
                  //   viewHolder.updateCommentsCount(0);
                }
            }

        });
        viewHolder.setUserData(name,imgurl);

try {
    viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bottomSheetProfileDetailUser = new BottomSheetProfileDetailUser(name, imgurl, userbio, context, blog_user_id);
            FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            bottomSheetProfileDetailUser.show(manager, "edit");
        }
    });
}
catch (Exception f){}


    }

    @Override
    public int getItemCount() {
        if(commentsList != null) {

            return commentsList.size();

        }  else {

            return 0;

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView blogCommentsCount;
        private TextView comment_date;
        private TextView comment_message;
        private ImageView imageView;
        private TextView u_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;
        }
        public void setComment_message(String message){


            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }
        public void updateCommentsCount(int count){

            //blogCommentsCount = mView.findViewById(R.id.blog_comment_count);
            //blogCommentsCount.setText(count+"Comments");

        }
        public void setTime(String date){
            comment_date = mView.findViewById(R.id.comment_date);
            comment_date.setText(date);
        }

        public void setUserData(String name, String image){

            imageView = mView.findViewById(R.id.comment_image);
            u_name = mView.findViewById(R.id.user);

            u_name.setText(name);

            Glide.with(context).load(image).into(imageView);

        }
    }
}
