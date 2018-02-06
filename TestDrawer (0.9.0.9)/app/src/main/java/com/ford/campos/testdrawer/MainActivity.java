package com.ford.campos.testdrawer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * If anyone is reading this: refactoring MainActivity is going to take a
 * while, please bare with me.
 * <p>
 * First we need to refactor the class's methods (which will take forever), then after
 * that we need to break this class into multiple classes, which, again, will take forever :C
 * <p>
 * I appreciate your patience <3
 * <p>
 * Halfway through refactoring round 1: 2/5/18
 */

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private final String VERSION_NUMBER = "0.9.0.9";
    private final String TAG = "MainActivity";
    private final String NUJABES = "nujabes";
    private final String FUTURE_FUNK = "future-funk";
    private final String LIKED = "liked";
    private final int LIKED_POSITION = 2;

    private Save save = new Save(this);
    private MusicFinderFragment lastFragment;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private MusicHolder musicHolder = new MusicHolder();
    private int currPosition;

    // Eventually firstTimeAppOpened needs to go away. When the app first opens, it needs to
    // automatically load the content at position 1
    private boolean firstTimeAppOpened = true;
    private boolean isLikedAlbum = false;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavDrawer(getToolbar());

        addMusic(); // to musicHolder

    }

    private void addMusic() {
        musicHolder.add(MainActivityHelper.loadLinks(this, NUJABES));
        musicHolder.add(MainActivityHelper.loadLinks(this, FUTURE_FUNK));
        musicHolder.add(MainActivityHelper.loadLinks(this, LIKED));
        Log.d(TAG, "loaded " + musicHolder.get(NUJABES).size() + " url's from: " + NUJABES);
        Log.d(TAG, "loaded " + musicHolder.get(FUTURE_FUNK).size() + " url's from: " + FUTURE_FUNK);
        Log.d(TAG, "loaded " + musicHolder.get(LIKED).size() + " url's from: " + LIKED);
    }

    /**
     * Could probably be refactored more but I like it how it is right now; this
     * may change in the future.
     */
    private Toolbar getToolbar() {
        // Sets the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Set version number on toolbar
        getSupportActionBar().setSubtitle("Version: " + VERSION_NUMBER);
        toolbar.setSubtitleTextAppearance(this, R.style.MyTheme_TitleTextStyle);

        // title for toolbar
        mTitle = getTitle();

        return toolbar;
    }

    private void setupNavDrawer(Toolbar toolbar) {
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Log.d(TAG, "onNavDrawer, pos: " + position);
        // Closes the navigation drawer if you pick the item you're already in.
        // firstTimeAppOpened is there so the navigation drawer doesn't close when you
        // start the app for the first time
        if (currPosition == position && !firstTimeAppOpened) {
            Log.d(TAG, "onNavDrawer, currPosition == position && !firstTimeAppOpened");
            return;
        }

        // No liked albums; Closes the navigation drawer
        if (position == LIKED_POSITION && musicHolder.isLikedEmpty()) {
            Toast.makeText(this, "You have no liked albums", Toast.LENGTH_SHORT).show();
            return;                                                                                 // Returning closes the navigation drawer
        }

        if (position == LIKED_POSITION)
            updateContentView(position, true);
        else
            updateContentView(position, false);

        currPosition = position;

    }

    /**
     * Destroys the old webView when it updates to a new one, then loads a new url
     * from whatever genre specified.
     *
     * @param genrePosition
     * @param setIsLiked
     */
    private void updateContentView(int genrePosition, boolean setIsLiked) {

        if (firstTimeAppOpened)
            firstTimeAppOpened = false;

        killPrevFragment();
        setFragmentToGenre(genrePosition);
        isLikedAlbum = setIsLiked;
        springCleaning();

    }

    /**
     * Free up some RAM, also it might be a little redundant
     */
    private void killPrevFragment() {
        if (lastFragment == null)
            return;

        Log.d(TAG, "updateContentView lastFragment != null");
        lastFragment.kill();
        lastFragment.onDestroy();
    }

    private void setFragmentToGenre(int genreIndex) {
        switch (genreIndex) {
            case 0:
                lastFragment = MusicFinderFragment.newInstance(
                        NUJABES, genreIndex + 1);
                break;
            case 1:
                lastFragment = MusicFinderFragment.newInstance(
                        FUTURE_FUNK, genreIndex + 1);
                break;
            case 2:
                lastFragment = MusicFinderFragment.newInstance(
                        LIKED, genreIndex + 1);
                break;
        }
        // Update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, lastFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            if (isLikedAlbum)
                this.menu.findItem(R.id.heart).setIcon(R.drawable.heart_filled);

            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.next) {

            /**
             * Todo: further refactoring of this scope
             */

            // If there are no more liked albums: open the drawer and don't do anything;
            if (currPosition == LIKED_POSITION && musicHolder.isLikedEmpty()) {
                mNavigationDrawerFragment.openDrawer();
                return true;
            }

            handleNextAlbumButtonClick();

        } else if (id == R.id.heart) {
            handleHeartButtonClick();
        } else
            return super.onOptionsItemSelected(item);

        return true;

    }

    /**
     * Loads up the next album, suggests to the GC that we should garbage collect, then
     * reset the heart icon.
     */
    private void handleNextAlbumButtonClick() {
        loadNextAlbum();
        springCleaning();
        setHeartIcon();
    }

    private void loadNextAlbum() {
        // Clear the webview cache, history, etc, first to try to release some memory
        lastFragment.refresh();
        lastFragment.getAndLoadRandomAlbum(musicHolder.get(currPosition));
    }

    /**
     * Suggest to JVM that there's some garbage that needs to be collected. However, it's okay
     * if he doesn't listen to us.
     */
    private void springCleaning() {
        System.gc();
    }

    /**
     * Sets the heart icon to heart_empty when you hit next, as long as you're not in
     * the liked playlist.
     */
    private void setHeartIcon() {
        if (currPosition == LIKED_POSITION)
            updateHeartSettings(R.drawable.heart_filled, true);
        else
            updateHeartSettings(R.drawable.heart_empty, false);
    }

    private boolean updateHeartSettings(int heartId, boolean updateLikedTo) {
        menu.findItem(R.id.heart).setIcon(heartId);
        isLikedAlbum = updateLikedTo;
        return true;
    }

    /**
     * Updates the heart button to the correct icon/
     */
    private void handleHeartButtonClick() {
        if (isLikedAlbum)
            updateLiked(-1, LIKED_POSITION,
                    false, R.drawable.heart_empty);
        else
            updateLiked(LIKED_POSITION, currPosition,
                    true, R.drawable.heart_filled);
    }

    private void updateLiked(int addToGenre, int removeFromGenre, boolean updateLikedTo, int heartId) {

        String currUrl = lastFragment.getCurrentUrl();
        musicHolder.update(currUrl, addToGenre, removeFromGenre);
        updateHeartSettings(heartId, updateLikedTo);

        if (updateLikedTo)
            Toast.makeText(this, "Added to Liked Playlist", Toast.LENGTH_SHORT).show();
        else if (musicHolder.isLikedEmpty() && currPosition == LIKED_POSITION) {
            Toast.makeText(this, "There are no more liked albums", Toast.LENGTH_SHORT).show();
            mNavigationDrawerFragment.openDrawer();
        } else
            Toast.makeText(this, "Removed from Liked Playlist", Toast.LENGTH_SHORT).show();

        logInfoForUpdateLiked(updateLikedTo, currUrl);

    }

    /**
     * Saves the linksArray in MusicHolder if there is a change
     */
    @Override
    protected void onPause() {
        super.onPause();
        save.save(NUJABES, musicHolder.get(0), false);
        save.save(FUTURE_FUNK, musicHolder.get(1), false);
        save.save(LIKED, musicHolder.get(LIKED_POSITION), false);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    /**
     * Sets the title of the toolbar
     *
     * @param genreIndex the position of the current genre we are in, in the nav drawer
     */
    public void onSectionAttached(int genreIndex) {

        // the index we're passed is 1's based
        genreIndex -= 1;

        switch (genreIndex) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            default:
                mTitle = getString(R.string.app_name);
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    // MusicHolder getters and setters
    public ArrayList<String> getMusic(int genrePosition) {
        return musicHolder.get(genrePosition);
    }

    public void setOriginalSizes(int index, int size) {
        musicHolder.setOriginalSizes(index, size);
    }

    public int getOriginalSizes(int index) {
        return musicHolder.getOriginalSizes(index);
    }
    // end of MusicHolder getters & setters

    // Info for me. This'll be deleted along with all log statements
    private void logInfoForUpdateLiked(boolean updateBooleansTo, String currUrl) {

        if (updateBooleansTo)
            Log.d(TAG, "Added: " + currUrl);
        else
            Log.d(TAG, "Removed: " + currUrl);

        Log.d(TAG, "liked size: " + musicHolder.get(LIKED_POSITION).size());
        Log.d(TAG, "size of nujabes array: " + musicHolder.get(0).size());
        Log.d(TAG, "size of future funk array: " + musicHolder.get(1).size());

    }

}