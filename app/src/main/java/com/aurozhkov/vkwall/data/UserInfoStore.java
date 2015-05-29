package com.aurozhkov.vkwall.data;

public interface UserInfoStore {

    void saveUserId(long id);

    long getUserId();

    void saveAccessToken(String accessToken);

    String getAccessToken();

    void clear();

    boolean hasInfo();
}
