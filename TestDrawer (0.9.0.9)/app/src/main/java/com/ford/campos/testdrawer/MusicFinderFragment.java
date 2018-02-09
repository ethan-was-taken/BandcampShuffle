package com.ford.campos.testdrawer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Round 1 refactoring: 2/7/18
 */

public class MusicFinderFragment extends Fragment {

    private static final String TAG = "MusicFinderFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LAST_URL = "url";
    private static final int FRAME_COUNT = 120;
    private static final int ROTATION_DURATION = 1000;

    private LoadingScreen loadingScreen;
    private boolean firstPass = true;
    private WebView webView;
    private String currentUrl;

    private int lastRandomNum = -1;

    public MusicFinderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MusicFinderFragment newInstance(int sectionNumber) {
        MusicFinderFragment fragment = new MusicFinderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = getRootView(inflater, container);
        setLoadingScreen(rootView);
        setWebView(rootView);
        getAndLoadRandomAlbum();

        return rootView;
    }

    private View getRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_music_finder, container, false);
    }

    private void setLoadingScreen(View rootView) {
        ImageView loadingImageView = (ImageView) rootView.findViewById(R.id.loading);
        loadingScreen = new LoadingScreen(
                getActivity(),
                loadingImageView,
                R.drawable.spinner_small,
                FRAME_COUNT,
                ROTATION_DURATION,
                R.anim.progress_anim
        );
    }

    private void setWebView(View rootView) {
        webView = new WebView(getActivity().getApplicationContext());
        webView = (WebView) rootView.findViewById(R.id.internet_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    /**
     * A basic wrapper method for getAndLoadRandomAlbum(ArrayList<String> linksArray), so we don't have to
     * muddy up onCreateView()
     */
    private void getAndLoadRandomAlbum() {
        int genrePosition = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
        ArrayList<String> linksArray = ((MainActivity) getActivity()).getMusic(genrePosition);
        getAndLoadRandomAlbum(linksArray);
    }

    public void getAndLoadRandomAlbum(ArrayList<String> linksArray) {

        if (linksArray.size() == 0)
            return;

        handleAnimation();

        setCurrentUrl(linksArray);

        if (isInSingularLiked())
            return;

        webView.loadUrl(currentUrl.substring(0, currentUrl.length() - 1));

    }

    private void handleAnimation() {
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
    }

    private void setCurrentUrl(ArrayList<String> linksArray) {
        int index = getRandomNumber(linksArray.size());
        currentUrl = linksArray.get(index);
    }

    private void startAnimation() {

        // We don't need to restart the loading screen every time this is called
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

        if (size == 1)
            return 0;

        Random random = new Random();
        int rand = random.nextInt(size);
        while (lastRandomNum == rand)
            rand = random.nextInt(size);
        lastRandomNum = rand;

        return rand;

    }

    /**
     * @return true if we're in the liked playlist and there's only one liked album
     */
    private boolean isInSingularLiked() {
        return currentUrl.substring(0, currentUrl.length() - 1).equals(webView.getUrl());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).setToolbarTitle(getArguments().getInt(ARG_SECTION_NUMBER));
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

}
