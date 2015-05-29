package com.aurozhkov.vkwall.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WallRecord {

    public long id;
    @SerializedName("from_id")
    public long fromId;
    public long date;
    public String text;
    public List<Attachment> attachments;
    public StatItem comments;
    public StatItem likes;
    public StatItem reposts;

    public static class StatItem {
        public int count;
    }
}
