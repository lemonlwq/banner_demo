package com.lemon.banner_demo.module.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lemon.banner_demo.R;
import com.lemon.banner_demo.widget.banner.BannerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    private Unbinder bind;

    @BindView(R.id.main_banner)
    BannerView mBannerView;

    private List<Integer> imageRes = new ArrayList<>(Arrays.asList(R.drawable.banner_01, R.drawable.banner_02, R.drawable.banner_03,
                                                                   R.drawable.banner_04, R.drawable.banner_05, R.drawable.banner_06));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mBannerView.init(imageRes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
