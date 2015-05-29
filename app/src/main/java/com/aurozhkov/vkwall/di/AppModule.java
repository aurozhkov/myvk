package com.aurozhkov.vkwall.di;

import android.content.Context;

import com.aurozhkov.vkwall.helpers.PaginationHelper;
import com.aurozhkov.vkwall.data.PreferencesUserInfoStore;
import com.aurozhkov.vkwall.data.UserInfoStore;
import com.squareup.otto.Bus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    UserInfoStore provideUserInfoStore(Context context) {
        return PreferencesUserInfoStore.get(context);
    }

    @Provides
    @Named("pagination")
    int providePaginationPageSize() {
        return 30;
    }

    @Provides
    PaginationHelper providePaginationHelper() {
        return new PaginationHelper();
    }
}
