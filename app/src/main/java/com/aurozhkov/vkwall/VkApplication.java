package com.aurozhkov.vkwall;

import android.app.Application;
import android.content.Context;

import com.aurozhkov.vkwall.di.VkComponent;

public class VkApplication extends Application {

    private VkComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        buildComponent();
    }

    public static VkComponent component(Context context) {
        return VkApplication.get(context).component();
    }

    private static VkApplication get(Context context) {
        return (VkApplication) context.getApplicationContext();
    }

    private void buildComponent() {
        component = VkComponent.Initializer.init(this);
    }

    private VkComponent component() {
        return component;
    }
}
