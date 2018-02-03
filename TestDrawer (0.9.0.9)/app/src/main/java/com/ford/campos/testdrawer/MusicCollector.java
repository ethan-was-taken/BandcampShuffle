package com.ford.campos.testdrawer;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashSet;

public class MusicCollector {

    private final String TAG = "MusicCollector";
    private final String LIKED = "liked";
    private final String NUJABES = "nujabes";
    private final String JAZZ_HOP = "jazzy-hip-hop";

    private HashSet<String> links = new HashSet<>();
    private String genre;
    private int position;

    public MusicCollector(String genre) {
        this.genre = genre;
        position = MainActivityHelper.getPositionForArray(this.genre);
    }

    public ArrayList<String> collect(ArrayList<String> oldLinks, ArrayList<String> likedAlbums) {

        links.addAll(oldLinks);
        Log.d(TAG, "Added " + links.size() + " url's to " + genre);

        return removeLikedFromUpdated(collect(false), likedAlbums);
    }

    // Could do this with asynch again, but for when it isn't the first time, so that it updates
    // links in the background
    public ArrayList<String> collect(boolean firstTime) {

        int upperBound = getUpperBound(firstTime);
        Log.d(TAG, "upperBound: " + upperBound);

        //Gets the links from pages 1 - 10; there are no duplicates
        for (int i = 1; i < upperBound; i++) {

            /*
            if( genre.equals(NUJABES) ) {

                boolean breakOne = getLinks(i, genre, firstTime);
                boolean breakTwo = getLinks(i, JAZZ_HOP, firstTime);

                if(breakOne && breakTwo)
                    break;

            } else if( getLinks(i, genre, firstTime) )
                break;
             */

            if ( getLinks(i, genre, firstTime) )
                break;

            //For the nujabes tag
            if (genre.equals(NUJABES) && getLinks(i, JAZZ_HOP, firstTime))
                break;

        }

        Log.d(TAG, genre + ".size(): " + links.size());

        return new ArrayList<>(links);

    }

    /**
     * Private/Protected methods
     */


    private int getUpperBound(boolean firstTime) {

        int upperBound = 11;

        if (genre.equals(NUJABES))
            upperBound = 6;

        //There aren't that many new albums added in a week
        //Remember to change back to 5
        if ( !firstTime && genre.equals(NUJABES) )
            upperBound = 4;
        else if(!firstTime)
            upperBound = 6;

        return upperBound;
    }

    private boolean getLinks(int index, String genreToGet, boolean firstTime) {

        Document doc;

        try {

            String url = "http://bandcamp.com/tag/" + genreToGet + "?page=" + index;

            if (!firstTime)
                url += "&sort_field=date";

/*
            String url = "http://bandcamp.com/tag/" + genreToGet + "?page=" + index + "&sort_field=date";

            if (firstTime)
                url = "http://bandcamp.com/tag/" + genreToGet + "?page=" + index;
*/
            long start = System.currentTimeMillis();
            doc = Jsoup.connect(url).timeout(5000).get();
            long end = System.currentTimeMillis();
            long timeTook = end - start;
            Log.d(TAG, "time it took to scrape 1 page: " + timeTook);

        } catch (IOException e) { return true; }

        //adds the links from page i to links
        int count = add(doc);

        Log.d(TAG, "added: " + count + " to " + genreToGet);
        return false;

    }

    // Adds album urls to the HashSet, and adds the genre number to the end of the url, so we know
    // where to add the url if the user unlikes the album
    private int add(Document doc) {

        Elements newsHeadlines = doc.select("a");
        int count = 0;

        long start = System.currentTimeMillis();

        for (int i = 0; i < newsHeadlines.size(); i++) {

            String temp = newsHeadlines.get(i).attr("abs:href");

            if (  /*temp.contains("http://bandcamp.com") ||*/
                  /*temp.contains("https://bandcamp.com") ||*/
                    temp.contains("://bandcamp.com") || temp.contains("\\n"))
                continue;
            else {
                count++;
                links.add(temp + position);
            }

        }

        long end = System.currentTimeMillis();
        long timeTook = end - start;

        Log.d(TAG, "Time it took to add urls to array: " + timeTook);

        return count;
    }

    private ArrayList<String> removeLikedFromUpdated(ArrayList<String> updatedLinks,
                                                     ArrayList<String> liked) {

        String TAG = "updateOldLinks";

        //int updatedLinksGenre = Character.getNumericValue( (updatedLinks.get(0).charAt(updatedLinks.get(0).length() - 1) ) );

        if(liked.size() == 0)
            return updatedLinks;

        Collections.sort(updatedLinks);

        for(String currLiked : liked) {

            Log.d(TAG, "trying to remove liked from updated links");

            int currLikedGenre = currLiked.charAt(currLiked.length() - 1) - 48;

            if(currLikedGenre == position)
                updatedLinks = removeCurrentUrl(updatedLinks, currLiked);

        }

        return updatedLinks;
    }

    private ArrayList<String> removeCurrentUrl(ArrayList<String> updatedLinks, String currLikedUrl) {

        String TAG = "updateOldLinks";
        Log.d(TAG, "Updating old links...");

        int position = Collections.binarySearch(updatedLinks, currLikedUrl);
        if(position < 0)
            return updatedLinks;

        if( updatedLinks.get(position).equals(currLikedUrl) ) {
            Log.d(TAG, updatedLinks.get(position) + " == " + currLikedUrl + "; removing from pos: " + position);
            updatedLinks.remove(position);
        }

        return updatedLinks;
    }

}
