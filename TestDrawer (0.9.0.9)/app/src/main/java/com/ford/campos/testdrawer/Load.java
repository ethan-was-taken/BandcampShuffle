package com.ford.campos.testdrawer;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Load {

    private final String TAG = "Load";
    private Context context;

    public Load(Context c) {
        context = c;
    }

    // Gets the links from the txt file and sends back an arrayList with
    // the links in it, if the file doesn't exist we return null
    public ArrayList<String> load(String filename) {

        //This shouldn't happen; if it does... well, we know something is wrong
        if (!hasFile(context, filename)) {
            Log.wtf( TAG, "Trying to load a non-existent file", new RuntimeException() );
        }

        filename += ".txt";

        ArrayList<String> linksArray = new ArrayList<>();

        try { linksArray = getLinksFromFile(filename); }
        catch (IOException e) {
            Log.wtf(TAG, "What the fuck, why am I here?", new RuntimeException());
        }

        int index = MainActivityHelper.getPositionForArray(filename);
        Log.d(TAG, filename + " loaded " + linksArray.size());
        ( (MainActivity)context ).setOriginalSizes(index, linksArray.size());

        return linksArray;

    }

    private ArrayList<String> getLinksFromFile(String filename) throws IOException {

        InputStream in = context.openFileInput(filename);
        ArrayList<String> linksArray = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;

        while ((line = reader.readLine()) != null)
            linksArray.add(line);

        if (reader != null) {
            Log.d(TAG, "reader != null, closing");
            try { reader.close(); }
            catch (IOException e) { Log.d(TAG, "reader!=null, IOException"); }
        }

        return linksArray;
    }

    private boolean hasFile(Context context, String filename) {
        return context.getFileStreamPath(filename + ".txt").exists();
    }

}
