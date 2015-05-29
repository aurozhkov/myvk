package com.aurozhkov.vkwall.network;

import java.util.List;

public class WallResponse {

    public Response response;

    public static class Response {
        public int count;
        public List<WallRecord> wall;
        public List<Profile> profiles;
    }
}
