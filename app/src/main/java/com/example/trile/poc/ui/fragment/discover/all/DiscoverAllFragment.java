package com.example.trile.poc.ui.fragment.discover.all;


import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.trile.poc.R;
import com.example.trile.poc.database.entity.MangaItemEntity;
import com.example.trile.poc.databinding.FragmentDiscoverAllBinding;
import com.example.trile.poc.ui.activity.MainActivity;
import com.example.trile.poc.ui.adapter.MangaItemAdapter;
import com.example.trile.poc.ui.helper.CustomFastScroller;
import com.example.trile.poc.ui.helper.RecyclerViewSpacesItemDecoration;
import com.example.trile.poc.ui.listener.EndlessRecyclerViewScrollListener;
import com.example.trile.poc.ui.listener.OnMangaListInteractionListener;
import com.example.trile.poc.utils.InjectorUtils;
import com.example.trile.poc.utils.Objects;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverAllFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author trile
 * @since 5/22/18 at 14:11
 */
public class DiscoverAllFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_MANGA_ITEM_HEIGHT = "manga-item-height";
    private int mColumnCount = 1;
    private int mMangaItemImageHeight = 164;

    private FragmentDiscoverAllBinding mBinding;

    private SwipeRefreshLayout mSwipeRefreshContainer;
    private ProgressBar mLoadingIndicator;
    private MangaItemAdapter mMangaItemAdapter;
    private RecyclerView mRecyclerView;
    private EndlessRecyclerViewScrollListener mRecyclerViewScrollListener;
    private OnMangaListInteractionListener mListener;

    private DiscoverAllViewModel mViewModel;

    public DiscoverAllFragment() {
        // Required empty public constructor
    }

    public static DiscoverAllFragment newInstance(int columnCount, int mangaItemImageHeight) {
        DiscoverAllFragment fragment = new DiscoverAllFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_MANGA_ITEM_HEIGHT, mangaItemImageHeight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mMangaItemImageHeight = getArguments().getInt(ARG_MANGA_ITEM_HEIGHT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_discover_all, container, false);

        mSwipeRefreshContainer = mBinding.fragmentDiscoverAllSwipeRefreshContainer;
        mSwipeRefreshContainer.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryLight,
                R.color.colorSecondary
        );
        mSwipeRefreshContainer.setOnRefreshListener(() -> {
            mSwipeRefreshContainer.setRefreshing(false);
        });

        mLoadingIndicator = mBinding.pbLoadingIndicator;

        mRecyclerView = mBinding.fragmentDiscoverAllList;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                gridLayoutManager.getOrientation()
        );

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(getResources()
                .getDimensionPixelSize(R.dimen.recycler_grid_view_item_spacing)));
        // mRecyclerView.addItemDecoration(dividerItemDecoration);
        // Retain an instance so that you can call `resetState()` for fresh searches
        mRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                loadMoreOlderHousingsFromApi(page);
            }
        };
        StateListDrawable verticalThumbDrawable = (StateListDrawable) ContextCompat.getDrawable
                (getContext(), R.drawable.thumb_drawable);
        Drawable verticalTrackDrawable = ContextCompat.getDrawable(getContext(), R.drawable
                .line_drawable);
        StateListDrawable horizontalThumbDrawable = (StateListDrawable) ContextCompat.getDrawable
                (getContext(), R.drawable.thumb_drawable);
        Drawable horizontalTrackDrawable = ContextCompat.getDrawable(getContext(), R.drawable
                .line_drawable);
        int defaultWidth = getResources().getDimensionPixelSize(android.support.v7.recyclerview.R.dimen
                .fastscroll_default_thickness);
        int scrollbarMinimumRange = getResources().getDimensionPixelSize(android.support.v7.recyclerview.R.dimen
                .fastscroll_minimum_range);
        int margin = getResources().getDimensionPixelOffset(android.support.v7.recyclerview.R.dimen
                .fastscroll_margin);
        new CustomFastScroller(getContext(), mRecyclerView, verticalThumbDrawable,
                verticalTrackDrawable, horizontalThumbDrawable, horizontalTrackDrawable,
                defaultWidth, scrollbarMinimumRange, margin);

        mMangaItemAdapter = new MangaItemAdapter(getContext(), mListener, mMangaItemImageHeight);

        mRecyclerView.addOnScrollListener(mRecyclerViewScrollListener);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mViewModel.setRecyclerViewScrollOffset(
                        dx,
                        recyclerView.computeVerticalScrollOffset()
                );
            }
        });
        mRecyclerView.setAdapter(mMangaItemAdapter);
        showLoading();

        /**
         * This {@link DiscoverAllFragment} won't retain when we {@link NavController#navigate(int)}
         * between {@link NavDestination} in the app.
         * Using {@link ViewModelProviders#of(getActivity)} to survive the
         * {@link PagedList<MangaItemEntity> {@link DiscoverAllViewModel.mMangaItems}} until the
         * {@link MainActivity} is {@link MainActivity#finish()} so it won't have to reload
         * every time this {@link DiscoverAllFragment} is recreated.
         */
        mViewModel = ViewModelProviders
                .of(
                        getActivity(),
                        InjectorUtils
                                .provideDiscoverAllViewModelFactory(getActivity().getApplication())
                )
                .get(DiscoverAllViewModel.class);
        mViewModel.getAllMangaItems().observe(this, mangaItems -> {
            /**
             * Use {@link android.arch.paging.PagedListAdapter#submitList(PagedList)} rather than
             * the old custom setMangaList() for the Paging Library to handle the comparison between
             * the oldList & newList in its super constructor.
             */
            mMangaItemAdapter.submitList(mangaItems);

            if (Objects.nonNull(mangaItems) && mangaItems.size() > 0) {
                showMangaItemView();
            }
        });
        mViewModel.getTotalNumMangaItems().observe(this, totalNumMangaItems ->
                mBinding.totalNumOfManga.setText(
                        getString(
                                R.string.discover_all_tab_total_num_of_manga,
                                totalNumMangaItems
                        )
                )
        );
        mViewModel.getOrderBy().observe(this, orderBy -> {
            mBinding.sortByButton.setText(
                    getString(R.string.discover_all_tab_sort_by,
                            orderBy)
            );
        });
        if (mViewModel.getAllMangaItems().getValue() == null ||
                mViewModel.getAllMangaItems().getValue().isEmpty()) {
            mViewModel.setOrderBy(mViewModel.getOrderBy().getValue() == null
                    ? getString(R.string.discover_all_tab_sort_by_rank)
                    : mViewModel.getOrderBy().getValue()
            );
        }

        mBinding.sortByButton.setOnClickListener(v -> {
            final CharSequence[] items = {
                    getString(R.string.discover_all_tab_sort_by_name),
                    getString(R.string.discover_all_tab_sort_by_rank)
            };
            new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.discover_all_tab_sort_by_dialog_title))
                    .setSingleChoiceItems(
                            items,
                            mViewModel.getOrderBy().getValue() ==
                                    getString(R.string.discover_all_tab_sort_by_name)
                                    ? 0 : 1,
                            (dialog, which) -> {
                                mViewModel.setOrderBy(items[which].toString());
                                dialog.dismiss();
                            }
                    )
                    .show();
        });

        mRecyclerView.smoothScrollBy(
                mViewModel.getRecyclerViewScrollOffsetX(),
                mViewModel.getRecyclerViewScrollOffsetY()
        );

        return mBinding.getRoot();
    }

    public void setMangaListInteractionListener(OnMangaListInteractionListener listener) {
        mListener = listener;
    }

    private void showMangaItemView() {
        // First, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        // Finally, make sure the weather data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        // Then, hide the weather data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Finally, show the loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
