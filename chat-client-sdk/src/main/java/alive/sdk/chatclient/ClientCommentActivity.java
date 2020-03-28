package alive.sdk.chatclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.MainActivity;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.events.ClickItemTouchListener;
import com.davidkazad.chantlouange.fragment.PostFragment;
import com.davidkazad.chantlouange.models.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientCommentActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";
    @BindView(R.id.recycler_post)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        navigationDrawer(savedInstanceState,null);

        initView();

        if (getIntent().getBooleanExtra(MainActivity.EXTRA_WRITE_POST,false)){
            writePost(null);
        }

        if (getIntent().getBooleanExtra(BaseActivity.EXTRA_COMMUNITY, false)){
            toolbar.setTitle(R.string.community);
        }
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        PostAdapter adapter = new PostAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new ClickItemTouchListener(recyclerView) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                Toast.makeText(PostActivity.this, "Hello", Toast.LENGTH_SHORT).show();
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

    }


    public void joinGroup(View view) {
        //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void writePost(View view) {
        PostFragment postFragment = new PostFragment();


        postFragment.setAllowEnterTransitionOverlap(true);
        postFragment.setAllowReturnTransitionOverlap(true);
        postFragment.setOnSendPostListener(new PostFragment.OnSendPostListener() {

            @Override
            public void onPostSuccess(Post post) {
                Toast.makeText(PostActivity.this, "clicked" +
                        "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPostFailure(Post post, Throwable t) {
                Log.d(TAG, "onPostFailure: "+t.getLocalizedMessage());
            }
        });

        postFragment.show(getSupportFragmentManager(), postFragment.getTag());
    }


    /*public void staff(View view) {
     *//*Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));*//*
    }*/

    public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_post, parent, false);
            PostHolder holder = new PostHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PostHolder holder, int position) {
            Post post = Common.posts.get(position);
            //holder.staffFunction.setText(staff.getUsername());
            holder.setHolder(post);
        }

        @Override
        public int getItemCount() {
            return Common.posts.size();
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

            ButterKnife.bind(itemView);
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
            ImageLoader.getInstance().displayImage(post.getImage(),imageUser);
            textName.setText(post.getName());
            textContent.setText(post.getText());
            textComment.setText(String.format(getString(R.string.comment), post.getComment()));
            textlike.setText(String.format(getString(R.string.like), post.getLikes()));
            if (post.getPhoto()!=null) {

                ImageLoader.getInstance().displayImage(post.getPhoto(), imagePictue);
            }
        }
    }
}
