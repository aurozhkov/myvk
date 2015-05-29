package com.aurozhkov.vkwall.helpers;

import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aurozhkov.vkwall.ui.PaginableAdapter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class PaginationHelper {

    private static class PaginationInfo<T, K> {
        public final Func1<Integer, Observable<K>> paginationFunction;
        public final Func1<K, Pair<Integer, T>> convertationFunction;
        public final Action1<T> action;

        public Subscription subscription;

        private PaginationInfo(Func1<Integer, Observable<K>> paginationFunction, Func1<K, Pair<Integer, T>> convertationFunction, Action1<T> action) {
            this.paginationFunction = paginationFunction;
            this.convertationFunction = convertationFunction;
            this.action = action;
        }
    }

    @SuppressWarnings("unchecked")
    public <T, K> void activatePagination(RecyclerView recyclerView,
                                          Func1<Integer, Observable<K>> paginationFunction,
                                          Func1<K, Pair<Integer, T>> convertationFunction,
                                          Action1<T> action) {
        PaginationInfo paginationInfo = new PaginationInfo(paginationFunction, convertationFunction, action);
        recyclerView.setTag(paginationInfo);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int pastVisibleItems;
            int visibleItemCount;
            int totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                pastVisibleItems = recyclerView.getLayoutManager() instanceof GridLayoutManager
                        ? ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()
                        : ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    loadMore(recyclerView);
                }
            }
        });

    }

    public void forcePagintaion(RecyclerView recyclerView) {
        final PaginableAdapter adapter = (PaginableAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        adapter.setErrorVisible(false);
        loadMore(recyclerView);
    }

    public void diactivatePagination(final RecyclerView recyclerView) {
        PaginationInfo paginationInfo = (PaginationInfo) recyclerView.getTag();
        if (paginationInfo != null && paginationInfo.subscription != null && !paginationInfo.subscription.isUnsubscribed()) {
            paginationInfo.subscription.unsubscribe();
        }
        recyclerView.setOnScrollListener(null);
        recyclerView.setTag(null);
    }

    @SuppressWarnings("unchecked")
    private <T> void loadMore(final RecyclerView recyclerView) {
        final PaginableAdapter adapter = (PaginableAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }
        final PaginationInfo paginationInfo = (PaginationInfo) recyclerView.getTag();
        paginationInfo.subscription = Observable.just(adapter.getDataItemsCount())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return !adapter.isDuringPagination();
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer object) {
                        adapter.setProgressVisible(true);
                    }
                })
                .flatMap(paginationInfo.paginationFunction)
                .map(paginationInfo.convertationFunction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Integer, T>>() {
                    @Override
                    public void call(Pair<Integer, T> result) {
                        adapter.setProgressVisible(false);
                        adapter.addAll(result.second);
                        if (adapter.getDataItemsCount() >= result.first) {
                            recyclerView.setOnScrollListener(null);
                        }
                        if (paginationInfo.action != null) {
                            paginationInfo.action.call(result.second);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        adapter.setProgressVisible(false);
                        adapter.setErrorVisible(true);
                    }
                });
        recyclerView.setTag(paginationInfo);
    }
}
