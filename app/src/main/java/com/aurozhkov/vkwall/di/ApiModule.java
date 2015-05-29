package com.aurozhkov.vkwall.di;

import android.content.Context;

import com.aurozhkov.vkwall.BuildConfig;
import com.aurozhkov.vkwall.network.Profile;
import com.aurozhkov.vkwall.network.WallRecord;
import com.aurozhkov.vkwall.network.WallResponse;
import com.aurozhkov.vkwall.services.ApiHelper;
import com.aurozhkov.vkwall.services.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module
public class ApiModule {

    @Provides
    @Singleton
    ApiService provideApiService(Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(Endpoints.newFixedEndpoint("https://api.vk.com/method/"))
                .setConverter(new GsonConverter(gson))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build().create(ApiService.class);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(WallResponse.Response.class, new JsonDeserializer<WallResponse.Response>() {
                    @Override
                    public WallResponse.Response deserialize(JsonElement json, Type typeOfT,
                                                             JsonDeserializationContext context) throws JsonParseException {
                        WallResponse.Response response = new WallResponse.Response();
                        JsonObject object = json.getAsJsonObject();
                        JsonArray jsonArray = object.getAsJsonArray("wall");
                        response.count = jsonArray.get(0).getAsInt();
                        response.wall = new ArrayList<>(jsonArray.size() - 1);
                        Gson gson = new Gson();
                        for (int i = 1; i < jsonArray.size(); i++) {
                            response.wall.add(gson.fromJson(jsonArray.get(i), WallRecord.class));
                        }
                        response.profiles = gson.fromJson(object.get("profiles"), new TypeToken<List<Profile>>() {
                        }.getType());
                        return response;
                    }
                })
                .create();
    }

    @Provides
    ApiHelper provideApiHelper() {
        return new ApiHelper();
    }

    @Provides
    @Singleton
    Picasso providePicasso(Context context) {
        return Picasso.with(context);
    }
}
