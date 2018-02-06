package com.ford.campos.testdrawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;

/**
 * Round 1 refactoring: 2/4/18
 */

public class Update {

    private static final String TAG = "Update";
    private static final long WEEK_MILLISECONDS = 604800000;

    /**
     * If it's been less than a week since last update, the file doesn't need an
     * update (return false), otherwise return it does need an update (return true).
     *
     * @param filename
     * @param context
     * @return
     */

    public static boolean needsUpdate(String filename, Context context) {

        if (!hasFile(context, filename))
            return true;
        else if (filename.equals("liked"))
            return false;

        long timeSinceLastUpdate = getTimeSinceLastUpdate(context, filename);
        Log.d(TAG, getTimeInHours(timeSinceLastUpdate) + " hrs since last updated");

        if (timeSinceLastUpdate < WEEK_MILLISECONDS) {
            Log.d(TAG, "returning false");
            return false;
        }

        toast(filename, context, timeSinceLastUpdate);
        Log.d(TAG, "returning true");
        return true;

    }

    public static boolean hasFile(Context context, String filename) {
        return context.getFileStreamPath(filename + ".txt").exists();
    }

    private static long getTimeSinceLastUpdate(Context context, String filename) {
        String lastUpdatedFilename = getLastUpdatedFilename(filename);
        long lastUpdated = getLastUpdated(lastUpdatedFilename, context);
        return Calendar.getInstance().getTimeInMillis() - lastUpdated;
    }

    private static String getLastUpdatedFilename(String filename) {
        return filename + "-last-updated.txt";
    }

    private static long getLastUpdated(String lastUpdatedFilename, Context context) {

        String filename = lastUpdatedFilename.substring(0, lastUpdatedFilename.length() - 4);
        if (!hasFile(context, filename)) {
            Log.d(TAG, lastUpdatedFilename + " doesn't exist, creating the file and returning 0");
            createLastUpdatedFile(context, lastUpdatedFilename);
            return 0;
        }

        long lastUpdated = context.getFileStreamPath(lastUpdatedFilename).lastModified();

        Log.d(TAG, lastUpdatedFilename + " " + lastUpdated + " ms ago");

        return lastUpdated;
    }

    private static long getTimeInHours(long timeSinceLastUpdate) {
        return ((timeSinceLastUpdate / 1000) / (60 * 60));
    }

    private static void createLastUpdatedFile(Context context, String lastUpdatedFilename) {

        OutputStream output = null;
        try {
            output = context.openFileOutput(lastUpdatedFilename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
        }

        Writer write = new OutputStreamWriter(output);
        try {
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void toast(String filename, Context context, long timeSinceLastUpdate) {
        Toast.makeText(
                context,
                "Updating " + filename + " Last updated: " +
                        getTimeInHours(timeSinceLastUpdate) + " hrs",
                Toast.LENGTH_LONG
        ).show();
    }


}
