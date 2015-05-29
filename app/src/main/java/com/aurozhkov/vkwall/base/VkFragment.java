package com.aurozhkov.vkwall.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.aurozhkov.vkwall.VkApplication;
import com.aurozhkov.vkwall.di.VkComponent;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import icepick.Icepick;

public abstract class VkFragment extends Fragment {

    @Inject protected Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
        daggerComponent().inject(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setClickable(true);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    protected VkComponent daggerComponent() {
        return VkApplication.component(getActivity());
    }
}
