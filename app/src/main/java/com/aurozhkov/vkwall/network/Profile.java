package com.aurozhkov.vkwall.network;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Profile {

    public long uid;

    @SerializedName("first_name")
    public String firstName;

    @SerializedName("last_name")
    public String lastName;

    @SerializedName("photo_medium_rec")
    public URL photoMediumRec;
}
