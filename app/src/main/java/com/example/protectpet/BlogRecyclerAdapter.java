package com.example.protectpet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.protectpet.fragment.BottomSheetProfileDetailUser;
import com.example.protectpet.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private List<BlogPost> blog_list;
    private List<User> user_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BottomSheetProfileDetailUser bottomSheetProfileDetailUser;
    public Context context1;
    private Double latitude, longitude;

    public BlogRecyclerAdapter(List<BlogPost> blog_list, List<User> user_list) {
        this.user_list = user_list;
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context1 = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final String blogPostId = blog_list.get(i).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        String desc_data = blog_list.get(i).getDesc();
        viewHolder.setDescText(desc_data);

        String address = blog_list.get(i).getAddress();
        viewHolder.setAddress(address);

        String post_type = blog_list.get(i).getPost_category();
        viewHolder.setPost_category(post_type);

        String location = blog_list.get(i).getLocation();
        viewHolder.setLocation(location);

        final String image_url = blog_list.get(i).getImage_url();
        final String thumbUri = blog_list.get(i).getImage_thumb();
        viewHolder.setBlogImage(image_url, thumbUri);


        String blog_user_id = blog_list.get(i).getUser_id();

        latitude = blog_list.get(i).getLatitude();
        longitude = blog_list.get(i).getLongitude();


        final String userName = user_list.get(i).getUsername();
        final String userImage = user_list.get(i).getImageURL();
        viewHolder.setUserData(userName, userImage);
        final String bio = user_list.get(i).getBio();



        try {
            long millisecond = blog_list.get(i).getTimestamp().getTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String dateString = df.format("dd/MM/yyyy", new Date(millisecond)).toString();
            viewHolder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context1, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //get likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener((Activity) context1, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {

                    int count = documentSnapshots.size();
                    viewHolder.updateLikesCount(count);
                } else {
                    viewHolder.updateLikesCount(0);
                }
            }

        });


        //get likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener((Activity) context1, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    viewHolder.blogLikebtn.setImageDrawable(context1.getDrawable(R.mipmap.action_like_accent));
                } else {
                    viewHolder.blogLikebtn.setImageDrawable(context1.getDrawable(R.mipmap.action_like_gray));

                }
            }
        });
        //likes feature
        viewHolder.blogLikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.getResult().exists()) {
                                    Map<String, Object> likeMap = new HashMap<>();
                                    likeMap.put("timestamp", FieldValue.serverTimestamp());
                                    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likeMap);
                                } else {
                                    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                                }
                            }
                        });


            }
        });
        viewHolder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context1, CommentsActivity.class);
                in.putExtra("blog_post_id", blogPostId);
                in.putExtra("URL", image_url);
                context1.startActivity(in);
            }
        });

        viewHolder.ad_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locmap = new Intent(context1, AddressMap.class);
                locmap.putExtra("latitude", latitude);
                locmap.putExtra("longitude", longitude);
                context1.startActivity(locmap);
            }
        });



        viewHolder.blog_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context1, CommentsActivity.class);
                intent.putExtra("URL", image_url);
                intent.putExtra("blog_post_id", blogPostId);
                context1.startActivity(intent);
            }
        });


        viewHolder.blogUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context1, ChatActivity.class);
                context1.startActivity(intent);
            }
        });

        viewHolder.blogUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetProfileDetailUser = new BottomSheetProfileDetailUser(userName, userImage, bio, context1);
                FragmentManager manager = ((AppCompatActivity) context1).getSupportFragmentManager();
                bottomSheetProfileDetailUser.show(manager, "edit");
            }
        });

        viewHolder.blogUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetProfileDetailUser = new BottomSheetProfileDetailUser(userName, userImage, bio, context1);
                FragmentManager manager = ((AppCompatActivity) context1).getSupportFragmentManager();
                bottomSheetProfileDetailUser.show(manager, "edit");
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mview;
        private TextView descView, addressView;
        private ImageView blog_image_view;
        private TextView blogDate;
        private TextView blogUserName;
        private CircleImageView blogUserImage;
        private ImageView blogLikebtn;
        private TextView blogLikecount;
        private ImageView ad_map;
        private ImageView blogCommentBtn;
        private Button blogDelBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            blogLikebtn = mview.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mview.findViewById(R.id.blog_comment_icon);
            blogDelBtn = mview.findViewById(R.id.blog_delete_btn);
            ad_map = mview.findViewById(R.id.address_map);
            blog_image_view = mview.findViewById(R.id.blog_image);

        }

        public void setDescText(String descText){
            descView = mview.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }
        public void setBlogImage(String downloadUri, String thumbUri){


            blog_image_view = mview.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            //requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context1).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context1).load(thumbUri)
            ).into(blog_image_view);


            /*

            Picasso.with(context1)
                    .load(downloadUri)
                    .into(blog_image_view);
                    */

        }

        public void setUserData(String name, String image){

            blogUserImage = mview.findViewById(R.id.blog_user_image);
            blogUserName = mview.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            Glide.with(context1).load(image).into(blogUserImage);

        }
    public void setTime(String date){
      blogDate = mview.findViewById(R.id.blog_date);
      blogDate.setText(date);
    }
        public void updateLikesCount(int count){

            blogLikecount = mview.findViewById(R.id.blog_like_count);
            blogLikecount.setText(count + " Likes");

        }

        public void setPost_category(String post_type) {

        }

        public void setLocation(String location) {

        }

        public void setAddress(String address) {
            addressView = mview.findViewById(R.id.address);
            addressView.setText(address);
        }
    }

    }
