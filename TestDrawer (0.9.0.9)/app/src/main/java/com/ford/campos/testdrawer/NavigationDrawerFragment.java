package com.ford.campos.testdrawer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 *
 * Round 1 refactoring: 2/18/18
 *
 * Note: this class is a mess and will need to be broken up; just need to figure out how to do
 * that...
 *
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final int LIKED_POSITION = 2;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private String TAG = "NavDrawer";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */

    private RecyclerView mDrawerRecyclerView;
    private RecyclerViewArrayAdapter adapter;

    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        checkIfUserLearnedDrawer();
        handleSavedInstance(savedInstanceState);

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition, "onCreate");

    }

    /**
     * Read in the flag indicating whether or not the user has demonstrated awareness of the
     * drawer. See PREF_USER_LEARNED_DRAWER for details.
     */
    private void checkIfUserLearnedDrawer() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    private void handleSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        mFromSavedInstanceState = true;
    }

    private void selectItem(int position, String from) {
        mCurrentSelectedPosition = position;
        setToolbarTitle();
        closeDrawer();
        startNewGenreFragment(position);
    }

    private void setToolbarTitle() {
        if (mDrawerRecyclerView != null)
            ((MainActivity) getActivity()).setToolbarTitle(mCurrentSelectedPosition);
    }

    public void openDrawer() {
        if (Drawer != null)
            Drawer.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        if (Drawer != null)
            Drawer.closeDrawer(mFragmentContainerView);
    }

    private void startNewGenreFragment(int position) {
        if (mCallbacks != null)
            mCallbacks.onNavigationDrawerItemSelected(position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    /**
     * Set up the Recycler View in the Navigation Drawer.
     *
     * @return the recycler view layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {

        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container,false);

        mDrawerRecyclerView = (RecyclerView)
                layout.findViewById(R.id.recyclerview_navigation_drawer);

        ArrayList<Information> sectionInformation =
                NavigationDrawerSections.getSectionInformation(getActivity());
        adapter = new RecyclerViewArrayAdapter(getActivity(), sectionInformation);

        mDrawerRecyclerView.setAdapter(adapter);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public boolean isDrawerOpen() {
        return Drawer != null && Drawer.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Todo: Panic... but also refactor this after I've talked to the wicked witch of the west.
     *
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        Drawer = drawerLayout;

        setupRecyclerView();
        setupLayoutManager();
        setupDrawerToggle(toolbar);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            Drawer.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        Drawer.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        // Drawer Listener set to the Drawer toggle
        Drawer.setDrawerListener(mDrawerToggle);

    }

    private void setupRecyclerView() {

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview_navigation_drawer);
        mRecyclerView.setHasFixedSize(true);

        ArrayList<Information> sectionInformation =
                NavigationDrawerSections.getSectionInformation(getActivity());
        adapter = new RecyclerViewArrayAdapter(getActivity(), sectionInformation);

        mRecyclerView.setAdapter(adapter);

        setupRecyclerViewFunctionality();
    }

    private void setupRecyclerViewFunctionality() {

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                    //note: recyclerView.getChildPosition(child) is 0 based
                    mCurrentSelectedPosition = recyclerView.getChildPosition(child);

                    setItemChecked(mCurrentSelectedPosition, true);
                    Drawer.closeDrawers();

                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) { }

        });

    }

    private void setupLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupDrawerToggle(final Toolbar toolbar) {
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), Drawer, toolbar,
                R.string .navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                if (!isAdded())
                    return;
                getActivity().supportInvalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                if (!isAdded())
                    return;

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();

                //Keeps the hamburger icon when the drawer is opened
                super.onDrawerSlide(drawerView, 0);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > .5)
                    onDrawerOpened(drawerView);
                else
                    onDrawerClosed(drawerView);
            }

        };
    }

    public void setItemChecked(int position, boolean callNavDrawerItemSelected) {

        Log.d(TAG, "setItemChecked, pos: " + position);
        mCurrentSelectedPosition = position;

        boolean isLikedEmpty = ((MainActivity) getActivity()).getMusic(LIKED_POSITION).size() == 0;

        if (!(position == LIKED_POSITION && isLikedEmpty)) {
            Log.d(TAG, "setItemChecked, !(position == 2 && isLikedEmpty)");
            adapter.setItemToActive(position);
        }

        if (callNavDrawerItemSelected)
            ((MainActivity) getActivity()).onNavigationDrawerItemSelected(mCurrentSelectedPosition);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (Drawer != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

    }

}
