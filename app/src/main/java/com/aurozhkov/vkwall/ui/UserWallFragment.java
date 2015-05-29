package com.aurozhkov.vkwall.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aurozhkov.vkwall.R;
import com.aurozhkov.vkwall.base.VkFragment;
import com.aurozhkov.vkwall.events.LogoutEvent;
import com.aurozhkov.vkwall.events.PaginationRetryClickEvent;
import com.aurozhkov.vkwall.events.RetryNetworkRequestEvent;
import com.aurozhkov.vkwall.helpers.PaginationHelper;
import com.aurozhkov.vkwall.model.VkWallRecord;
import com.aurozhkov.vkwall.network.WallResponse;
import com.aurozhkov.vkwall.services.ApiHelper;
import com.aurozhkov.vkwall.services.ApiService;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import icepick.Icicle;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;


public class UserWallFragment extends VkFragment {

    @Arg long userId;

    @Inject ApiService apiService;
    @Inject ApiHelper apiHelper;
    @Inject @Named("pagination") int paginationPageSize;
    @Inject PaginationHelper paginationHelper;

    @InjectView(R.id.wall_fragment_loadable_card_layout) LoadableCardLayout loadableCardLayout;
    @InjectView(R.id.wall_fragment_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.wall_fragment_recycler_view) RecyclerView recyclerView;
    @InjectView(R.id.wall_fragment_toolbar) Toolbar toolbar;

    @Icicle ArrayList<VkWallRecord> vkWallRecords;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_wall, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.inflateMenu(R.menu.menu_frament_user_wall);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_logout) {
                    bus.post(new LogoutEvent());
                    return true;
                }
                return false;
            }
        });

        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.vk_color));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new MarginDecoration(getResources().getDimension(R.dimen.margin_decorator)));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (vkWallRecords == null) {
            loadableCardLayout.showLoading();
            loadData(false);
        } else {
            recyclerView.setAdapter(new WallRecyclerAdapter(getActivity(), vkWallRecords));
            activatePagination();
        }
    }

    @Override
    public void onDestroyView() {
        paginationHelper.diactivatePagination(recyclerView);
        super.onDestroyView();
    }

    @SuppressWarnings("UnusedDeclaration") // Used by Otto
    @Subscribe
    public void onRetryNetworkRequest(RetryNetworkRequestEvent event) {
        loadData(false);
    }

    @SuppressWarnings("UnusedDeclaration") // Used by Otto
    @Subscribe
    public void retryNextPageLoad(PaginationRetryClickEvent event) {
        paginationHelper.forcePagintaion(recyclerView);
    }

    private void loadData(final boolean refreshing) {
        AppObservable.bindFragment(this,
                apiService.getUserWall(userId, 0, paginationPageSize))
                .map(new Func1<WallResponse, Pair<Integer, List<VkWallRecord>>>() {
                    @Override
                    public Pair<Integer, List<VkWallRecord>> call(WallResponse wallResponse) {
                        return Pair.create(wallResponse.response.count, apiHelper.convertWallResponse(wallResponse));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Integer, List<VkWallRecord>>>() {
                    @Override
                    public void call(Pair<Integer, List<VkWallRecord>> results) {
                        vkWallRecords = new ArrayList<>(results.second);
                        if (refreshing) {
                            recyclerView.swapAdapter(new WallRecyclerAdapter(getActivity(), vkWallRecords), false);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            recyclerView.setAdapter(new WallRecyclerAdapter(getActivity(), vkWallRecords));
                        }
                        if (vkWallRecords.isEmpty()) {
                            loadableCardLayout.showNoContent();
                        } else {
                            loadableCardLayout.showContent();
                        }
                        if (results.first >= vkWallRecords.size()) {
                            activatePagination();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (refreshing) {
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            loadableCardLayout.showError();
                        }
                    }
                });
    }

    private void activatePagination() {
        Func1 paginationFunction = new Func1<Integer, Observable<WallResponse>>() {
            @Override
            public Observable<WallResponse> call(Integer integer) {
                return apiService.getUserWall(userId, integer, paginationPageSize);
            }
        };
        Func1 convertationFunction = new Func1<WallResponse, Pair<Integer, List<VkWallRecord>>>() {
            @Override
            public Pair<Integer, List<VkWallRecord>> call(WallResponse wallResponse) {
                return Pair.create(wallResponse.response.count, apiHelper.convertWallResponse(wallResponse));
            }
        };

        Action1 callback = new Action1<List<VkWallRecord>>() {
            @Override
            public void call(List<VkWallRecord> posts) {
                vkWallRecords.addAll(posts);
            }
        };

        paginationHelper.activatePagination(recyclerView, paginationFunction, convertationFunction, callback);
    }
}
