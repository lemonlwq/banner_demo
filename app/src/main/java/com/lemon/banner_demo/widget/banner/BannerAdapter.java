package com.lemon.banner_demo.widget.banner;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by MLinWQ on 2018/8/7 0007.
 */

public class BannerAdapter extends PagerAdapter {
    private List<ImageView> mImageViewList;
    ViewPagerOnItemClickListener mViewPagerOnItemClickListener;

    public BannerAdapter(List<ImageView> mImageViewList) {
        this.mImageViewList = mImageViewList;
    }

    public void setViewPagerOnItemClickListener(ViewPagerOnItemClickListener mViewPagerOnItemClickListener) {
        this.mViewPagerOnItemClickListener = mViewPagerOnItemClickListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        position %= mImageViewList.size();

        ImageView v = mImageViewList.get(position);

        //如果在用Glide时设置了ScaleType，则再设置无效
        //v.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ViewParent vp = v.getParent();
        if (vp != null) {
            ViewGroup vg = (ViewGroup) vp;
            vg.removeView(v);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPagerOnItemClickListener != null) {
                    mViewPagerOnItemClickListener.onItemClick();
                }
            }
        });

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    interface ViewPagerOnItemClickListener {
        void onItemClick();
    }
}
