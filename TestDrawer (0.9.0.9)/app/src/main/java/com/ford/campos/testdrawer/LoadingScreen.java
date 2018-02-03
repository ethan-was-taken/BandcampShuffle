package com.ford.campos.testdrawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class LoadingScreen {

    private Context context;
    private ImageView image;
    private int frameCount, rotationDuration, animResourceID;
    private Animation animation;
    private Bitmap bitmap;


    public LoadingScreen(Context context, ImageView imageView, int drawResourceID,
                         int frameCount, int rotationDuration, int animResourceID) {

        this.context = context;
        this.image = imageView;
        this.frameCount = frameCount;
        this.rotationDuration = rotationDuration;                                                   //The time it takes to complete 1 rotation
        this.animResourceID = animResourceID;

        bitmap = BitmapFactory.decodeResource(context.getResources(), drawResourceID);

    }


    public void startAnimation() {

        image.setImageBitmap(bitmap);

        image.setVisibility(View.VISIBLE);

        animation = AnimationUtils.loadAnimation(context, animResourceID);
        animation.setInterpolator(new Interpolator() {
            //int frameCount = 120;

            @Override
            public float getInterpolation(float input) {
                return (float) Math.floor(input * frameCount) / frameCount;
            }
        });
        animation.setDuration(rotationDuration);
        image.startAnimation(animation);

    }

    public void stopAnimation() {

        if(image == null || animation == null)
            return;

        animation.setDuration(0);
        animation = null;
        image.invalidate();
        image.setVisibility(View.INVISIBLE);

    }

    public void kill() {

        bitmap.recycle();
        bitmap = null;
        context = null;
        image = null;
        animation = null;

    }

}