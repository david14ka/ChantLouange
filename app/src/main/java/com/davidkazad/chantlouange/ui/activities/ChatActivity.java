package com.davidkazad.chantlouange.ui.activities;

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
import com.davidkazad.chantlouange.config.Common;
import com.davidkazad.chantlouange.config.SongsApplication;
import com.davidkazad.chantlouange.ui.fragment.ChatFragment;
import com.davidkazad.chantlouange.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    RecyclerView recyclerView;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);

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

        //postList = Post.getList();
        final PostAdapter adapter = new PostAdapter();
        recyclerView.setAdapter(adapter);
/*

        recyclerView.addOnItemTouchListener(new ClickItemTouchListener(recyclerView) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Toast.makeText(ChatActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return false;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
*/

        Common.postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    postList = new ArrayList<>();
                    for (DataSnapshot s :
                            dataSnapshot.getChildren()) {
                        Post post = s.getValue(Post.class);
                        if (post != null) {
                            if (s.getKey().equals("delete")){
                                Post.deleteAll(Post.class);
                            }
                            post.setPid(s.getKey());
                            postList.add(post);
                            //post.add();

                        }

                    }
                }
                Collections.reverse(postList);
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
        ChatFragment chatFragment = new ChatFragment();

        chatFragment.setAllowEnterTransitionOverlap(true);
        chatFragment.setAllowReturnTransitionOverlap(true);
        chatFragment.setOnSendPostListener(new ChatFragment.OnSendPostListener() {

            @Override
            public void onPostSuccess(Post post) {
                Toast.makeText(ChatActivity.this, "new post has been sent: ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPostFailure(Post post, Throwable t) {
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
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_item_post, parent, false);
            PostHolder holder = new PostHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PostHolder holder, int position) {
            Post post = postList.get(position);
            //holder.staffFunction.setText(staff.getUsername());
            holder.setHolder(post);
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }
    }

    private class PostHolder extends RecyclerView.ViewHolder {

        ImageView imageUser;
        TextView textName;
        TextView textContent;
        TextView textlike;
        TextView textComment;
        ImageView imagePictue;
        ImageView like;
        ImageView comment;

        public PostHolder(View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.iUser);
            textName = itemView.findViewById(R.id.username);
            textContent = itemView.findViewById(R.id.content);
            textlike = itemView.findViewById(R.id.like1);
            textComment = itemView.findViewById(R.id.comment1);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            imagePictue = itemView.findViewById(R.id.picture);
        }

        public void setHolder(Post post) {
            ImageLoader.getInstance().displayImage(post.getImage(), imageUser, SongsApplication.displayImageCircleOptions);
            textName.setText(post.getName());
            textContent.setText(post.getText());
            textComment.setText(String.format(getString(R.string.comment), post.getComments()));
            textlike.setText(String.format(getString(R.string.like), post.getLikes()));
            ImageLoader.getInstance().displayImage(post.getImage(), imagePictue);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ChatActivity.this, "like", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }
}
