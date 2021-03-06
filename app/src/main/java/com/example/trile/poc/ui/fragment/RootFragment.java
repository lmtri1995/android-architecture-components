package com.example.trile.poc.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trile.poc.Constants;
import com.example.trile.poc.R;
import com.example.trile.poc.ui.fragment.discover.DiscoverFragment;
import com.example.trile.poc.ui.listener.OnMangaListInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RootFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author trile
 * @since 5/22/18 at 14:15
 */
public class RootFragment extends Fragment {
    private int viewPagerIndex;

    private View.OnClickListener mNavigationToolbarButtonOnClickListener;
    private OnMangaListInteractionListener mMangaListInteractionListener;

    public RootFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RootFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RootFragment newInstance(int viewPagerIndex) {
        RootFragment fragment = new RootFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM, viewPagerIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewPagerIndex = getArguments()
                    .getInt(Constants.ROOT_FRAGMENT_VIEW_PAGER_INDEX_PARAM, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_root, container, false);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Fragment fragment;

        switch (viewPagerIndex) {
            case Constants.VIEW_PAGER_INDEX_DISCOVER:
                fragment = DiscoverFragment.newInstance();
                ((DiscoverFragment) fragment)
                        .setNavigationOnClickListener(mNavigationToolbarButtonOnClickListener);
                ((DiscoverFragment) fragment)
                        .setMangaListInteractionListener(mMangaListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_FAVORITES:
                fragment = DiscoverFragment.newInstance();
                ((DiscoverFragment) fragment)
                        .setNavigationOnClickListener(mNavigationToolbarButtonOnClickListener);
                ((DiscoverFragment) fragment)
                        .setMangaListInteractionListener(mMangaListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
//                fragment = FavoritesFragment.newInstance();
//                ((FavoritesFragment) fragment)
//                        .setMangaListInteractionListener(mMangaListInteractionListener);
//                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_RECENT:
                fragment = DiscoverFragment.newInstance();
                ((DiscoverFragment) fragment)
                        .setNavigationOnClickListener(mNavigationToolbarButtonOnClickListener);
                ((DiscoverFragment) fragment)
                        .setMangaListInteractionListener(mMangaListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
//                fragment = RecentFragment.newInstance(1);
//                ((RecentFragment) fragment)
//                        .setMangaListInteractionListener(mMangaListInteractionListener);
//                transaction.replace(R.id.root_container, fragment);
                break;
            case Constants.VIEW_PAGER_INDEX_DOWNLOADS:
                fragment = DiscoverFragment.newInstance();
                ((DiscoverFragment) fragment)
                        .setNavigationOnClickListener(mNavigationToolbarButtonOnClickListener);
                ((DiscoverFragment) fragment)
                        .setMangaListInteractionListener(mMangaListInteractionListener);
                transaction.replace(R.id.root_container, fragment);
//                fragment = DownloadsFragment.newInstance();
//                ((DownloadsFragment) fragment)
//                        .setMangaListInteractionListener(mMangaListInteractionListener);
//                transaction.replace(R.id.root_container, fragment);
                break;
        }
        transaction.commit();

        return view;
    }

    public void setNavigationClickListener(View.OnClickListener navigationClickListener) {
        mNavigationToolbarButtonOnClickListener = navigationClickListener;
    }

    public void setMangaListInteractionListener(OnMangaListInteractionListener mangaListInteractionListener) {
        mMangaListInteractionListener = mangaListInteractionListener;
    }

    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentById(R.id.root_container);
    }
}
