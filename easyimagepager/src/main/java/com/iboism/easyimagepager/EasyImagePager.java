package com.iboism.easyimagepager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.animation.ValueAnimator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
/**
 * Created by Brad on 3/8/2017.
 */

public class EasyImagePager extends RelativeLayout {
    private static final int PAGER_ANIMATION_DURATION = 800;
    private static final int PAGER_ANIMATION_DELAY = 5000;
    private static final int INDICATOR_SELECTED_RES = R.drawable.image_flipper_indicator_circle_selected;
    private static final int INDICATOR_DESELECTED_RES = R.drawable.image_flipper_indicator_circle_deselected;
    private static final boolean DEFAULT_AUTO_SCROLL = true;
    //objects
    private Context mContext;
    private ArrayList<View> indicators;
    private ViewPager pager;
    private PagerTimer imageUpdateTimer;
    //primitives
    private boolean swipeDirectionForward;
    private boolean shouldAutoScroll;
    private int animationDelay;
    private int animationDuration;
    private int indicatorSelected;
    private int indicatorDeselected;
    private static int what = 5;
    /**
     * Create a new EasyImagePager from a list of image urls
     * @param context
     * @param imageUrls
     * @param shouldAutoScroll whether or not the pager will automatically scroll
     */
    public EasyImagePager(Context context, ArrayList<String> imageUrls, boolean shouldAutoScroll){
        super(context);
        sharedConstructor(context, shouldAutoScroll);
        setImages(imageUrls);

    }

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

    /**
     *
     * @return delay between page animations in milliseconds
     */
    public int getAnimationDelay() {
        return animationDelay;
    }

    /**
     *
     * @return the time it takes to animate between pages in milliseconds
     */
    public int getAnimationDuration() {
        return animationDuration;
    }

    /**
     *
     * @param delay set the time between page animations in milliseconds
     */
    public void setAnimationDelay(int delay){
        animationDelay = delay;
    }

    /**
     *
     * @param duration set the time it takes to animate to a new page in milliseconds
     */
    public void setAnimationDuration(int duration){
        animationDuration = duration;
    }

    /**
     *
     * @param indicatorDeselected set the resource for the deselected indicator drawable
     */
    public void setIndicatorDeselectedResource(int indicatorDeselected) {
        this.indicatorDeselected = indicatorDeselected;
    }

    /**
     *
     * @param indicatorSelected set the resource for the selected indicator drawable
     */
    public void setIndicatorSelectedResource(int indicatorSelected) {
        this.indicatorSelected = indicatorSelected;
    }

    /**
     *
     * @return the resource for the selected indicator drawable
     */
    public int getIndicatorSelectedResource() {
        return indicatorSelected;
    }

    /**
     *
     * @return the resource for the deselected indicator drawable
     */
    public int getIndicatorDeselectedResource() {
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
                    if (pager != null && pager.getChildCount() > 0) {
                        //if the pager has reached the end, reverse the direction of the swipe animation
                        if (pager.getCurrentItem() == indicators.size() - 1) {
                            swipeDirectionForward = false;
                        } else if (pager.getCurrentItem() == 0) {
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
    public void setImages(@NonNull ArrayList<String> imageUrls){
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.easy_image_pager,this);
        pager = (ViewPager) root.findViewById(R.id.image_pager);
        LinearLayout indicatorContainer = (LinearLayout) root.findViewById(R.id.indicator_container);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        indicators.clear();
        indicatorContainer.removeAllViewsInLayout();

        //Add an imageview and a page indicator for each image url
        for (String url : imageUrls) {
            //create an imageview, load it with a url, and add it as a pager child
            ImageView photo = new ImageView(mContext);
            ViewGroup.LayoutParams photoLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photo.setLayoutParams(photoLayout);
            photo.setAdjustViewBounds(true);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(mContext).load(url).into(photo);
            adapter.addView(photo);

            //create an indicator circle for each view in the pager
            View indicator = new View(mContext);
            LinearLayout.LayoutParams indicatorLayout = new LinearLayout.LayoutParams(pixelConvert(6), pixelConvert(6));
            indicatorLayout.setMargins(pixelConvert(2), 0, pixelConvert(2), 0);
            indicator.setLayoutParams(indicatorLayout);
            indicator.setBackgroundResource(INDICATOR_DESELECTED_RES);
            indicators.add(indicator);
            indicatorContainer.addView(indicator, indicatorLayout);
        }

        //Add a listener to the pager so that we can update the indicator when the page changes
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        });
        pager.setAdapter(adapter);
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
        if (pager.getChildCount()==0) return;

        ValueAnimator animator = ValueAnimator.ofInt(0, pager.getWidth());
        animator.addListener(new PagerAnimator(this.pager));
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                if(pager.isFakeDragging()) {
                    pager.fakeDragBy(dragOffset * (forward ? -1 : 1));
                }
            }
        });
        animator.setDuration(animationDuration);
        pager.beginFakeDrag();
        animator.start();
    }
}
