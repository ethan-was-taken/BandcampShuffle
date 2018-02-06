package com.ford.campos.testdrawer;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ethan on 8/7/2015.
 */
public class MusicHolder {

    private static final String TAG = "MusicHolder";
    private final int LIKED_POSITION = 2;

    /**
    *  Current genre/thing at position 1:   Nujabes
    *                                  2:   Future-Funk
    *                                  3:   Liked playlist
    **/
    private ArrayList<ArrayList<String>> musicArrayHolder = new ArrayList<>();
    private ArrayList<Integer> originalSizes = new ArrayList<>(3);

    public MusicHolder() {  }

    public void add(ArrayList<String> toAdd) {
        musicArrayHolder.add(toAdd);
    }

    public void setOriginalSizes(int index, int size) {
        Log.d(TAG, "originalSize.size() " + originalSizes.size());
        Log.d(TAG, "index " + index + " size " + size);

        if(originalSizes.size() < 3)
            originalSizes.add(size);
        else
            originalSizes.set(index, size);

        if(originalSizes.size() > 3)
            throw new RuntimeException("originalSizes too large");

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
            addToGenre = readd(currUrl);
        // If it's still -1, something is srsly wrong!
        if (addToGenre == -1)
            Log.wtf(TAG, "addToGenre == -1", new RuntimeException());

        musicArrayHolder.get(removeFromGenre).remove(currUrl);
        musicArrayHolder.get(addToGenre).add(currUrl);

    }

    private int readd(String url) {
        Integer temp = Integer.valueOf(url.substring(url.length() - 1));
        Log.d(TAG, "URL: " + url);
        Log.d(TAG, "addToGenre: " + temp);
        return temp;
    }

    public boolean isLikedEmpty() {

        Log.d(TAG, "musicArrayHolder size: " + musicArrayHolder.size());
        Log.d(TAG, "Liked size: " + get(LIKED_POSITION).size());
        return get(2).isEmpty();

    }

    public void set(ArrayList<String> toAdd, int position) {
        String TAG = "MusicHolderSet";
        Log.d(TAG, "setting " + position);
        Log.d(TAG, "size: " + toAdd.size());

        musicArrayHolder.set(position, toAdd);
        originalSizes.set(position, toAdd.size());
    }
}
