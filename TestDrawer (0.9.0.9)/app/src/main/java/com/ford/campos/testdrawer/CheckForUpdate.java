package com.ford.campos.testdrawer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;

public class CheckForUpdate {

    private static final String TAG = "CheckForUpdate";
    private static final long WEEK_MILLISECONDS = 604800000;

    // If the file doesn't exist, we return true. If it's been less than a
    // week since last update, we return false, otherwise true
    public static boolean needsUpdate(String filename, Context context) {

        if( !hasFile(context, filename) )
            return true;
        else if( filename.equals("liked") )
            return false;

        String lastUpdatedFilename = filename + "-last-updated.txt";

        long lastUpdated = getLastUpdated(lastUpdatedFilename, context);
        long currTimeInMillis = Calendar.getInstance().getTimeInMillis();
        long timeSinceLastUpdate = currTimeInMillis - lastUpdated;

        long timeInHoursSinceLastUpdate = ((timeSinceLastUpdate / 1000) / (60 * 60));
        Log.d(TAG, timeInHoursSinceLastUpdate + " hrs since last updated");

        if ( timeSinceLastUpdate < WEEK_MILLISECONDS ) {
            Log.d(TAG, "returning false");
            return false;
        }

        Toast.makeText(context, "Updating " + filename + " Last updated: " +
                timeInHoursSinceLastUpdate + " hrs", Toast.LENGTH_LONG).show();
        Log.d(TAG, "returning true");
        return true;

    }

    public static boolean hasFile(Context context, String filename) {
        return context.getFileStreamPath(filename + ".txt").exists();
    }

    private static long getLastUpdated(String lastUpdatedFilename, Context context) {

        String temp = lastUpdatedFilename.substring(0, lastUpdatedFilename.length() - 4);

        if( !hasFile(context, temp) ) {
            Log.d(TAG, lastUpdatedFilename + " doesn't exist, creating the file and returning 0");
            createLastUpdatedFile(context, lastUpdatedFilename);
            return 0;
        }

        long lastUpdated = context.getFileStreamPath(lastUpdatedFilename).lastModified();

        Log.d(TAG, lastUpdatedFilename + " " + lastUpdated + " ms ago");

        return lastUpdated;
    }

    private static void createLastUpdatedFile(Context context, String lastUpdatedFilename) {

        OutputStream outTwo = null;
        try { outTwo = context.openFileOutput(lastUpdatedFilename, Context.MODE_PRIVATE); }
        catch (FileNotFoundException e) { }

        Writer writerTwo = new OutputStreamWriter(outTwo);
        try { writerTwo.close(); }
        catch (IOException e) { e.printStackTrace(); }

    }

}
