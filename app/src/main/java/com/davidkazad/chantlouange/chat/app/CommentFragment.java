package com.davidkazad.chantlouange.chat.app;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.davidkazad.chantlouange.R;
import com.davidkazad.chantlouange.common.Common;
import com.davidkazad.chantlouange.models.Comment;
import com.davidkazad.chantlouange.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pixplicity.easyprefs.library.Prefs;

import butterknife.OnClick;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ChatFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link OnSendPostListener}.</p>
 */
public class CommentFragment extends BottomSheetDialogFragment {


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

        textViewEmail.setText(Prefs.getString("username",getString(R.string.display_name)));
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.sign_in)
                        .input(R.string.firstname, R.string.action_search, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence firstname) {
                                User user = new User("", String.valueOf(firstname));
                                user.store();
                                Prefs.putString("username", String.valueOf(firstname));
                                textViewEmail.setText(firstname);
                                dismiss();

                            }
                        })
                        .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                        .negativeText(R.string.cancel)
                        .positiveText(R.string.finiish)
                        .show();
            }
        });
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
        final Comment comment = new Comment(editTextPost.getText().toString(), "");
        if (TextUtils.isEmpty(comment.getText())) {
            editTextPost.setHintTextColor(Color.RED);
            return;
        }

        String username = Prefs.getString("username", "");

        if (!TextUtils.isEmpty(username)) {
            //comment.add();
            comment.setName(Prefs.getString("username",getString(R.string.no_name)));
            perfomPost(comment);

        }else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.sign_in)
                    .input(R.string.firstname, R.string.action_search, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence firstname) {
                            User user = new User("", String.valueOf(firstname));
                            user.store();
                            Prefs.putString("username", String.valueOf(firstname));
                            comment.setName(String.valueOf(firstname));
                            perfomPost(comment);
                            dismiss();

                        }
                    })
                    .inputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                    .negativeText(R.string.cancel)
                    .positiveText(R.string.finiish)
                    .show();
        }


    }

    private void perfomPost(final Comment comment) {

        pbar.setVisibility(View.VISIBLE);

        Common.commentRef.push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (onSendPostListener != null) {
                    onSendPostListener.onPostSuccess(comment);
                    dismiss();
                }
            }
        });
    }

    @OnClick({R.id.tCancel})
    public void cancel() {
        dismiss();
    }


    public void setOnSendPostListener(OnSendPostListener onSendPostListener) {
        this.onSendPostListener = onSendPostListener;
    }

    public OnSendPostListener getOnSendPostListener() {
        return onSendPostListener;
    }

    public interface OnSendPostListener {
        void onPostSuccess(Comment comment);

        void onPostFailure(Comment comment, Throwable t);
    }

}
