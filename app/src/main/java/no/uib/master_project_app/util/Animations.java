package no.uib.master_project_app.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;

/**
 * Created by moled on 03.10.2017.
 */

public class Animations {
    /**
     * Gets the system-defined animation times. Recommended to use, as these are adjusted accordingly
     * if systemwide animations are changed. I.e. in developer options.
     *
     */
    public int getShortAnimTime(Context context) {
        return context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }
    public int getMediumAnimTime(Context context) {
        return context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
    }
    public int getLongAnimTime(Context context) {
        return context.getResources().getInteger(android.R.integer.config_longAnimTime);
    }
/**********************************************************************************************/

    /**
     * Fades in a view. Sets the view to 0 alpha ad fades gradually in. Remember to set the alpha
     * to 0 in XML, or else the view will "blink" before fading
     * @param view view to fade
     * @param startDelay how long it should take from init the method to teh animation to begin
     * @param duration the duration of the anim
     */
    public void fadeInView(View view, int startDelay, int duration) {
        view.setAlpha(0);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha",  0, 1f);
        fadeIn.setStartDelay(startDelay);
        fadeIn.setDuration(duration);
        fadeIn.start();
    }
    /**
     * Fades out a view. Starts from 1 alpha and gradually fades down to 0 alpha. Remember to set
     * view's alpha to zero after fade, or else it will pop back up again, as this is opnly the animation
     * and doesnt set the alpha afterwards.
     * @param view view to fade
     * @param startDelay how long it should take from init the method to teh animation to begin
     * @param duration the duration of the anim
     */
    public void fadeOutView(View view, int startDelay, int duration) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha",  1f, 0);
        fadeIn.setStartDelay(startDelay);
        fadeIn.setDuration(duration);
        fadeIn.start();
    }

    /**
     * Moves a view along the Y axis (vertically). Moves relative to the view's position, meaning that 0 is the view's start position.
     *
     * @param VIEW the view to move
     * @param startDelay how long it should take from init the method to teh animation to begin
     * @param duration the duration of the anim
     * @param offset Relative pixel (not dpi) distance to where ti should move. Positive numbers is downwards, negative is upwards.
     * @param goneOnAnimEnd whether the view's visibility shold be set to gone or not. TRUE = gone, False = still exists
     *                      Note: Visibility = GONE affects the View's space around it, meaning that setting it to gone, it won't take up space anymore.
     *                      It's also still instantiated, meaning that you can set the property to "VISIBLIE" and it'll pop up back again.
     */
    public void moveViewToTranslationY(final View VIEW, int startDelay, int duration, int offset, final boolean goneOnAnimEnd) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(VIEW, "TranslationY", offset);
        fadeIn.setStartDelay(startDelay);
        fadeIn.setDuration(duration);
        fadeIn.start();
        fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(!goneOnAnimEnd){
                    VIEW.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(goneOnAnimEnd){
                    VIEW.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * Fades a view's background color from one specified color to another specified color
     * @param view the voew to change its color
     * @param duration duration of the fading
     * @param fromColor the start color, usually this would be the current color of the view.
     *                  I.e. if the view's bg color is white, the color here should be white.
     * @param toColor the color the view should change in to.
     */
    public void fadeBackgroundFromColorToColor(View view, int duration, int fromColor, int toColor){
        ColorDrawable[] color2 = {new ColorDrawable(fromColor), new ColorDrawable(toColor)};
        TransitionDrawable trans = new TransitionDrawable(color2);
        //This will work also on old devices. The latest API says you have to use setBackground instead.
        view.setBackground(trans);
        trans.startTransition(duration);

    }

}
