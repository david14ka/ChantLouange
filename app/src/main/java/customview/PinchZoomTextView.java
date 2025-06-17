package customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


import com.pixplicity.easyprefs.library.Prefs;

public class PinchZoomTextView extends TextView {

    private TextView textView;
    /**
     * Consider each "step" between the two pointers as 200 px. In other words, the TV size will grow
     * every 200 pixels.
     */
    private static final float STEP = 200;

    /**
     * The ratio of the text size compared to its original.
     */
    private float ratio = 1.0f;

    /**
     * The distance between the two pointers when they are first placed on the screen.
     */
    private int baseDistance;

    /**
     * The ratio of the text size when the gesture is started.
     */
    private float baseRatio;

    /**
     * Boolean flag for whether or not zoom feature is enabled. Defaults to true.
     */
    private boolean zoomEnabled = true;

    /**
     * Default constructor.
     */
    public PinchZoomTextView(Context context) {
        this(context, null);
        textView = this;
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resizeTextDialog();
                return false;

            }
        });
    }

    /**
     * Default constructor.
     */
    public PinchZoomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textView = this;
        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resizeTextDialog();
                return false;

            }
        });
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);

    }
    private void resizeTextDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setMessage("Taille du Text"); //Message here

        final SeekBar seekBar = new SeekBar(this.getContext());
        seekBar.setProgress((int) textView.getTextSize());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setTextSize(progress / 2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        alert.setView(seekBar);


        alert.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Prefs.putFloat("TextSize", textView.getTextSize());
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();

        alertDialog.show();
    }

    /**
     * Handles the touch event by the user and determines whether font size should grow,
     * and by how much.
     *
     * If the action is simply `POINTER_DOWN` it means the user is just setting their fingers down,
     * so collect base values.
     *
     * Otherwise, the user is pinching, so get the distance between the pointers, find the ratio
     * we need, and set the text size. Note: based on an initial size of 13, and can't exceed a ratio
     * of 1024.
     *
     * Inspiration taken from: http://stackoverflow.com/a/20303367/3131147
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPaintFlags(getPaintFlags() | (Paint.LINEAR_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPaintFlags(getPaintFlags() & ~(Paint.LINEAR_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG));
                break;
        }*/

        // Must have two gestures.
        if(zoomEnabled && event.getPointerCount() == 2) {
            int action = event.getAction();
            int distance = getDistance(event);
            int pureAction = action & MotionEvent.ACTION_MASK;
            if (pureAction == MotionEvent.ACTION_POINTER_DOWN) {
                baseDistance = distance;
                baseRatio = ratio;

                resizeTextDialog();

            } else {
                /*float delta = (distance - baseDistance) / STEP;
                float multi = (float) Math.pow(2, delta);
                ratio = Math.min(1024.0f, Math.max(0.1f, baseRatio * multi));
                //ratio = Math.max(0.1f, Math.min(ratio, 10.0f));
                if (ratio < 18.380517f) {
                    setTextSize(18.380517f);
                    ratio=18.380517f;
                }
                else setTextSize(ratio);

                Prefs.putFloat("TextSize",ratio);

                Log.d(TAG, "onTouchEvent: "+ratio);*/

            }
        }

        return true;
    }

    /**
     * Returns the distance between two pointers on the screen.
     */
    private int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    /**
     * Sets the enabled state of the zoom feature.
     */
    public void setZoomEnabled(boolean enabled) {
        this.zoomEnabled = enabled;
    }

    /**
     * Returns the enabled state of the zoom feature.
     */
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }
}