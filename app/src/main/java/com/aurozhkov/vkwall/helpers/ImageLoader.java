package com.aurozhkov.vkwall.helpers;

import android.widget.ImageView;

import com.aurozhkov.vkwall.model.VkProfile;
import com.aurozhkov.vkwall.model.VkWallRecord;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ImageLoader {

    private final Picasso picasso;

    @Inject
    public ImageLoader(Picasso picasso) {
        this.picasso = picasso;
    }

    public void loadAvatar(ImageView imageView, VkProfile profile) {
        if (profile != null && profile.avatar() != null) {
            picasso.load(profile.avatar().toString()).into(imageView);
        }
    }

    public void loadPhoto(ImageView imageView, VkWallRecord record) {
        if (record.photo() != null) {
            picasso.load(record.photo().photo().toString()).into(imageView);
        }
    }

    public void cancel(ImageView imageView) {
        picasso.cancelRequest(imageView);
    }
}