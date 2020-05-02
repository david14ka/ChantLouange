package com.davidkazad.chantlouange.chat.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.MainActivity;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.BaseActivity;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.config.SongsApplication;
import com.davidkazad.chantlouange.models.Comment;
import com.davidkazad.chantlouange.models.Post;
import com.davidkazad.chantlouange.utils.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public class CommentActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    RecyclerView recyclerView;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.comment_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        navigationDrawer(savedInstanceState, null);

        initView();

        if (getIntent().getBooleanExtra(MainActivity.EXTRA_WRITE_POST, false)) {
            writePost(null);
        }

        if (getIntent().getBooleanExtra(BaseActivity.EXTRA_COMMUNITY, false)) {
            toolbar.setTitle(R.string.community);
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_post);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        commentList = Comment.getList();
        final PostAdapter adapter = new PostAdapter();
        recyclerView.setAdapter(adapter);

        Common.commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    commentList = new ArrayList<>();
                    for (DataSnapshot s :
                            dataSnapshot.getChildren()) {
                        Comment comment = s.getValue(Comment.class);
                        if (comment != null) {
                            if (s.getKey().equals("delete")) {
                                Post.deleteAll(Post.class);
                            }
                            comment.setCid(s.getKey());
                            commentList.add(comment);
                            comment.add();

                        }

                    }
                }
                Collections.reverse(commentList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void joinGroup(View view) {
        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void writePost(View view) {

        CommentFragment chatFragment = new CommentFragment();

        chatFragment.setOnSendPostListener(new CommentFragment.OnSendPostListener() {

            @Override
            public void onPostSuccess(Comment comment) {
                initView();
                Toast.makeText(CommentActivity.this, "new comment has been sent!: ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPostFailure(Comment comment, Throwable t) {
                Log.d(TAG, "onPostFailure: " + t.getLocalizedMessage());
            }
        });

        chatFragment.show(getSupportFragmentManager(), chatFragment.getTag());
    }


    /*public void staff(View view) {
     *//*Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));*//*
    }*/

    public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_item_comment, parent, false);
            PostHolder holder = new PostHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PostHolder holder, int position) {
            Comment comment = commentList.get(position);
            //holder.staffFunction.setText(staff.getUsername());
            holder.setHolder(comment);
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }
    }

    private class PostHolder extends RecyclerView.ViewHolder {

        ImageView imageUser;
        TextView textName;
        TextView date;
        TextView textContent;
        ImageView imagePictue;

        public PostHolder(View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.iUser);
            textName = itemView.findViewById(R.id.username);
            textContent = itemView.findViewById(R.id.content);
            imagePictue = itemView.findViewById(R.id.picture);
            date = itemView.findViewById(R.id.date);
        }

        public void setHolder(final Comment post) {
            ImageLoader.getInstance().displayImage(post.getImage(), imageUser, SongsApplication.displayImageCircleOptions);
            textName.setText(post.getName());
            textContent.setText(post.getText());
            try {
                TimeAgo timeAgo = new TimeAgo(getApplicationContext());
                //String result = timeAgo.getTimeAgo(new Date(post.getDate()));
                date.setText(timeAgo.using(post.getSentAt()));

            } catch (Exception e) {

            }
            ImageLoader.getInstance().displayImage(post.getImage(), imagePictue);

            //ImageLoader.getInstance().displayImage(post.getImage(), imagePictue);
        }
    }
}
