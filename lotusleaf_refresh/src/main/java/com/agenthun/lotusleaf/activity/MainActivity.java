package com.agenthun.lotusleaf.activity;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.agenthun.lotusleaf.R;
import com.agenthun.lotusleaf.UiUtils;
import com.agenthun.lotusleaf.view.LotusLeafSunHeaderView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.iface.ImageLoadHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class MainActivity extends AppCompatActivity {

    private String mUrl = "http://www.hdwallpapers.in/download/poltergeist_2015_movie-1280x720.jpg";
    private long mStartLoadingTime = -1;
    private boolean mImageHasLoaded = false;

    @InjectView(R.id.material_style_image_view)
    CubeImageView imageView;
    @InjectView(R.id.material_style_ptr_frame)
    PtrFrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final ImageLoader imageLoader = ImageLoaderFactory.create(this);

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
                if (mImageHasLoaded) {
                    long delay = 1500;
                    frameLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            frameLayout.refreshComplete();
                        }
                    }, delay);
                } else {
                    mStartLoadingTime = System.currentTimeMillis();
                    imageView.loadImage(imageLoader, mUrl);
                }
            }
        });

        ImageLoadHandler imageLoadHandler = new ImageLoadHandler() {
            @Override
            public void onLoading(ImageTask imageTask, CubeImageView cubeImageView) {

            }

            @Override
            public void onLoadFinish(ImageTask imageTask, final CubeImageView cubeImageView, final BitmapDrawable bitmapDrawable) {
                mImageHasLoaded = true;
                long delay = 1500;
                frameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cubeImageView != null && bitmapDrawable != null) {
                            TransitionDrawable wl = new TransitionDrawable(new Drawable[]{new ColorDrawable(Color.WHITE), (Drawable) bitmapDrawable});
                            imageView.setImageDrawable(wl);
                            wl.startTransition(200);
                        }
                        frameLayout.refreshComplete();
                    }
                }, delay);
            }

            @Override
            public void onLoadError(ImageTask imageTask, CubeImageView cubeImageView, int i) {

            }
        };
        imageLoader.setImageLoadHandler(imageLoadHandler);
    }
}
