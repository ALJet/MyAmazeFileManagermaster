package indi.aljet.myamazefilemanager_master.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Property;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import java.util.ArrayList;


/**
 * Created by PC-LJL on 2018/1/12.
 */

public class AnimUtils {

    private static Interpolator fastOutSlowIn;
    private static Interpolator fastOutLinearIn;
    private static Interpolator linearOutSlowIn;

    public static Interpolator getFastOutSlowInInterpolator
            (Context context){
        if(fastOutSlowIn == null){
            fastOutSlowIn = AnimationUtils.loadInterpolator
                    (context, android.R.interpolator.
                    fast_out_slow_in);
        }
        return fastOutSlowIn;
    }


    public static Interpolator getFastOutLinearInInterpolator
            (Context context){
                if(fastOutLinearIn == null){
                    fastOutLinearIn = AnimationUtils
                            .loadInterpolator(context,
                                    android.R.interpolator
                            .fast_out_linear_in);
                }
                return fastOutLinearIn;
    }


    public static Interpolator getLinearOutSlowInInterpolator
            (Context context){
                if(linearOutSlowIn == null){
                    linearOutSlowIn = AnimationUtils
                            .loadInterpolator(context,
                                    android.R.interpolator
                            .linear_out_slow_in);
                }
                return linearOutSlowIn;
    }


    /**
     * Linear interpolate between a and b with parameter t.
     */
    public static float lerp(float a,float b,float t){
                return a + (b - a) * t;
    }




    public static abstract class FloatProperty<T> extends
            Property<T,Float>{

        public FloatProperty(Class<Float> type, String name) {
            super(type, name);
        }


        public abstract void setValue(T object,float value);

        @Override
        public void set(T object, Float value) {
            setValue(object,value);
        }
    }


    public static abstract class InteProperty<T> extends
            Property<T,Integer>{

        public InteProperty(Class<Integer> type, String name) {
            super(type, name);
        }

        public abstract void setValue(T object,int value);

        @Override
        public void set(T object, Integer value) {
            setValue(object,value);
        }
    }



    public static class NoPauseAnimator extends Animator{


        private final Animator mAnimator;

        private final ArrayMap<AnimatorListener,
                AnimatorListener> mListeners = new
                ArrayMap<>();


        public NoPauseAnimator(Animator mAnimator) {
            this.mAnimator = mAnimator;
        }

        @Override
        public void addListener(AnimatorListener listener) {
            AnimatorListener wrapper = new
                    AnimatorListenerWrapper(this,listener);
            if(mListeners.containsKey(listener)){
                mListeners.put(listener,wrapper);
                mAnimator.addListener(wrapper);
            }
        }

        @Override
        public void cancel() {
            mAnimator.cancel();
        }

        @Override
        public void end() {
            mAnimator.end();
        }

        @Override
        public long getDuration() {
            return mAnimator.getDuration();
        }


        @Override
        public TimeInterpolator getInterpolator() {
            return mAnimator.getInterpolator();
        }


        @Override
        public void setInterpolator(TimeInterpolator timeInterpolator) {
            mAnimator.setInterpolator(timeInterpolator);
        }


        @Override
        public ArrayList<AnimatorListener> getListeners() {
            return new ArrayList<>(mListeners.keySet());
        }


        @Override
        public long getStartDelay() {
            return mAnimator.getStartDelay();
        }


        @Override
        public void setStartDelay(long l) {
            mAnimator.setStartDelay(l);
        }


        @Override
        public boolean isPaused() {
            return mAnimator.isPaused();
        }


        @Override
        public boolean isRunning() {
            return mAnimator.isRunning();
        }


        @Override
        public boolean isStarted() {
            return mAnimator.isStarted();
        }


        @Override
        public void removeAllListeners() {
            mListeners.clear();
            mAnimator.removeAllListeners();
        }


        @Override
        public void removeListener(AnimatorListener listener) {
            AnimatorListener wrapper = mListeners
                    .get(listener);
            if(wrapper != null){
                mListeners.remove(listener);
                mAnimator.removeListener(wrapper);
            }
        }


        @Override
        public Animator setDuration(long l) {
            mAnimator.setDuration(l);
            return this;
        }


        @Override
        public void setTarget(@Nullable Object target) {
            mAnimator.setTarget(target);
        }

        @Override
        public void setupEndValues() {
            mAnimator.setupEndValues();
        }

        @Override
        public void setupStartValues() {
            mAnimator.setupStartValues();
        }


        @Override
        public void start() {
            mAnimator.start();
        }
    }



    private static class AnimatorListenerWrapper implements
            Animator.AnimatorListener{

        private final Animator mAnimator;

        private final Animator.AnimatorListener mListener;

        public AnimatorListenerWrapper(Animator mAnimator, Animator.AnimatorListener mListener) {
            this.mAnimator = mAnimator;
            this.mListener = mListener;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            mListener.onAnimationStart(mAnimator);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mListener.onAnimationEnd(mAnimator);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mListener.onAnimationCancel(animator);
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            mListener.onAnimationRepeat(animator);
        }
    }




}
