package com.aurozhkov.vkwall.ui;

import android.content.Intent;
import android.os.Bundle;

import com.aurozhkov.vkwall.base.VkActivity;
import com.aurozhkov.vkwall.data.UserInfoStore;
import com.aurozhkov.vkwall.events.LoginSuccessEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class LoginActivity extends VkActivity {

    @Inject UserInfoStore userInfoStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        daggerComponent().inject(this);

        if (userInfoStore.hasInfo()) {
            showMainScreen();
        } else {
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content,
                                new LoginFragment(),
                                "login")
                        .commit();
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration") // Used by Otto
    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event) {
        showMainScreen();
    }

    private void showMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
