package com.ford.campos.testdrawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * Round 1 refactoring: 2/5/18
 * <p>
 * Todo: figure out a better name for frameCount
 */
public class LoadingScreen {

    private Context context;
    private ImageView imageView;
    private int frameCount;
    private int rotationDuration;       //The time it takes to complete 1 rotation
    private int animResourceID;
    private Animation animation;
    private Bitmap bitmap;


    public LoadingScreen(Context context, ImageView imageView, int drawResourceID,
                         int frameCount, int rotationDuration, int animResourceID) {

        this.context = context;
        this.imageView = imageView;
        this.frameCount = frameCount;
        this.rotationDuration = rotationDuration;
        this.animResourceID = animResourceID;

        bitmap = BitmapFactory.decodeResource(context.getResources(), drawResourceID);

    }

    public void startAnimation() {
        setImageToBeAnimated();
        loadPicIntoAnimation();
        setAnimationDelta();
        imageView.startAnimation(animation);
    }

    /**
     * Note that this isn't a gif, it's a square picture that will be rotated.
     */
    private void setImageToBeAnimated() {
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }

    /**
     * Load the animResourceID into its "container."
     */
    private void loadPicIntoAnimation() {
        animation = AnimationUtils.loadAnimation(context, animResourceID);
    }

    /**
     * Delta meaning rate of change.
     */
    private void setAnimationDelta() {
        animation.setInterpolator(new Interpolator() {
            //int frameCount = 120;

            @Override
            public float getInterpolation(float input) {
                return (float) Math.floor(input * frameCount) / frameCount;
            }
        });
        animation.setDuration(rotationDuration);
    }

    public void stopAnimation() {

        if (imageView == null || animation == null)
            return;

        animation.setDuration(0);
        animation = null;
        imageView.invalidate();
        imageView.setVisibility(View.INVISIBLE);

    }

    public void kill() {

        bitmap.recycle();
        bitmap = null;
        context = null;
        imageView = null;
        animation = null;

    }

}