package com.example.trile.poc.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.trile.poc.database.model.MangaItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author trile
 * @since 5/22/18 at 14:07
 */
@Entity
public class MangaItemEntity implements MangaItem {

    @PrimaryKey
    @SerializedName("mid")
    private int Id;

    @SerializedName("name")
    private String Name;
    @SerializedName("author")
    private String Author;
    @SerializedName("rank")
    private int Rank;

    @Ignore
    @SerializedName("categories")
    private ArrayList<Integer> Genres;  // Only use for object mapping on API Request, then store
                                        // this in MangaGenreEntity to manage Genres of each Manga,
                                        // so we @Ignore this when storing data into Room Database.
//    private boolean Favorited = false;
//    private String RecentEpisode = "";
//    private Long ReadStartTime = -1L;
//    private Long ReadElapsedTime = -1L;
//    private int NumChapterDownloaded = 0;

    public MangaItemEntity() {
    }

    public MangaItemEntity(int id, String name, String author, int rank) {
        Id = id;
        Name = name;
        Author = author;
        Rank = rank;
    }

    public MangaItemEntity(MangaItem mangaItem) {
        Id = mangaItem.getId();
        Name = mangaItem.getName();
        Author = mangaItem.getAuthor();
        Rank = mangaItem.getRank();
    }

//    public MangaItemEntity(String name, String author, boolean favorited, String recentEpisode,
//                           Long readStartTime, Long readElapsedTime, int numChapterDownloaded) {
//        this.Name = name;
//        this.Author = author;
//        this.Favorited = favorited;
//        this.RecentEpisode = recentEpisode;
//        this.ReadStartTime = readStartTime;
//        this.ReadElapsedTime = readElapsedTime;
//        this.NumChapterDownloaded = numChapterDownloaded;
//    }
//
//    public MangaItemEntity(MangaItem mangaItem) {
//        this.Id = mangaItem.getId();
//        this.Name = mangaItem.getName();
//        this.Author = mangaItem.getAuthor();
//        this.Favorited = mangaItem.getFavorited();
//        this.RecentEpisode = mangaItem.getRecentEpisode();
//        this.ReadStartTime = mangaItem.getReadStartTime();
//        this.ReadElapsedTime = mangaItem.getReadElapsedTime();
//        this.NumChapterDownloaded = mangaItem.getNumChapterDownloaded();
//    }

    @Override
    public int getId() {
        return Id;
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public String getAuthor() {
        return Author;
    }

    @Override
    public int getRank() {
        return Rank;
    }

    @Override
    public ArrayList<Integer> getGenres() {
        return Genres;
    }

    //    @Override
//    public boolean getFavorited() {
//        return Favorited;
//    }
//
//    @Override
//    public String getRecentEpisode() {
//        return RecentEpisode;
//    }
//
//    @Override
//    public Long getReadStartTime() {
//        return ReadStartTime;
//    }
//
//    @Override
//    public Long getReadElapsedTime() {
//        return ReadElapsedTime;
//    }
//
//    @Override
//    public int getNumChapterDownloaded() {
//        return NumChapterDownloaded;
//    }

    public void setId(int id) {
        this.Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setRank(int rank) {
        Rank = rank;
    }

    //    public void setFavorited(boolean favorited) {
//        Favorited = favorited;
//    }
//
//    public void setRecentEpisode(String recentEpisode) {
//        RecentEpisode = recentEpisode;
//    }
//
//    public void setReadStartTime(Long readStartTime) {
//        ReadStartTime = readStartTime;
//    }
//
//    public void setReadElapsedTime(Long readElapsedTime) {
//        ReadElapsedTime = readElapsedTime;
//    }
//
//    public void setNumChapterDownloaded(int numChapterDownloaded) {
//        NumChapterDownloaded = numChapterDownloaded;
//    }
}
