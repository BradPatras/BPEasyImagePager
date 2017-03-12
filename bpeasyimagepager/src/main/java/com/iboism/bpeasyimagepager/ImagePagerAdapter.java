package com.iboism.bpeasyimagepager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Calm on 3/8/2017.
 */
class ImagePagerAdapter extends PagerAdapter {
    ArrayList<ImageView> images;

    public ImagePagerAdapter(){
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