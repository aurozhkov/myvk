package com.aurozhkov.vkwall.ui;

import android.content.Intent;
import android.os.Bundle;

import com.aurozhkov.vkwall.base.VkActivity;
import com.aurozhkov.vkwall.data.UserInfoStore;
import com.aurozhkov.vkwall.events.LogoutEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;


public class MainActivity extends VkActivity {

    @Inject UserInfoStore userInfoStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daggerComponent().inject(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content,
                            new UserWallFragmentBuilder(userInfoStore.getUserId()).build(),
                            "login")
                    .commit();
        }
    }

    @SuppressWarnings("UnusedDeclaration") // Used by Otto
    @Subscribe
    public void onLogout(LogoutEvent event) {
        userInfoStore.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
