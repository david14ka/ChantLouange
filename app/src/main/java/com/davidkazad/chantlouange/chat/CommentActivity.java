package com.davidkazad.chantlouange.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.activities.BaseActivity;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.config.SongsApplication;
import com.davidkazad.chantlouange.models.Comment;
import com.davidkazad.chantlouange.utils.TimeAgo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

public class CommentActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    private List<Comment> commentList;
    private TextView textComment;
    private String username;
    private ProgressBar progressBar;
    private ProgressBar progressSend;

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

        progressBar =  findViewById(R.id.progress_bar);
        progressSend =  findViewById(R.id.progressSend);
        textComment = findViewById(R.id.textComment);
        username = Prefs.getString("username", "");

        initView();
    }

    private void initView() {

        progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recycler_post);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final CommentAdapter adapter = new CommentAdapter();

        Common.commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    commentList = new ArrayList<>();
                    for (DataSnapshot s :
                            dataSnapshot.getChildren()) {
                        Comment comment = s.getValue(Comment.class);
                        if (comment != null) {

                            //Post.deleteAll(Post.class);

                            comment.setCid(s.getKey());
                            commentList.add(comment);
                            //comment.add();
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);

                if (commentList!=null){
                    Collections.reverse(commentList);
                    //adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void joinGroup(View view) {
        joinGroup();
        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void setUser(View view) {

         String name = (username.isEmpty())? getString(R.string.identification) : username;

        new MaterialDialog.Builder(view.getContext())
                .title(R.string.identification)
                .input(getString(R.string.username),name,  false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        username = String.valueOf(input);
                        Prefs.putString("username", String.valueOf(input));
                    }
                })

                .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .negativeText(R.string.cancel)
                .positiveText(R.string.enregistrer)
                .show();

    }

    public void sendComment(View view) {

        if (username.isEmpty()) setUser(view);

        if (!TextUtils.isEmpty(textComment.getText().toString())) {
            Comment comment = new Comment();
            comment.setName(Prefs.getString("username", ""));
            comment.setText(textComment.getText().toString());
            //comment.setPhoto("https://developer.maishapay.net/presenter_assets/images/icon-logo.png");
           // comment.setImage("https://developer.maishapay.net/presenter_assets/images/icon-logo.png");
            comment.setDate(new Date().toString());

            progressSend.setVisibility(View.VISIBLE);
            view.setEnabled(false);

            Common.commentRef.push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    view.setEnabled(true);
                    textComment.setText("");
                    progressSend.setVisibility(View.GONE);
                    initView();
                    Toast.makeText(CommentActivity.this, "new comment has been sent!: ", Toast.LENGTH_SHORT).show();

                }
            });
        }else Toast.makeText(this, getString(R.string.comment_is_empty), Toast.LENGTH_SHORT).show();
    }

    public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_item_comment, parent, false);
            CommentViewHolder holder = new CommentViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            //holder.staffFunction.setText(staff.getUsername());
            holder.setHolder(comment);
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView imageUser;
        TextView textName;
        TextView date;
        TextView textContent;
        ImageView imagePictue;

        public CommentViewHolder(View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.iUser);
            textName = itemView.findViewById(R.id.username);
            textContent = itemView.findViewById(R.id.content);
            imagePictue = itemView.findViewById(R.id.picture);
            date = itemView.findViewById(R.id.date);
        }

        public void setHolder(final Comment post) {
            ImageLoader.getInstance().displayImage(post.getPhoto(), imageUser, SongsApplication.displayImageCircleOptions);
            textName.setText(post.getName());
            textContent.setText(post.getText());
            try {
                TimeAgo timeAgo = new TimeAgo(getApplicationContext());
                //String result = timeAgo.getTimeAgo(new Date(post.getDate()));
                date.setText(timeAgo.using(post.getSentAt()));

            } catch (Exception e) {

            }
            ImageLoader.getInstance().displayImage(post.getImage(), imagePictue);

        }
    }
}
