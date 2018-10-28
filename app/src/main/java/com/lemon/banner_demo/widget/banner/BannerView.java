package com.lemon.banner_demo.widget.banner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lemon.banner_demo.GlideApp;
import com.lemon.banner_demo.R;
import com.lemon.banner_demo.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by MLinWQ on 2018/7/3 0003.
 */

public class BannerView extends RelativeLayout implements BannerAdapter.ViewPagerOnItemClickListener {

    @BindView(R.id.layout_vp_banner)
    ViewPager viewPager;
    @BindView(R.id.layout_banner_indicator)
    LinearLayout indicator;

    private List<Integer> resList;
    private List<ImageView> imageViewList;

    private int selectRes = R.drawable.shape_dot_select;
    private int unSelectRes = R.drawable.shape_dot_default;

    private Disposable disposable;
    //轮播时间，默认5秒
    private long delayTime = 5;
    int currentPos;

    private boolean isStopScroll;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_banner, this, true);
        ButterKnife.bind(this);
        resList = new ArrayList<>();
        imageViewList = new ArrayList<>();
    }


    /**
     * @param list 图片资源
     */
    public void init(List<Integer> list) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        if (list == null) {
            this.setVisibility(GONE);
            return;
        }
        resList.addAll(list);

        //判断是否清空 指示器点
        if (indicator.getChildCount() != 0) {
            indicator.removeAllViewsInLayout();
        }

        //指示器圆点数量
        final int dotSize = resList.size();
        //向指示器添加圆点
        for (int i = 0; i < dotSize; i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(unSelectRes);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DisplayUtil.dp2px(getContext(), 5),
                    DisplayUtil.dp2px(getContext(), 5));
            params.leftMargin = 10;
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            indicator.addView(dot);
        }

        indicator.getChildAt(0).setBackgroundResource(selectRes);

        for (int i = 0; i < resList.size(); i++) {
            final ImageView imageview = new ImageView(getContext());
            SimpleTarget target = new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@android.support.annotation.NonNull Drawable resource, @Nullable Transition transition) {
                    imageview.setImageDrawable(resource);
                    float scale = (float)resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                    int vHeight = (int)(DisplayUtil.getScreenWidth(getContext()) * scale);
                    Log.i("good", "onResourceReady: " + scale + "---" + DisplayUtil.getScreenWidth(getContext()) + "---" + vHeight);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, vHeight);
                    /*layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = 600;*/
                    viewPager.setLayoutParams(layoutParams);

                }
            };

            GlideApp.with(getContext())
                    .load(resList.get(i))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.banner_default_image)
                    .dontAnimate()
                    .into(target);
            imageViewList.add(imageview);
        }

        //设置轮播监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position = position % dotSize;
                currentPos = position;

                for (int i = 0; i < dotSize; i++) {
                    indicator.getChildAt(i).setBackgroundResource(unSelectRes);
                }
                indicator.getChildAt(position).setBackgroundResource(selectRes);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        startScroll();
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        stopScroll();
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        break;
                }
            }
        });
        BannerAdapter bannerAdapter = new BannerAdapter(imageViewList);
        viewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        bannerAdapter.setViewPagerOnItemClickListener(this);

        startScroll();
    }

    //开始轮播
    private void startScroll() {
        isStopScroll = false;
        Observable.timer(delayTime, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        if (isStopScroll) {
                            return;
                        }
                        isStopScroll = true;
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //停止轮播
    private void stopScroll() {
        isStopScroll = true;
    }

    @Override
    public void onItemClick() {
        Toast.makeText(getContext(), String.valueOf(currentPos + 1), Toast.LENGTH_SHORT).show();
    }
}
