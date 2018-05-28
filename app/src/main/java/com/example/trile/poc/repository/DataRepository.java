package com.example.trile.poc.repository;

import android.arch.lifecycle.LiveData;

import com.example.trile.poc.AppExecutors;
import com.example.trile.poc.api.service.MangaNetworkDataSource;
import com.example.trile.poc.database.AppDatabase;
import com.example.trile.poc.database.entity.MangaItemEntity;

import java.util.List;

/**
 * Single Data Source of Truth of this whole Application.
 *
 * @author trile
 * @since 5/22/18 at 11:54
 */
public class DataRepository {
    public static final String TAG = DataRepository.class.getSimpleName();

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private final MangaNetworkDataSource mMangaNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private DataRepository(final AppDatabase database,
                           final MangaNetworkDataSource mangaNetworkDataSource,
                           final AppExecutors executors) {
        mDatabase = database;
        mMangaNetworkDataSource = mangaNetworkDataSource;
        mExecutors = executors;

        LiveData<List<MangaItemEntity>> networkData =
                mMangaNetworkDataSource.getDownloadedMangaItems();
        networkData.observeForever(newMangaItemsFromNetwork ->
                mExecutors.diskIO().execute(() ->
                        mDatabase.mangaItemDAO().insertAll(newMangaItemsFromNetwork)
                )
        );
    }

    /**
     * Get the singleton for this class
     */
    public static DataRepository getInstance(final AppDatabase database,
                                             final MangaNetworkDataSource mangaNetworkDataSource,
                                             final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database, mangaNetworkDataSource, executors);
                }
            }
        }
        return sInstance;
    }

    /**
     * Expose this LiveData (list of all manga items from the database) for others
     * and get notified when the local data changes.
     */
    public LiveData<List<MangaItemEntity>> getAllMangaItems() {
        initializeData();
        return mDatabase.mangaItemDAO().loadAllMangaItems();
    }

    public LiveData<MangaItemEntity> loadMangaItemById(final int mangaItemId) {
        initializeData();
        return mDatabase.mangaItemDAO().loadMangaItem(mangaItemId);
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        // This method call triggers Manga Rock to create its task to synchronize manga item data
        // periodically.
//        RetrofitClient.scheduleRecurringFetchMangaSync();

        mExecutors.diskIO().execute(() -> {
            if (isFetchNeeded()) {
                startFetchAllMangaItems();
            }
        });
    }

    /**
     * Checks if there is any manga for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private boolean isFetchNeeded() {
        return (!mDatabase.mangaItemDAO().isExistAnyManga());
    }

    /**
     * Network related operation
     */
    private void startFetchAllMangaItems() {
        mMangaNetworkDataSource.startFetchAllMangaItems();
    }
}