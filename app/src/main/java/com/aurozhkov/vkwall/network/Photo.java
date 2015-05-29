package com.aurozhkov.vkwall.network;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Photo {

    public long pid;
    public int width;
    public int height;
    @SerializedName("src_big")
    public URL srcBig;
}
