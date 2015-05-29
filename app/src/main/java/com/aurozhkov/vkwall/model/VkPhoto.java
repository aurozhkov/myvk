package com.aurozhkov.vkwall.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.net.URL;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class VkPhoto implements Parcelable {

    @AutoParcel.Builder
    public interface Builder {

        Builder id(long id);

        Builder width(int width);

        Builder height(int height);

        Builder photo(URL photo);

        VkPhoto build();
    }

    public static Builder builder() {
        return new AutoParcel_VkPhoto.Builder()
                .id(0)
                .width(0)
                .height(0);
    }

    public abstract long id();

    public abstract int width();

    public abstract int height();

    @Nullable
    public abstract URL photo();
}
