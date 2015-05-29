package com.aurozhkov.vkwall.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aurozhkov.vkwall.R;
import com.aurozhkov.vkwall.VkApplication;
import com.aurozhkov.vkwall.events.RetryNetworkRequestEvent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import icepick.Icepick;
import icepick.Icicle;

public class LoadableCardLayout extends FrameLayout {

    @Inject Bus bus;

    @Icicle int selectedChild;

    public LoadableCardLayout(Context context) {
        this(context, null);
    }

    public LoadableCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadableCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        VkApplication.component(context).inject(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadableCardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (isInEditMode()) {
            return;
        }
        VkApplication.component(context).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new IllegalStateException("Illegal state! LoadableCardLayout can have only one child views.");
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View loading = inflater.inflate(R.layout.layout_loading, this, false);
        LayoutParams loadingLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        loadingLayoutParams.gravity = Gravity.CENTER;
        loading.setLayoutParams(loadingLayoutParams);
        loading.setVisibility(INVISIBLE);
        addView(loading);

        View noContent = inflater.inflate(R.layout.layout_no_content, this, false);
        LayoutParams noContentLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        noContentLayoutParams.gravity = Gravity.CENTER;
        noContent.setLayoutParams(noContentLayoutParams);
        noContent.setVisibility(INVISIBLE);
        addView(noContent);

        View error = inflater.inflate(R.layout.layout_reload, this, false);
        LayoutParams errorLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        errorLayoutParams.gravity = Gravity.CENTER;
        error.setLayoutParams(errorLayoutParams);
        error.setVisibility(INVISIBLE);
        error.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new RetryNetworkRequestEvent());
            }
        });
        addView(error);
    }

    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        showSelectedChild();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    public void showContent() {
        selectedChild = 0;
        showSelectedChild();
    }

    public void showLoading() {
        selectedChild = getChildCount() - 3;
        showSelectedChild();
    }

    public void showNoContent() {
        selectedChild = getChildCount() - 2;
        showSelectedChild();
    }

    public void showError() {
        selectedChild = getChildCount() - 1;
        showSelectedChild();
    }

    private void showSelectedChild() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(selectedChild == i ? VISIBLE : INVISIBLE);
        }
    }
}
