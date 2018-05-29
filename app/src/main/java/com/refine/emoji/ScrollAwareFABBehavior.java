package com.refine.emoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //@Override
    //public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout
    //        , FloatingActionButton child
    //        , View directTargetChild
    //        , View target
    //        , int nestedScrollAxes) {
    //    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
    //            super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
    //                    nestedScrollAxes);
    //}

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout
            , @NonNull FloatingActionButton child
            , @NonNull View directTargetChild
            , @NonNull View target
            , int axes
            , int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    //@Override
    //public void onNestedScroll(CoordinatorLayout coordinatorLayout
    //        , FloatingActionButton child
    //        , View target
    //        , int dxConsumed
    //        , int dyConsumed
    //        , int dxUnconsumed
    //        , int dyUnconsumed) {
    //    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
    //            dyUnconsumed);
    //
    //    if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
    //        animateOut(child);
    //    } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
    //        animateIn(child);
    //    }
    //}

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            animateOut(child);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            animateIn(child);
        }
    }

    private void animateOut(final FloatingActionButton button) {
        //if (Build.VERSION.SDK_INT >= 14) {
        //    ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
        //            .setInterpolator(INTERPOLATOR).withLayer()
        //            .setListener(new ViewPropertyAnimatorListener() {
        //                @Override
        //                public void onAnimationStart(View view) {
        //                    ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
        //                }
        //
        //                @Override
        //                public void onAnimationCancel(View view) {
        //                    ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
        //                }
        //
        //                @Override
        //                public void onAnimationEnd(View view) {
        //                    ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
        //                    view.setVisibility(View.GONE);
        //                }
        //            }).start();
        //} else {
        //    Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_out);
        //    anim.setInterpolator(INTERPOLATOR);
        //    anim.setDuration(200L);
        //    anim.setAnimationListener(new Animation.AnimationListener() {
        //        @Override
        //        public void onAnimationStart(Animation animation) {
        //            ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
        //        }
        //
        //        @Override
        //        public void onAnimationEnd(Animation animation) {
        //            ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
        //            button.setVisibility(View.GONE);
        //        }
        //
        //        @Override
        //        public void onAnimationRepeat(final Animation animation) {
        //        }
        //    });
        //    button.startAnimation(anim);
        //}
        ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
                .setInterpolator(INTERPOLATOR).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                        view.setVisibility(View.INVISIBLE);
                    }
                }).start();
    }

    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        //if (Build.VERSION.SDK_INT >= 14) {
        //    ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
        //            .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
        //            .start();
        //} else {
        //    Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
        //    anim.setDuration(200L);
        //    anim.setInterpolator(INTERPOLATOR);
        //    button.startAnimation(anim);
        //}
        ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                .start();
    }
}