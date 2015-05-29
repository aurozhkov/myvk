package com.aurozhkov.vkwall.services;

import com.aurozhkov.vkwall.model.VkPhoto;
import com.aurozhkov.vkwall.model.VkProfile;
import com.aurozhkov.vkwall.model.VkWallRecord;
import com.aurozhkov.vkwall.network.Attachment;
import com.aurozhkov.vkwall.network.Photo;
import com.aurozhkov.vkwall.network.Profile;
import com.aurozhkov.vkwall.network.WallRecord;
import com.aurozhkov.vkwall.network.WallResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiHelper {

    public List<VkWallRecord> convertWallResponse(WallResponse wallResponse) {
        List<VkWallRecord> vkWallRecords = new ArrayList<>();
        for (WallRecord wallRecord : wallResponse.response.wall) {
            VkWallRecord vkWallRecord
                    = VkWallRecord.builder()
                    .id(wallRecord.id)
                    .date(new Date(wallRecord.date * 1000))
                    .text(wallRecord.text)
                    .author(getAuthor(wallResponse.response.profiles, wallRecord.fromId))
                    .photo(getPhoto(wallRecord.attachments))
                    .commentsCount(wallRecord.comments.count)
                    .likesCount(wallRecord.likes.count)
                    .repostsCount(wallRecord.reposts.count)
                    .build();
            vkWallRecords.add(vkWallRecord);
        }
        return vkWallRecords;
    }

    private VkProfile getAuthor(List<Profile> profiles, long id) {
        if (profiles != null) {
            for (Profile profile : profiles) {
                if (profile.uid == id) {
                    return convertProfile(profile);
                }
            }
        }
        return null;
    }

    private VkProfile convertProfile(Profile profile) {
        return VkProfile.builder()
                .id(profile.uid)
                .firstName(profile.firstName)
                .lastName(profile.lastName)
                .avatar(profile.photoMediumRec)
                .build();
    }

    private VkPhoto getPhoto(List<Attachment> attachments) {
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if ("photo".equals(attachment.type)) {
                    return convertPhoto(attachment.photo);
                }
            }
        }
        return null;
    }

    private VkPhoto convertPhoto(Photo photo) {
        return VkPhoto.builder()
                .id(photo.pid)
                .width(photo.width)
                .height(photo.height)
                .photo(photo.srcBig)
                .build();
    }
}
