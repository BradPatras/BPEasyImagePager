package com.iboism.easyimagepager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by Calm on 3/8/2017.
 */

public class EasyImagePager extends RelativeLayout {
    private final int PAGER_ANIMATION_DURATION = 800;
    private final int PAGER_ANIMATION_DELAY = 5000;
    private final int INDICATOR_SELECTED_RES = R.drawable.image_flipper_indicator_circle_selected;
    private final int INDICATOR_DESELECTED_RES = R.drawable.image_flipper_indicator_circle_deselected;
    private final boolean DEFAULT_AUTO_SCROLL = true;
    //objects
    private Context mContext;
    private ArrayList<View> indicators;
    private ViewPager flipper;
    private PagerTimer imageUpdateTimer;
    //primitives
    private boolean swipeDirectionForward;
    private boolean shouldAutoScroll;
    private int animationDelay;
    private int animationDuration;
    private int indicatorSelected;
    private int indicatorDeselected;

    public EasyImagePager(Context context) {
        super(context);
        sharedConstructor(context, DEFAULT_AUTO_SCROLL);
    }

    public EasyImagePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context, DEFAULT_AUTO_SCROLL);
    }

    public EasyImagePager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor(context, DEFAULT_AUTO_SCROLL);
    }

    private void sharedConstructor(Context context, Boolean autoScroll){
        mContext = context;
        indicators = new ArrayList<>();
        swipeDirectionForward = true;
        animationDelay = PAGER_ANIMATION_DELAY;
        animationDuration = PAGER_ANIMATION_DURATION;
        indicatorSelected = INDICATOR_SELECTED_RES;
        indicatorDeselected = INDICATOR_DESELECTED_RES;
        this.shouldAutoScroll = autoScroll;
        //Create the timer that will change the page after a delay
        setAutoScroll(autoScroll);
    }

    //endregion

    //region Getters and Setters
    public int getAnimationDelay() {
        return animationDelay;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDelay(int delay){
        animationDelay = delay;
    }

    public void setAnimationDuration(int duration){
        animationDuration = duration;
    }

    public void setIndicatorDeselected(int indicatorDeselected) {
        this.indicatorDeselected = indicatorDeselected;
    }

    public void setIndicatorSelected(int indicatorSelected) {
        this.indicatorSelected = indicatorSelected;
    }

    public int getIndicatorSelected() {
        return indicatorSelected;
    }

    public int getIndicatorDeselected() {
        return indicatorDeselected;
    }
    //endregion


    @Override
    protected void onDetachedFromWindow() {
        if(imageUpdateTimer != null) imageUpdateTimer.stopTimer();
        super.onDetachedFromWindow();
    }

    private void startAutoScroll(){
        if (imageUpdateTimer == null) {
            imageUpdateTimer = new PagerTimer(animationDelay, new PagerTimer.TimerListener() {
                @Override
                public void onIntervalTick() {
                    if (flipper != null && flipper.getChildCount() > 0) {
                        //if the pager has reached the end, reverse the direction of the swipe animation
                        if (flipper.getCurrentItem() == indicators.size() - 1) {
                            swipeDirectionForward = false;
                        } else if (flipper.getCurrentItem() == 0) {
                            swipeDirectionForward = true;
                        }
                        animatePagerTransition(swipeDirectionForward);
                    }
                }
            });
        } else {
            imageUpdateTimer.startTimer();
        }
    }

    private void cancelAutoScroll(){
        if (imageUpdateTimer != null){
            imageUpdateTimer.stopTimer();
        }
    }

    public void setAutoScroll(boolean shouldAutoScroll){
        this.shouldAutoScroll = shouldAutoScroll;
        if (shouldAutoScroll){
            startAutoScroll();
        } else {
            cancelAutoScroll();
        }
    }

    /**
     * Create the view pager with a page for each of the image urls, with indicators
     * @param imageUrls array of urls that point to images for the pager, each image will have it's own page
     */
    public void setImages(ArrayList<String> imageUrls){
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.easy_image_pager,this);
        flipper = (ViewPager) root.findViewById(R.id.image_pager);
        LinearLayout indicatorContainer = (LinearLayout) root.findViewById(R.id.indicator_container);
        ImageStreamAdapter adapter = new ImageStreamAdapter();
        indicators.clear();

        for(String url: imageUrls){
            //create an imageview, load it with a url, and add it as a flipper child
            ImageView photo = new ImageView(mContext);
            ViewGroup.LayoutParams photoLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photo.setLayoutParams(photoLayout);
            photo.setAdjustViewBounds(true);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(mContext).load(url).into(photo);
            adapter.addView(photo);

            //create an indicator circle for each view in the flipper
            View v = new View(mContext);
            LinearLayout.LayoutParams indicatorLayout = new LinearLayout.LayoutParams(pixelConvert(6), pixelConvert(6));
            indicatorLayout.setMargins(pixelConvert(2),0,pixelConvert(2),0);
            v.setLayoutParams(indicatorLayout);
            v.setBackgroundResource(INDICATOR_DESELECTED_RES);
            indicators.add(v);
            indicatorContainer.addView(v, indicatorLayout);

        }

        //Add a listener to the pager so that we can update the indicator when the page changes
        flipper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        });
        flipper.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        updateIndicators(0);

        if (shouldAutoScroll){
            startAutoScroll();
        }
    }

    /**
     * Convert dp pixels to px
     * @param dp display-independent pixels
     * @return raw pixels
     */
    private int pixelConvert(int dp){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Update the page indicator
     * @param displayed index of the currently displayed page
     */
    private void updateIndicators(int displayed){
        int i = 0;
        for (View v:indicators){
            if (displayed == i){
                v.setBackgroundResource(indicatorSelected);
            } else {
                v.setBackgroundResource(indicatorDeselected);
            }
            i++;
        }

    }

    /**
     * There's no way to set custom animations for moving between pages in the default ViewPager, and the default is too fast/jarring.
     * Instead of an animation, simulate a drag on the ViewPager.
     * @param forward the direction of the swipe
     */
    private void animatePagerTransition(final boolean forward) {
        if (flipper.getChildCount()==0) return;

        ValueAnimator animator = ValueAnimator.ofInt(0, flipper.getWidth());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(flipper.isFakeDragging())
                    flipper.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (flipper.isFakeDragging())
                    flipper.endFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                if(flipper.isFakeDragging()) {
                    flipper.fakeDragBy(dragOffset * (forward ? -1 : 1));
                }
            }
        });

        animator.setDuration(animationDuration);
        flipper.beginFakeDrag();
        animator.start();
    }

    /**
     * Adapter class for the pager
     * addView allows loading of images into its collection one by one
     */
    public class ImageStreamAdapter extends PagerAdapter{
        ArrayList<ImageView> images;

        public ImageStreamAdapter(){
            images = new ArrayList<>();
        }

        @Override
        public int getCount ()
        {
            return images.size();
        }

        public void addView(ImageView view){
            images.add(view);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem (ViewGroup container, int position)
        {
            ImageView v = images.get (position);
            container.addView (v);
            return v;
        }


    }



}
