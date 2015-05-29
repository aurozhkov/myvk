package com.aurozhkov.vkwall.di;

import com.aurozhkov.vkwall.ui.LoadableCardLayout;
import com.aurozhkov.vkwall.ui.LoginActivity;
import com.aurozhkov.vkwall.ui.LoginFragment;
import com.aurozhkov.vkwall.ui.MainActivity;
import com.aurozhkov.vkwall.ui.UserWallFragment;
import com.aurozhkov.vkwall.VkApplication;
import com.aurozhkov.vkwall.ui.WallRecyclerAdapter;
import com.aurozhkov.vkwall.base.VkActivity;
import com.aurozhkov.vkwall.base.VkFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface VkComponent {

    public static final class Initializer {

        public static VkComponent init(VkApplication app) {
            return DaggerVkComponent.builder()
                    .appModule(new AppModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.

    }

    void inject(VkActivity activity);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(VkFragment fragment);

    void inject(LoginFragment fragment);

    void inject(UserWallFragment userWallFragment);

    void inject(LoadableCardLayout loadableCardLayout);

    void inject(WallRecyclerAdapter adapter);
}
