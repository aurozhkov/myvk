package com.aurozhkov.vkwall.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.net.URL;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class VkProfile implements Parcelable {

    @AutoParcel.Builder
    public interface Builder {

        Builder id(long id);

        Builder firstName(String firstName);

        Builder lastName(String lastName);

        Builder avatar(URL avatar);

        VkProfile build();
    }

    public static Builder builder() {
        return new AutoParcel_VkProfile.Builder()
                .id(0)
                .firstName("")
                .lastName("");
    }

    public abstract long id();

    public abstract String firstName();

    public abstract String lastName();

    @Nullable
    public abstract URL avatar();
}
