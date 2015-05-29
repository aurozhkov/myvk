package com.aurozhkov.vkwall.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurozhkov.vkwall.R;
import com.aurozhkov.vkwall.VkApplication;
import com.aurozhkov.vkwall.events.PaginationRetryClickEvent;
import com.aurozhkov.vkwall.helpers.ImageLoader;
import com.aurozhkov.vkwall.model.VkWallRecord;
import com.squareup.otto.Bus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WallRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PaginableAdapter<List<VkWallRecord>> {

    static class WallViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.wall_item_avatar_image_view) ImageView avatar;
        @InjectView(R.id.wall_item_name_text_view) TextView name;
        @InjectView(R.id.wall_item_date_text_view) TextView date;
        @InjectView(R.id.wall_item_text_text_view) TextView text;
        @InjectView(R.id.wall_item_photo_image_view) DynamicHeightImageView photo;

        public WallViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_PROGRESS = 2;
    private static final int VIEW_TYPE_ERROR = 3;

    @Inject ImageLoader imageLoader;
    @Inject Bus bus;

    private final Context context;
    private final LayoutInflater layoutInflater;
    private List<VkWallRecord> wallRecords = Collections.emptyList();

    private boolean progressVisible;
    private boolean errorVisible;

    public WallRecyclerAdapter(Context context, List<VkWallRecord> records) {
        VkApplication.component(context).inject(this);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.wallRecords = new ArrayList<>(records);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && errorVisible) {
            return VIEW_TYPE_ERROR;
        } else if (position == getItemCount() - 1 && progressVisible) {
            return VIEW_TYPE_PROGRESS;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PROGRESS) {
            return new ProgressViewHolder(layoutInflater
                    .inflate(R.layout.recycler_view_pagination_progress, parent, false));
        } else if (viewType == VIEW_TYPE_ERROR) {
            return new ProgressErrorViewHolder(layoutInflater
                    .inflate(R.layout.recycler_view_pagination_error, parent, false));
        } else {
            return new WallViewHolder(layoutInflater
                    .inflate(R.layout.recycler_view_wall_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_ITEM) {
            WallViewHolder wallViewHolder = (WallViewHolder) holder;
            VkWallRecord vkWallRecord = wallRecords.get(position);
            imageLoader.cancel(wallViewHolder.avatar);
            imageLoader.cancel(wallViewHolder.photo);
            imageLoader.loadAvatar(wallViewHolder.avatar, vkWallRecord.author());
            if (vkWallRecord.author() != null) {
                wallViewHolder.name.setText(vkWallRecord.author().firstName() + " " + vkWallRecord.author().lastName());
            } else {
                wallViewHolder.name.setText(context.getString(R.string.unknown_author));
            }
            wallViewHolder.date.setText(DATE_FORMAT.format(vkWallRecord.date()));
            if (TextUtils.isEmpty(vkWallRecord.text()) && vkWallRecord.photo() == null) {
                wallViewHolder.text.setVisibility(View.VISIBLE);
                wallViewHolder.photo.setVisibility(View.GONE);
                wallViewHolder.text.setText(context.getString(R.string.unknown_format));
            } else {
                if (!TextUtils.isEmpty(vkWallRecord.text())) {
                    wallViewHolder.text.setVisibility(View.VISIBLE);
                    wallViewHolder.text.setText(Html.fromHtml(vkWallRecord.text()));
                } else {
                    wallViewHolder.text.setVisibility(View.GONE);
                }
                if (vkWallRecord.photo() != null) {
                    wallViewHolder.photo.setVisibility(View.VISIBLE);
                    wallViewHolder.photo.setHeightRatio((1f * vkWallRecord.photo().height()) / (1f * vkWallRecord.photo().width()));
                    imageLoader.loadPhoto(wallViewHolder.photo, vkWallRecord);
                } else {
                    wallViewHolder.photo.setVisibility(View.GONE);
                }
            }
        } else if (viewType == VIEW_TYPE_ERROR) {
            ((ProgressErrorViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bus.post(new PaginationRetryClickEvent());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return wallRecords.size() + (errorVisible || progressVisible ? 1 : 0);
    }

    @Override
    public long getItemId(int position) {
        if (position == getItemCount() - 1) {
            if (errorVisible) {
                return -1;
            } else if (progressVisible) {
                return -2;
            }
        }
        return wallRecords.get(position).id();
    }

    @Override
    public void setProgressVisible(boolean progressVisible) {
        this.progressVisible = progressVisible;
        if (progressVisible) {
            notifyItemInserted(getItemCount());
        } else {
            notifyItemRemoved(getItemCount());
        }
    }

    @Override
    public void setErrorVisible(boolean errorVisible) {
        this.errorVisible = errorVisible;
        if (errorVisible) {
            notifyItemInserted(getItemCount());
        } else {
            notifyItemRemoved(getItemCount());
        }
    }

    @Override
    public boolean isDuringPagination() {
        return errorVisible || progressVisible;
    }

    @Override
    public void addAll(List<VkWallRecord> vkWallRecords) {
        this.wallRecords.addAll(vkWallRecords);
        notifyItemRangeInserted(getItemCount() - vkWallRecords.size(), vkWallRecords.size());
    }

    @Override
    public int getDataItemsCount() {
        return wallRecords.size();
    }
}
