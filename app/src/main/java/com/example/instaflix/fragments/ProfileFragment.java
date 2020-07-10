package com.example.instaflix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instaflix.LoginActivity;
import com.example.instaflix.Post;
import com.example.instaflix.PostsAdapter;
import com.example.instaflix.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private List<Post> allPosts;
    private TextView tvUserName;
    private ParseUser currUser;
    private ImageView ivProfileimg;

    public ProfileFragment() {} // required empty public constructor

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currUser = ParseUser.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //setHasOptionsMenu(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        ParseUser.logOut();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                }

                return false;
            }
        });

        rvPosts = view.findViewById(R.id.rvProfile);
        tvUserName = view.findViewById(R.id.tvUserName);
        ivProfileimg = view.findViewById(R.id.ivProfileimg);

        tvUserName.setText(currUser.getUsername());

        // TODO Get profile picture
        // User not considered parse object so cannot call getParseFile???
        if (currUser.getParseFile("profileimg") != null) {
            Log.i(TAG, "Profile picture " + currUser.getParseFile("profileimg").toString());
            Glide.with(this).load(currUser.getParseFile("profileimg").getUrl()).centerCrop().into(ivProfileimg);
        }

        allPosts = new ArrayList<>(); // allPosts is the data source
        adapter = new PostsAdapter(getContext(), allPosts);
        // Steps to use the recycler view:
        // 0. create layout for one row in the list -> item_post.xml
        // 1. create the adapter -> PostsAdapter.java
        // 2. create the data source -> allPosts list above
        // 3. set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts(); // -> update data source of new data
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
