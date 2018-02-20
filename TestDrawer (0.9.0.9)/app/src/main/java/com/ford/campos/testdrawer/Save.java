package com.ford.campos.testdrawer;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ethan on 8/26/2015.
 */
public class Save {
    private final String TAG = "Save";

    private Context context;

    public Save(Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }

    // Saves the links gathered to a txt file, if there is an IOException
    // it returns false, otherwise true
    public boolean save(String filename, ArrayList<String> linksArray, boolean isUpdating) {

        int position = MainActivityHelper.getPositionForArray(filename);

        String lastUpdatedFilename = filename + "-last-updated.txt";
        filename = filename + ".txt";

        Writer writer = null;

        //try {
        /**
         * extracting this (the things below and up to the finally statement) and putting it in
         * it's own method causes some big problems
         */
        ////////////////////////////////////////////////////////////////////////////////////////
        //This section tells us whether or not we should delete the file
        try {

            int originalSize = ((MainActivity) context).getOriginalSizes(position);

            if (linksArray.size() == originalSize)
                return false;
            else if (hasFile(context, filename) && hasFile(context, lastUpdatedFilename))
                deleteFiles(filename, lastUpdatedFilename, isUpdating);

        } catch (IndexOutOfBoundsException e) {
            //We get here when we first start the program, so we don't need to delete the file
            Log.d(TAG, "IndexOutOfBoundsException");
        }
        //
        ////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////
        //This section writes info to a file
        OutputStream out = null;
        try {
            out = context.openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
        }

        writer = new OutputStreamWriter(out);

        // Write the links array to a file
        try {

            // Creates a file (with nothing in it) so we can check the last time it was
            // modified
            if (isUpdating)
                createLastUpdatedFile(lastUpdatedFilename);

            for (String s : linksArray) {
                writer.write(s);
                writer.write("\n");
            }

        } catch (IOException e) {
            Log.d(TAG, "IOException trying to save. Returning false");
            return false;
        }

        Log.d("MusicCollector", "saved " + filename);
        //
        ////////////////////////////////////////////////////////////////////////////////////////

        //} finally {

        if (!closeWriter(writer))
            return false;

        //}


        //if the sizes change, then change originalSizes, so we know later if things have changed
        //if (!isUpdating)
        ((MainActivity) context).setOriginalSizes(position, linksArray.size());

        if (!hasFile(context, filename))
            throw new RuntimeException(filename + " !exists");
        else
            Log.d(TAG, filename + " exists");

        return true;
    }

    private void deleteFiles(String filename, String lastUpdatedFilename, boolean isUpdating) {

        context.deleteFile(filename);
        Log.d(TAG, "deleted: " + filename);

        if (isUpdating) {
            context.deleteFile(lastUpdatedFilename);
            Log.d(TAG, "deleted: " + lastUpdatedFilename);
        }

    }

    private void createLastUpdatedFile(String lastUpdatedFilename) {

        OutputStream outTwo = null;
        try {
            outTwo = context.openFileOutput(lastUpdatedFilename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
        }

        Writer writerTwo = new OutputStreamWriter(outTwo);
        closeWriter(writerTwo);

        if (!hasFile(context, lastUpdatedFilename)) {
            throw new RuntimeException(lastUpdatedFilename + " doesn't exist, even " +
                    "though it should");
        }


    }

    private boolean closeWriter(Writer writer) {

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    private boolean hasFile(Context context, String filename) {
        return context.getFileStreamPath(filename).exists();
    }

}

