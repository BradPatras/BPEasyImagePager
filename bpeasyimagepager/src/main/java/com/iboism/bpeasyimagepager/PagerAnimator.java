package com.iboism.bpeasyimagepager;

import android.animation.Animator;
import android.support.v4.view.ViewPager;

/**
 * Created by Calm on 3/8/2017.
 */

class PagerAnimator implements Animator.AnimatorListener {
    private ViewPager pager;
    public PagerAnimator(ViewPager pager){
        this.pager = pager;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(pager.isFakeDragging())
            pager.endFakeDrag();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (pager.isFakeDragging())
            pager.endFakeDrag();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {}
    @Override
    public void onAnimationStart(Animator animation) {}
}
