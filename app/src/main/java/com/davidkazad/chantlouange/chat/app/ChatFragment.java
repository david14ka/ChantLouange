package com.davidkazad.chantlouange.chat.app;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.OnClick;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ChatFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link OnSendPostListener}.</p>
 */
public class ChatFragment extends BottomSheetDialogFragment {


    private OnSendPostListener onSendPostListener;

    EditText editTextPost;
    ImageView imageViewUser;
    TextView textViewEmail;
    LinearLayout add_image;

    ImageView post;
    TextView post2;
    TextView cancel;
    ProgressBar pbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment_comment, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editTextPost = view.findViewById(R.id.edtPost);
        imageViewUser = view.findViewById(R.id.iUser);
        textViewEmail = view.findViewById(R.id.tEmail);
        add_image = view.findViewById(R.id.layout_add_image);
        post = view.findViewById(R.id.iPost);
        post2 = view.findViewById(R.id.tPost);
        cancel = view.findViewById(R.id.tCancel);
        pbar = view.findViewById(R.id.pbar);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPost();
            }
        });
        post2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPost();
            }
        });

    }

    private void attemptPost() {
        Post post = new Post(editTextPost.getText().toString(),"");
        if (TextUtils.isEmpty(post.getText())){
            editTextPost.setHintTextColor(Color.RED);
            return;
        }

        perfomPost(post);
    }

    private void perfomPost(final Post post) {

        pbar.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "new comment Sent!", Toast.LENGTH_SHORT).show();

        Common.postRef.push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (onSendPostListener != null) {

                    post.add();
                    onSendPostListener.onPostSuccess(post);
                    dismiss();

                }
            }
        });
    }

    @OnClick({R.id.tCancel})
    public void cancel(){
        dismiss();
    }


    public void setOnSendPostListener(OnSendPostListener onSendPostListener) {
        this.onSendPostListener = onSendPostListener;
    }

    public OnSendPostListener getOnSendPostListener() {
        return onSendPostListener;
    }

    public interface OnSendPostListener {
        void onPostSuccess(Post post);
        void onPostFailure(Post post, Throwable t);
    }

}
