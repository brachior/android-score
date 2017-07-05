package net.brach.android.score;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScoreView extends RelativeLayout {
    // text style
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    private final TextView label;
    private final ProgressBar progressBar;
    private final AnimatorSet anim;
    private final int duration;

    private final Score score;

    public ScoreView(Context context) {
        this(context, null);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScoreView, 0, 0);
        int padding = (int) a.getDimension(R.styleable.ScoreView_lvl_padding, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, metrics));
        int drawable = a.getResourceId(R.styleable.ScoreView_lvl_drawable, R.drawable.progress);
        duration = a.getInteger(R.styleable.ScoreView_lvl_anim_duration, 1000);

        float fontSize = a.getDimension(R.styleable.ScoreView_lvl_fontSize, 38);
        int fontColor = a.getColor(R.styleable.ScoreView_lvl_fontColor, Color.WHITE);
        int typefaceIndex = a.getInt(R.styleable.ScoreView_lvl_typeface, -1);
        String fontFamily = a.getString(R.styleable.ScoreView_lvl_fontFamily);
        int textStyleIndex = a.getInt(R.styleable.ScoreView_lvl_textStyle, 0);

        score = new Score(
                a.getInteger(R.styleable.ScoreView_lvl_level, 0),
                a.getInteger(R.styleable.ScoreView_lvl_percent, 0));

        int plevel = a.getInteger(R.styleable.ScoreView_lvl_level_preview, 0);
        int pprogress = a.getInteger(R.styleable.ScoreView_lvl_progress_preview, 0);
        a.recycle();

        if (typefaceIndex != -1) {
            fontFamily = null;
        }

        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.progress, this, true);

        progressBar = (ProgressBar) findViewById(R.id.level_progress);
        label = (TextView) findViewById(R.id.level_label);

        // init percent
        progressBar.setPadding(padding, padding, padding, padding);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(context, drawable));
        progressBar.setProgress(score.percent);

        // init text
        label.setPadding(padding, padding, padding, padding);
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        label.setTextColor(fontColor);
        label.setText(String.valueOf(score.level));
        setTypeface(label, fontFamily, textStyleIndex, typefaceIndex);

        // init animation
        anim = new AnimatorSet();
        ObjectAnimator shake = ObjectAnimator.ofFloat(label, "rotation", -5f, 5f);
        shake.setRepeatMode(ValueAnimator.REVERSE);
        shake.setRepeatCount(10);
        shake.setDuration(duration / 10);
        ObjectAnimator zoomIn = ObjectAnimator.ofPropertyValuesHolder(label,
                PropertyValuesHolder.ofFloat("scaleX", 2.f),
                PropertyValuesHolder.ofFloat("scaleY", 2.f));
        zoomIn.setDuration(duration);
        ObjectAnimator zoomOut = ObjectAnimator.ofPropertyValuesHolder(label,
                PropertyValuesHolder.ofFloat("scaleX", 1.f),
                PropertyValuesHolder.ofFloat("scaleY", 1.f));
        zoomOut.setDuration(duration);

        anim.play(zoomOut).after(zoomIn);
        anim.play(shake).with(zoomIn);
        anim.setTarget(label);

        if (isInEditMode()) {
            progressBar.setProgress(pprogress);
            label.setText(String.valueOf(plevel));
        }
    }

    public boolean init(Score score, boolean animate) {
        return update(score.level, score.percent, animate);
    }

    public boolean update(final int level, final int progress, boolean animate) {
        final int nbLevel = level - score.level;

        score.update(level, progress);

        if (animate) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < nbLevel; i++) {
                        animateProgress(duration / (nbLevel + 1), 100);
                    }
                    animateProgress(duration / (nbLevel + 1), progress);

                    label.setText(String.valueOf(level));

                    if (nbLevel > 0) {
                        anim.start();
                    }
                }
            });
        } else {
            noAnimationProgress();
        }

        return nbLevel > 0;
    }

    public int getLevel() {
        return score.level;
    }

    public int getPercent() {
        return score.percent;
    }

    public Score getScore() {
        return new Score(score.level, score.percent);
    }

    /*************/
    /** private **/
    /*************/

    private void noAnimationProgress() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                label.setText(String.valueOf(score.level));
                progressBar.setProgress(score.percent);
            }
        });
    }

    private ValueAnimator previous;

    private void animateProgress(final int duration, final int percent) {
        if (previous != null) {
            previous.cancel();
        }
        previous = ValueAnimator.ofInt(progressBar.getProgress(), percent);
        previous.setDuration(duration);
        previous.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressBar.setProgress((int) animation.getAnimatedValue());
            }
        });
        previous.start();
    }

    private void setTypeface(TextView textView, String fontFamily, int textStyleIndex, int typefaceIndex) {
        Typeface tf = null;
        if (fontFamily != null) {
            tf = Typeface.create(fontFamily, textStyleIndex);
            if (tf != null) {
                textView.setTypeface(tf);
                return;
            }
        }
        switch (typefaceIndex) {
            case SANS:
                tf = Typeface.SANS_SERIF;
                break;

            case SERIF:
                tf = Typeface.SERIF;
                break;

            case MONOSPACE:
                tf = Typeface.MONOSPACE;
                break;
        }

        textView.setTypeface(tf, textStyleIndex);
    }
}
