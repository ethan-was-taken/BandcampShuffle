package com.ford.campos.testdrawer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

public class MusicFinderFragment extends Fragment {

    private static final String TAG = "MusicFinderFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LAST_URL = "url";

    private LoadingScreen loadingScreen;
    private boolean firstPass = true;
    private WebView webView;
    private String currentUrl;

    private int lastRandomNum = -1;

    public MusicFinderFragment() { }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MusicFinderFragment newInstance(String genre, int sectionNumber) {
        MusicFinderFragment fragment = new MusicFinderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_music_finder, container, false);
        ImageView loadingImageView = (ImageView) rootView.findViewById(R.id.loading);

        loadingScreen = new LoadingScreen(
                getActivity(),
                loadingImageView,
                R.drawable.spinner_small,
                120,
                1000,
                R.anim.progress_anim
        );

        webView = new WebView(getActivity().getApplicationContext());
        webView = (WebView) rootView.findViewById(R.id.internet_web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        int genrePosition = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
        Log.d(TAG, "genrePosition: " + genrePosition);
        Log.d(TAG, "MusicHolder.get(genrePosition).size(): " +
                ((MainActivity) getActivity()).getMusic(genrePosition).size());
        getAndLoadRandomAlbum(((MainActivity) getActivity()).getMusic(genrePosition));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ( (MainActivity) activity ).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void getAndLoadRandomAlbum(ArrayList<String> linksArray) {

        if(linksArray.size() == 0)
            return;

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100)
                    startAnimation();
                else {
                    stopAnimation();
                    firstPass = true;
                }
            }
        });

        //can be refactored
        int index = getRandomNumber(linksArray.size());

        currentUrl = linksArray.get(index);

        Log.d(TAG, "current url: " + currentUrl.substring(0, currentUrl.length() - 1) +
                " from genre position: " + currentUrl.charAt(currentUrl.length() - 1) );

        //If true, this means that we're in the liked playlist and there's only one liked album,
        //so we don't bother reloading it
        if ( currentUrl.substring(0, currentUrl.length() - 1).equals( webView.getUrl() ) )
            return;

        webView.loadUrl(currentUrl.substring(0, currentUrl.length() - 1));
        Log.d(TAG, "getAndLoadRandomAlbum webView.getUrl:" + getCurrentUrl());

    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void kill() {
        Log.d(TAG, "kill");

        loadingScreen.kill();
        loadingScreen = null;

        currentUrl = null;

        refresh();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
    }

    public void refresh() {
        webView.freeMemory();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.clearFormData();
        webView.clearMatches();
    }

    /**
     * Private Methods
     */

    private void startAnimation() {

        // We don't need to restart the loading screen everytime this is called
        if (!firstPass)
            return;

        webView.setVisibility(View.INVISIBLE);
        loadingScreen.startAnimation();

        firstPass = false;

    }

    private void stopAnimation() {

        webView.setVisibility(View.VISIBLE);
        loadingScreen.stopAnimation();

    }

    private int getRandomNumber(int size) {

        int rand;

        if (size == 1)
            rand = 0;
        else {

            Random random = new Random();
            rand = random.nextInt(size);

            while (lastRandomNum == rand)
                rand = random.nextInt(size);

            lastRandomNum = rand;

        }

        return rand;
    }

}
