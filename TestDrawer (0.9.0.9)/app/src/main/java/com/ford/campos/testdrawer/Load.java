package com.ford.campos.testdrawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Mostly refactored on: 2/4/18
 *
 * Todo: fix setMusicHolderArraySize(); it is inexcusable.
 *
 */

public class Load {

    private final String TAG = "Load";
    private Context context;

    private String filename;
    private ArrayList<String> linksArray;

    public Load(Context c) {
        context = c;
        linksArray = new ArrayList<>();
    }

    /**
     * Gets the URL's from the specified txt file and sends back an ArrayList with
     * those links. If the file doesn't exist we return null.
     *
     * @param filename the file we're trying to load (not including ".txt")
     * @return if the file exists: an ArrayList with the URL's in the file; otherwise null.
     */
    public ArrayList<String> load(String filename) {
        setup(filename);
        setLinksArray();
        setMusicHolderArraySize();
        return linksArray;
    }

    private void setup(String filename) {
        this.filename = filename;
        this.filename += ".txt";
        checkIfFileExists();
    }

    /**
     * This should never throw a runtime exception, if it does... well, we know something has gone
     * horribly wrong.
     */
    private void checkIfFileExists() {
        if (!hasFile(context)) {
            Log.wtf( TAG, "Trying to load a non-existent file", new RuntimeException() );
        }
    }

    private boolean hasFile(Context context) {
        return context.getFileStreamPath(filename).exists();
    }

    /**
     * Wrapper method that adds all the url's in the specified file to the AraryList
     *
     * @throws RuntimeException if the URL's weren't able to be retrieved
     */
    private void setLinksArray() {
        try { getLinksFromFile(); }
        catch (IOException e) {
            Log.wtf(TAG, "couldn't get links from file", new RuntimeException());
        }
    }

    /**
     * Sets everything up for the file to be read, then calls addUrlsToLinksArray() to
     * do the actual work.
     *
     * @throws IOException if the file wasn't able to be opened
     */
    private void getLinksFromFile() throws IOException {

        InputStream in = context.openFileInput(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        addUrlsToLinksArray(reader);
        closeReader(reader);

    }

    /**
     * Adds a bunch of URL's to the linksArray.
     *
     * @throws IOException if the file wasn't able to be opened
     */
    private void addUrlsToLinksArray(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null)
            linksArray.add(line);
    }

    private void closeReader(BufferedReader reader) {
        if (reader != null) {
            Log.d(TAG, "reader != null, closing");
            try { reader.close(); }
            catch (IOException e) { Log.d(TAG, "reader!=null, IOException"); }
        }
    }

    /**
     * Todo: fix this!
     *
     * This is really bad, it'll need to be refactored; BUT the rest of the app needs to be
     * refactored first, so ill come back to it later
     */
    private void setMusicHolderArraySize() {
        int index = MainActivityHelper.getPositionForArray(filename);
        Log.d(TAG, filename + " loaded " + linksArray.size());
        ( (MainActivity)context ).setOriginalSizes(index, linksArray.size());
    }

}
