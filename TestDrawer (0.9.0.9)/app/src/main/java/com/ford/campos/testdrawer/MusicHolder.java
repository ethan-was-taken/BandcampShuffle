package com.ford.campos.testdrawer;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ethan on 8/7/2015.
 *
 * Round 1 refactoring: 2/7/18
 */
public class MusicHolder {

    private static final String TAG = "MusicHolder";
    private static final int NUM_GENRES = 3;
    private static final int LIKED_POSITION = 2;

    /**
    *  Current genre/thing at position 1:   Nujabes
    *                                  2:   Future-Funk
    *                                  3:   Liked playlist
    **/
    private ArrayList<ArrayList<String>> musicArrayHolder = new ArrayList<>();
    private ArrayList<Integer> originalSizes = new ArrayList<>(NUM_GENRES);

    public MusicHolder() {  }

    public void add(ArrayList<String> toAdd) {
        musicArrayHolder.add(toAdd);
    }

    public void setOriginalSizes(int index, int size) {
        if(originalSizes.size() > NUM_GENRES)
            throw new RuntimeException("originalSizes too large");
        else if(originalSizes.size() < NUM_GENRES)
            originalSizes.add(size);
        else
            originalSizes.set(index, size);
    }

    public int getOriginalSizes(int index) {
        return originalSizes.get(index);
    }

    public ArrayList<String> get(int index) {
        return musicArrayHolder.get(index);
    }

    public ArrayList<String> get(String genre) {
        // I'm not too happy about this, I shouldn't really be calling other classes, but at the
        // same time, I don't want to have a getPositionForArray method in every class
        int index = MainActivityHelper.getPositionForArray(genre);
        return musicArrayHolder.get(index);
    }

    public void update(String currUrl, int addToGenre, int removeFromGenre) {

        if(addToGenre == -1)
            addToGenre = getGenreFromUrl(currUrl);
        // If it's still -1, something is seriously wrong!
        if (addToGenre == -1)
            Log.wtf(TAG, "addToGenre == -1", new RuntimeException());

        musicArrayHolder.get(removeFromGenre).remove(currUrl);
        musicArrayHolder.get(addToGenre).add(currUrl);

    }

    private int getGenreFromUrl(String url) {
        Integer temp = Integer.valueOf(url.substring(url.length() - 1));
        return temp;
    }

    public boolean isLikedEmpty() {
        return get(2).isEmpty();
    }

    public void set(ArrayList<String> toAdd, int position) {
        musicArrayHolder.set(position, toAdd);
        originalSizes.set(position, toAdd.size());
    }
}
