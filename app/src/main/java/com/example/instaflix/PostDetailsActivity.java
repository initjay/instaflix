package com.example.instaflix;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instaflix.R;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";

    TextView tvUserName;
    TextView tvCaption;
    ImageView ivProfileImg;
    ImageView ivPostImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUserName = findViewById(R.id.tvUserName);
        tvCaption = findViewById(R.id.tvCaption);
        ivProfileImg = findViewById(R.id.ivProfileImg);
        ivPostImg = findViewById(R.id.ivPostImg);

        final String postId = getIntent().getStringExtra("post_id");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user");
        // First try to find from the cache and only then go to network
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        // Execute the query to find the object with ID
        query.getInBackground(postId, new GetCallback<Post>() {
            public void done(Post post, ParseException e) {
                if (e == null) {
                    // item was found
                    tvCaption.setText(post.getDescription());

                    if (post.getUser().getParseFile("profileimg") != null) {
                        Log.i(TAG, "Profile picture " + post.getUser().getParseFile("profileimg").toString());
                        Glide.with(getApplicationContext()).load(post.getUser().getParseFile("profileimg").getUrl()).centerCrop().into(ivProfileImg);
                    }

                    Glide.with(getApplicationContext()).load(post.getImage().getUrl()).into(ivPostImg);

                    tvUserName.setText(post.getUser().getUsername());


                }
            }
        });
    }
}