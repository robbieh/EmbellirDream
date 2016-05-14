package nundrum.embellirdream;

import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.util.Log;

import java.lang.Runnable;

import nundrum.embellirdream.PolarClockView;

/**
 * This class is a sample implementation of a DreamService. When activated, a
 * TextView will repeatedly, move from the left to the right of screen, at a
 * random y-value.
 * The generated {@link EmbellirDaydreamServiceSettingsActivity} allows
 * the user to change the text which is displayed.
 * <p/>
 * Daydreams are only available on devices running API v17+.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class EmbellirDaydreamService extends DreamService {
    private static final String TAG="EmbellirDream";
    private static final TimeInterpolator sInterpolator = new LinearInterpolator();
    private final Random mRandom = new Random();
    private final Point mPointSize = new Point();
    private PolarClockView PCView;
    private ViewPropertyAnimator mAnimator;
    private final AnimatorListener mAnimListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            // Start animation again
            startTextViewScrollAnimation();
        }
    };


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v(TAG, "onAttachedToWindow()");

        // Exit dream upon user touch?
        setInteractive(false);

        // Hide system UI?
        setFullscreen(true);

        // Keep screen at full brightness?
        setScreenBright(false);

        // Set the content view, just like you would with an Activity.
        setContentView(R.layout.embellir_daydream);

        PCView = (PolarClockView) findViewById(R.id.polar_clock_view);
//		final Handler viewHandler = new Handler();
//		final Runnable updateView = new Runnable () {
//			@Override
//			public void run() {
//				PCView.invalidate();
//				viewHandler.postDelayed(PCView, 1000);
//			};
//		};

    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        Log.v(TAG, "onDreamingStarted()");
        // TODO: Begin animations or other behaviors here.

        //startTextViewScrollAnimation();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        Log.v(TAG, "onDreamingStopped()");
        // TODO: Stop anything that was started in onDreamingStarted()

        //mAnimator.cancel();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v(TAG, "onDetachedFromWindow()");

        // TODO: Dismantle resources
        // (for example, detach from handlers and listeners).
    }

    private String getTextFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(R.string.pref_dream_text_key),
                getString(R.string.pref_dream_text_default));
    }

    private void startTextViewScrollAnimation() {
        // Refresh Size of Window
        getWindowManager().getDefaultDisplay().getSize(mPointSize);

        final int windowWidth = mPointSize.x;
        final int windowHeight = mPointSize.y;
        final float midW = mPointSize.x / 2;
        final float midH = mPointSize.y / 2;


        // Move TextView so it's moved all the way to the left
        //mDreamTextView.setTranslationX(-mDreamTextView.getWidth());

        // Move TextView to random y value
        //final int yRange = windowHeight - mDreamTextView.getHeight();
        //mDreamTextView.setTranslationY(mRandom.nextInt(yRange));

        // Create an Animator and keep a reference to it
        //mAnimator = mDreamTextView.animate().translationX(windowWidth)
        //        .setDuration(3000)
        //        .setStartDelay(500)
        //        .setListener(mAnimListener)
        //        .setInterpolator(sInterpolator);

        // Start the animation
        //mAnimator.start();
    }

}
