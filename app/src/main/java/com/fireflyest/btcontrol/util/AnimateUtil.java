package com.fireflyest.btcontrol.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class AnimateUtil {

    private AnimateUtil(){
    }

    public static void show(final View view, final long duration, final long delay, final float alpha, boolean scale){
        float s = scale ? 1.1f :1f;
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setStartDelay(delay)
                .alpha(alpha)
                .scaleX(s)
                .scaleY(s)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(duration/2)
                                .setListener(null);
                    }
                });
    }

    public static void show(final View view, final long duration, final long delay, final float alpha){
        show(view, duration, delay, 1,  false);
    }

    public static void show(final View view, final long duration, final long delay){
        show(view, duration, delay, 1);
    }

    public static void hide(final View view, final long duration, final long delay){
        view.animate()
                .setStartDelay(delay)
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void click(final View view, final long duration){
        view.animate()
                .setDuration(duration)
                .scaleX(0.6f)
                .scaleY(0.6f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.animate()
                                .setDuration(duration)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setListener(null);
                    }
                });
    }

    public static void alpha(final View view, final long duration, final float alpha){
        view.animate()
                .alpha(alpha)
                .setDuration(duration)
                .setListener(null);
    }

    public static void expand(final View view, final long duration){
        view.animate()
                .setDuration(duration)
                .scaleX(2f)
                .scaleY(2f)
                .setListener(null);
    }

    public static void lessen(final View view, final long duration){
        view.animate()
                .setDuration(duration)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setListener(null);
    }

}
