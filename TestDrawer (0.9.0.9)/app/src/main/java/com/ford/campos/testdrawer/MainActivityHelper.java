package com.ford.campos.testdrawer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * These methods don't belong in MainActivity, but kinda also do, and I don't know where to put
 * them. So I'm hiding them away in some backwater class
 *
 * One day this class will get broken up and everything will be okay with the world... except
 * for the second part
 */
public class MainActivityHelper {

    private static final String TAG = "MainActivityHelper";
    private static final String NUJABES = "nujabes";
    private static final String FUTURE_FUNK = "future-funk";
    private static final String LIKED = "liked";
    private static final int LIKED_POSITION = 2;


    public static ArrayList<String> loadLinks(Context context, String genre/*, boolean forceUpdate*/) {

        boolean needsUpdate = CheckForUpdate.needsUpdate(genre, context);
        SaveVersionTwo save = new SaveVersionTwo(context);
        Load load = new Load(context);

        Log.d(TAG, "needsUpdate: " + needsUpdate);

        if( needsUpdate && CheckForUpdate.hasFile(context, genre) ) {
            Log.d(TAG, "forceUpdate || ( needsUpdate && CheckForUpdate.hasFile(context, genre) )");
            collectAndSaveLinks(false, genre, save, load);
        }else if(needsUpdate) {
            Log.d(TAG, "needsUpdate");
            collectAndSaveLinks(true, genre, save, load);
        }

        return load.load(genre);

    }

    public static int getPositionForArray(String filename) {
        switch(filename) {
            case NUJABES:       return 0;
            case FUTURE_FUNK:   return 1;
            case LIKED:         return LIKED_POSITION;
            default:            return -1;
        }
    }

    private static void collectAndSaveLinks(final boolean isFirstTime, final String genre,
                                            final SaveVersionTwo save, final Load load) {

        if(!CheckForUpdate.hasFile(save.getContext(), genre))
            Log.d("MainActivity", "file DOESN'T exist, attempting the create file...");

        final MusicCollector collector = new MusicCollector(genre);

        // I should probably change this, there is probably a better way to handle the main thread
        // not being able to access the internet. Also, fuck AsynchTask
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> linksToSave = new ArrayList<>();

                if( isFirstTime && !genre.equals(LIKED) ) {
                    Log.d(TAG, "isFirstTime");
                    linksToSave = collector.collect(true);
                }else if( !genre.equals(LIKED) ){
                    Log.d(TAG, "adding files from new");

                    //returns an ArrayList of the specified genre with the liked albums removed
                    linksToSave = collector.collect(
                            load.load(genre),
                            load.load(LIKED)
                    );

                }
                save.save(genre, linksToSave, true);
                Log.d(TAG, "saved url's");

            }
        });

        thread.start();
        try { thread.join(); }
        catch (InterruptedException e) { Log.d(TAG, "InterruptedException"); }

        Log.d(TAG, "File created and saved");
    }

}
