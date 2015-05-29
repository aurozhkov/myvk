package com.aurozhkov.vkwall.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface PaginableAdapter<T> {

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(View view) {
            super(view);
        }
    }

    class ProgressErrorViewHolder extends RecyclerView.ViewHolder {

        final View view;

        public ProgressErrorViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    void setProgressVisible(boolean progressVisible);

    void setErrorVisible(boolean errorVisible);

    boolean isDuringPagination();

    void addAll(T t);

    int getDataItemsCount();
}
