package com.example.instaflix;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.post(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    // Must add 'implements View.OnCLickListener' to enable onClick function
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUserName;
        private ImageView ivImage;
        private TextView tvDescription;
        private ImageView ivProfileImg;
        private TextView tvTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfileImg = itemView.findViewById(R.id.ivProfileImg);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);

            // Make post clickable to go to post details activity
            itemView.setOnClickListener(this);
        }

        public void post(Post post) {
            //Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUserName.setText(post.getUser().getUsername());
            tvTimeStamp.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));
            ParseFile image = post.getImage();

            if (image != null) { // only load image into imageview if valid image file is present
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage); // how to load image into imageview
            }

            if (post.getUser().getParseFile("profileimg") != null) {
                Log.i("Parse Adapter", "Profile picture " + post.getUser().getParseFile("profileimg").toString());
                Glide.with(context).load(post.getUser().getParseFile("profileimg").getUrl()).circleCrop().into(ivProfileImg);
            }
        }

        // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
        public String getRelativeTimeAgo(String rawJsonDate) {
            String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }


        @Override
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();
            // make sure the position is valid - exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position
                Post post = posts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);
                // pass post ParseObject to other activity
                intent.putExtra("post_id", post.getObjectId());
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
