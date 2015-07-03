package com.agenthun.lotusleaf.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.agenthun.lotusleaf.R;
import com.agenthun.lotusleaf.UiUtils;
import com.agenthun.lotusleaf.view.LotusLeafSunHeaderView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class MainActivity extends AppCompatActivity {

    private List<String> mUrls;

    @InjectView(R.id.material_style_ptr_frame)
    PtrFrameLayout frameLayout;
    @InjectView(R.id.content_linearlayout)
    LinearLayout linearLayout;
    @InjectView(R.id.image_view)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mUrls = Arrays.asList(getResources().getStringArray(R.array.images_url));

        LotusLeafSunHeaderView headerView = new LotusLeafSunHeaderView(this);
        headerView.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        headerView.setPadding(0, UiUtils.dipToPx(this, 15), 0, UiUtils.dipToPx(this, 10));
        headerView.setUp(frameLayout);

        frameLayout.setLoadingMinTime(1000);
        frameLayout.setDurationToCloseHeader(1500);
        frameLayout.setHeaderView(headerView);
        frameLayout.addPtrUIHandler(headerView);
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                frameLayout.autoRefresh(true);
            }
        }, 100);

        frameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
                return true;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                updateImage();
            }
        });
    }

    private void updateImage() {
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Picasso.with(MainActivity.this)
                        .load(mUrls.get((int) (Math.random() * mUrls.size())))
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                linearLayout.setScaleX(0);
                                linearLayout.setScaleY(0);
                                linearLayout.animate()
                                        .scaleX(1)
                                        .scaleY(1)
                                        .setDuration(200)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setStartDelay(100)
                                        .start();
                            }

                            @Override
                            public void onError() {

                            }
                        });
                frameLayout.refreshComplete();
            }
        }, 1500);
    }
}
