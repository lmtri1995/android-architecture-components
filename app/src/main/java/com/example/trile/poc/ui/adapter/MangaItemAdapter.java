package com.example.trile.poc.ui.adapter;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.trile.poc.Constants;
import com.example.trile.poc.R;
import com.example.trile.poc.database.AppDatabase;
import com.example.trile.poc.database.entity.MangaItemEntity;
import com.example.trile.poc.database.model.MangaItem;
import com.example.trile.poc.databinding.MangaItemBinding;
import com.example.trile.poc.repository.DataRepository;
import com.example.trile.poc.repository.paging.discover.all.MangaItemDataSource;
import com.example.trile.poc.ui.listener.OnMangaListInteractionListener;

import java.util.List;
import java.util.Objects;

/**
 * Adapter for Manga Item showing on Discover, Favorites & Downloads Tabs.
 *
 * @author trile
 * @since 5/22/18 at 13:59
 */
public class MangaItemAdapter
        extends PagedListAdapter<MangaItemEntity, MangaItemAdapter.MangaItemViewHolder> {
    public static final String TAG = MangaItemAdapter.class.getSimpleName();

    private final Context mContext;
    List<? extends MangaItem> mMangaList;
    private final int mMangaItemImageHeight;
    private CircularProgressDrawable mCircularProgressDrawable;

    @Nullable
    private final OnMangaListInteractionListener mListener;

    public MangaItemAdapter(@NonNull final Context context,
                            @Nullable OnMangaListInteractionListener listener,
                            final int mangaItemImageHeight) {
        super(DIFF_CALLBACK);
        mContext = context;
        mListener = listener;
        mMangaItemImageHeight = mangaItemImageHeight;

        mCircularProgressDrawable = new CircularProgressDrawable(context);
        mCircularProgressDrawable.setStrokeWidth(5f);
        mCircularProgressDrawable.setCenterRadius(30f);
    }

    @NonNull
    @Override
    public MangaItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MangaItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.manga_item,
                        parent, false);
        return new MangaItemViewHolder(binding, mMangaItemImageHeight);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaItemViewHolder holder, int position) {
        MangaItem mangaItem = getItem(position);
        if (mangaItem != null) {
            mCircularProgressDrawable.start();
            Glide.with(mContext)
                    .load(
                            String.format(
                                    Constants.MANGA_ROCK_THUMBNAIL_BASE_URL,
                                    Constants.GET_ALL_MANGAS_MSID_CODE,
                                    mangaItem.getId()
                            )
                    )
                    .apply(new RequestOptions().fitCenter()
                            .placeholder(mCircularProgressDrawable)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    )
                    .into(holder.mBinding.mangaImage);

            holder.mBinding.setManga(mangaItem);
            holder.mBinding.getRoot().setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onOpenMangaInfo(mangaItem);
                }
            });
            holder.mBinding.executePendingBindings();
        } else {
            holder.mBinding.invalidateAll();
        }
        /**
         * If we don't use {@link PagedList}, whenever we use the fast scroller to scroll from
         * the beginning to the end of the {@link RecyclerView},
         * this {@link Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
         * will be invoked to inflate all {@link MangaItem} in the {@link mMangaList} to the
         * {@link RecyclerView}.
         *
         * Else with the help of {@link PagedListAdapter#getItem(int position)}, whenever we use
         * the fast scroller to scroll from the beginning to the end of the {@link RecyclerView},
         * the Paging Library don't return every {@link MangaItem} from the {@link AppDatabase}
         * but only fetch the visible {@link MangaItem} on the screen and handle the prefetching
         * of a predefined number of {@link MangaItem} off the screen which defined by the
         * {@link PagedList.Config} in the {@link DataRepository}.
         * This means {@link MangaItem} can be {@code null} for items outside the prefetching
         * distance defined in {@link PagedList.Config}.
         *
         * By default (Placeholders Enabled = <code>{@TRUE}</code>), the {@link PagedList} (with the
         * {@link PagedList#size()} = total number of Manga Items we can load from
         * the {@link AppDatabase} - here is 500) will be populated with a number of Manga Items
         * from the {@link AppDatabase} equals to the {@link PagedList.Config.pageSize} and the
         * trailer {@code NULL} items in {@link PagedList} after the
         * {@link MangaItemDataSource#loadInitial()} is invoked.
         *
         * Afterwards, when we make a fast scroll to the end of the {@link PagedList}
         * the middle off-screen items in the {@link PagedList} will be populated with
         * dummy duplicated items.
         * The {@link PagedList} only be populated with the new {@link MangaItem},
         * loaded by {@link MangaItemDataSource#loadRange()} from the {@link AppDatabase},
         * which currently visible on the screen.
         */
        Log.d(TAG, "onBindViewHolder: position = " + position);
        Log.d(TAG, "onBindViewHolder: mangaItem = " + mangaItem);
        Log.d(TAG, "onBindViewHolder: image = " + holder.mBinding.mangaImage);
        Log.d(TAG, "onBindViewHolder: title = " + holder.mBinding.mangaTitle.getText());
        Log.d(TAG, "onBindViewHolder: subtitle = " + holder.mBinding.mangaSubtitle.getText());
    }

//    @Override
//    public int getItemCount() {
//        return mMangaList == null ? 0 : mMangaList.size();
//    }

    private static final DiffUtil.ItemCallback<MangaItemEntity> DIFF_CALLBACK = new DiffUtil
            .ItemCallback<MangaItemEntity>() {
        @Override
        public boolean areItemsTheSame(MangaItemEntity oldItem, MangaItemEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(MangaItemEntity oldItem, MangaItemEntity newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getAuthor(),newItem.getAuthor())
                    && oldItem.getRank() == newItem.getRank();
        }
    };

    /**
     * avoid using non-static inner class
     * watch here: https://www.youtube.com/watch?v=_CruQY55HOk
     */
    static class MangaItemViewHolder extends RecyclerView.ViewHolder {

        private final MangaItemBinding mBinding;

        public MangaItemViewHolder(MangaItemBinding binding, final int mangaItemImageHeight) {
            super(binding.getRoot());
            mBinding = binding;

            mBinding.mangaImage.getLayoutParams().height = mangaItemImageHeight;
        }
    }
}
