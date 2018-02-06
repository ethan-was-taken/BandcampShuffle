package com.ford.campos.testdrawer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Still needs a second, thrid, forth, etc round of refactoring :C
 * Until I do the other classes, that'll do pig... that,ll do
 */
public class MusicCollector {

    private final String NUJABES = "nujabes";
    private final String JAZZ_HOP = "jazzy-hip-hop";

    private HashSet<String> links = new HashSet<>();
    private String genre;
    private int currGenreIndex;

    private boolean isFirstTime;

    public MusicCollector(String genre) {
        this.genre = genre;
        currGenreIndex = MainActivityHelper.getPositionForArray(this.genre);
    }

    public ArrayList<String> collect(ArrayList<String> oldLinks, ArrayList<String> likedAlbums) {
        links.addAll(oldLinks);
        ArrayList<String> newAlbums = collect(false);
        return removeLikedFromUpdated(newAlbums, likedAlbums);
    }

    /**
     * Ideally this would be done in the background, but I'm not too sure how that would be done
     */
    public ArrayList<String> collect(boolean firstTime) {

        isFirstTime = firstTime;
        int upperBound = getUpperBound();

        //Gets the links from pages 1 - 10; there are no duplicates
        for (int i = 1; i < upperBound; i++) {
            try { getAlbumLinks(i, genre); }
            catch (IOException e) { break; }
            handleSpecialCaseNujabes(i);
        }

        return new ArrayList<>(links);

    }

    private int getUpperBound() {
        return (genre.equals(NUJABES) || !isFirstTime) ? 6 : 11;
    }

    private void getAlbumLinks(int index, String genreToGet) throws IOException {
        String url = getUrl(index, genreToGet);
        Document doc = Jsoup.connect(url).timeout(5000).get();
        addAlbumUrlToLinks(doc);
    }

    private String getUrl(int index, String genreToGet) {
        StringBuilder url = new
                StringBuilder("http://bandcamp.com/tag/" + genreToGet + "?page=" + index);
        if (!isFirstTime)
            url.append("&sort_field=date");
        return url.toString();
    }

    // Adds album urls to the HashSet, and adds the genre number to the end of the url, so we know
    // where to addAlbumUrlToLinks the url if the user unlikes the album
    private int addAlbumUrlToLinks(Document doc) {

        Elements headlines = doc.select("a");
        int count = 0;

        for (int i = 0; i < headlines.size(); i++) {
            String temp = headlines.get(i).attr("abs:href");
            if (!isInvalidUrl(temp)){
                count++;
                links.add(temp + currGenreIndex);
            }
        }

        return count;

    }

    private boolean isInvalidUrl(String temp) {
        return temp.contains("://bandcamp.com") || temp.contains("\\n");
    }

    private void handleSpecialCaseNujabes(int i) {
        if (!genre.equals(NUJABES))
            return;
        try { getAlbumLinks(i, JAZZ_HOP); }
        catch (IOException e) { }
    }

    private ArrayList<String> removeLikedFromUpdated(ArrayList<String> albumLinks,
                                                     ArrayList<String> liked) {

        if (liked.size() == 0)
            return albumLinks;

        HashSet<String> currentUrls = new HashSet<>(albumLinks);
        for (String currLiked : liked) {
            int currAlbumGenreFromLiked = getGenre(currLiked);
            if (currAlbumGenreFromLiked == currGenreIndex)
                removeCurrentUrl(currentUrls, currLiked);
        }

        return hashSetToArrayList(currentUrls);
    }

    private int getGenre(String currLiked) {
        return currLiked.charAt(currLiked.length() - 1) - 48;
    }

    private void removeCurrentUrl(HashSet<String> currUrls, String currLikedUrl) {
        if (currUrls.contains(currLikedUrl))
            currUrls.remove(currLikedUrl);
    }

    private ArrayList<String> hashSetToArrayList(HashSet<String> currentUrls) {
        ArrayList<String> res = new ArrayList<>();
        res.addAll(currentUrls);
        return res;
    }

}
