package com.aurozhkov.vkwall.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Date;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class VkWallRecord implements Parcelable {

    @AutoParcel.Builder
    public interface Builder {
        Builder id(long id);

        Builder date(Date date);

        Builder text(String text);

        Builder author(VkProfile author);

        Builder photo(VkPhoto photo);

        Builder likesCount(int likesCount);

        Builder repostsCount(int repostsCount);

        Builder commentsCount(int commentsCount);

        VkWallRecord build();
    }

    public static Builder builder() {
        return new AutoParcel_VkWallRecord.Builder()
                .id(0)
                .date(new Date())
                .text("")
                .likesCount(0)
                .repostsCount(0)
                .commentsCount(0);
    }

    public abstract long id();

    public abstract Date date();

    public abstract String text();

    @Nullable
    public abstract VkProfile author();

    @Nullable
    public abstract VkPhoto photo();

    public abstract int likesCount();

    public abstract int repostsCount();

    public abstract int commentsCount();
}
