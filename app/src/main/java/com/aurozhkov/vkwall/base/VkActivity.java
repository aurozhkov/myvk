package com.aurozhkov.vkwall.base;

import android.app.Activity;
import android.os.Bundle;

import com.aurozhkov.vkwall.VkApplication;
import com.aurozhkov.vkwall.di.VkComponent;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public abstract class VkActivity extends Activity {

    @Inject protected Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daggerComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    protected VkComponent daggerComponent() {
        return VkApplication.component(this);
    }
}
