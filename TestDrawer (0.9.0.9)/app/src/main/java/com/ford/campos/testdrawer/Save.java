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
 *
 *
 */
public class Save {

    private final String TAG = "Save";

    private Context context;
    private String filename;
    private String lastUpdatedFilename;
    private int position;
    private ArrayList<String> linksArray;
    private boolean isUpdating;

    private OutputStream out;
    private Writer writer;

    public Save(Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Saves the links gathered to a .txt file, because there's no need to make this more
     * complicated than that.
     * @param filename
     * @param linksArray
     * @param isUpdating
     * @return false if there's an IO exception or there was no need to save, otherwise true
     */
    public boolean save(String filename, ArrayList<String> linksArray, boolean isUpdating) {

        setGlobals(filename, linksArray, isUpdating);

        int originalSize = ((MainActivity) context).getOriginalSizes(position);
        if (linksArray.size() == originalSize)
            return false;

        removeFile();

        boolean wasWriteSuccessful = writeFile();
        if (!wasWriteSuccessful)
            return false;

        if (!closeWriter(writer))
            return false;

        updateOriginalSize();

        if (!hasFile(this.filename))
            throw new RuntimeException(filename + " !exists");
        else
            Log.d(TAG, filename + " exists");

        return true;
    }

    /**
     * if the sizes change, then change originalSizes, so we know later if things have changed
     * if (!isUpdating)
     */
    private void updateOriginalSize() {
        ((MainActivity) context).setOriginalSizes(position, this.linksArray.size());
    }

    private boolean writeFile() {

        out = null;
        try { out = context.openFileOutput(filename, Context.MODE_PRIVATE); }
        catch (FileNotFoundException e) { }

        writer = new OutputStreamWriter(out);

        try { writeLinksToFile(); }
        catch (IOException e) {
            Log.d(TAG, "IOException trying to save. Returning false");
            return false;
        }

        Log.d("MusicCollector", "saved " + filename);
        return true;

    }

    private void writeLinksToFile() throws IOException {
        // Creates a file (with nothing in it) so we can check the last time it was
        // modified
        if (isUpdating)
            createLastUpdatedFile();

        for (String s : linksArray) {
            writer.write(s);
            writer.write("\n");
        }
    }

    private void setGlobals(String filename, ArrayList<String> linksArray, boolean isUpdating) {
        position = MainActivityHelper.getPositionForArray(filename);
        lastUpdatedFilename = filename + "-last-updated.txt";
        this.linksArray = linksArray;
        this.filename = filename + ".txt";
        this.isUpdating = isUpdating;
    }

    private void removeFile() {
        try {
            if (hasFile(filename) && hasFile(lastUpdatedFilename))
                deleteFiles();
        } catch (IndexOutOfBoundsException e) {
            //We get here when we first start the program, so we don't need to delete the file
            Log.d(TAG, "IndexOutOfBoundsException");
        }
    }

    private void deleteFiles() {

        context.deleteFile(filename);
        Log.d(TAG, "deleted: " + filename);

        if (isUpdating) {
            context.deleteFile(lastUpdatedFilename);
            Log.d(TAG, "deleted: " + lastUpdatedFilename);
        }

    }

    private void createLastUpdatedFile() {

        OutputStream outTwo = null;
        try {
            outTwo = context.openFileOutput(lastUpdatedFilename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) { }

        Writer writerTwo = new OutputStreamWriter(outTwo);
        closeWriter(writerTwo);

        if (!hasFile(lastUpdatedFilename)) {
            throw new RuntimeException(lastUpdatedFilename + " doesn't exist, even " +
                    "though it should");
        }


    }

    private boolean closeWriter(Writer writer) {

        if (writer != null) {
            try { writer.close(); }
            catch (IOException e) { return false; }
        }

        return true;
    }

    private boolean hasFile(String filename) {
        return context.getFileStreamPath(filename).exists();
    }

}

