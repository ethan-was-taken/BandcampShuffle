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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
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
    private RecyclerView mRecyclerView;                                                             // Declaring RecyclerView
    private RecyclerView.LayoutManager mLayoutManager;                                              // Declaring Layout Manager as a linear layout manager
    private DrawerLayout Drawer;                                                                    // Declaring DrawerLayout
    private ActionBarDrawerToggle mDrawerToggle;                                                    // Declaring Action Bar Drawer Toggle
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

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition, "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        /*
        ArrayList<Information> sectionInformation =
                NavigationDrawerSections.getSectionInformation( getActivity() );
        adapter = new RecyclerViewArrayAdapter(getActivity(), sectionInformation);                  // Might want to move this to onActivityCreated()
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mDrawerRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview_navigation_drawer);

        ArrayList<Information> sectionInformation =
                NavigationDrawerSections.getSectionInformation(getActivity());
        adapter = new RecyclerViewArrayAdapter(getActivity(), sectionInformation);                  // Might want to move this to onActivityCreated()

        mDrawerRecyclerView.setAdapter(adapter);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public boolean isDrawerOpen() {
        return Drawer != null && Drawer.isDrawerOpen(mFragmentContainerView);
    }

    // I'm not proud of this method, but it works and I hate recycler view because I don't
    // understand it
    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);

        Drawer = drawerLayout;

        // Assigning the RecyclerView Object to the xml View
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview_navigation_drawer);
        mRecyclerView.setHasFixedSize(true);                                                        // Letting the system know that the list objects are of fixed size

        ArrayList<Information> sectionInformation =
                NavigationDrawerSections.getSectionInformation(getActivity());
        adapter = new RecyclerViewArrayAdapter(getActivity(), sectionInformation);
        //adapter = new RecyclerViewArrayAdapter(getActivity(), getData());                           // Creating the Adapter of MyAdapter class
        mRecyclerView.setAdapter(adapter);                                                          // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
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
                    /**
                     * Also Important:      recyclerView.getChildPosition(child) is 0 based
                     */
                    mCurrentSelectedPosition = recyclerView.getChildPosition(child);

                    setItemChecked(mCurrentSelectedPosition, true);
                    Drawer.closeDrawers();

                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
        });


        mLayoutManager = new LinearLayoutManager(getActivity());                                  // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                                             // Setting the layout Manager

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                Drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {

            @Override
            public void onDrawerClosed(View drawerView) {

                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                if (!isAdded()) {
                    return;
                }


                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                super.onDrawerSlide(drawerView, 0); //Keeps the hamburger icon when the drawer is opened
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //super.onDrawerSlide(drawerView, slideOffset); // 0 disables hamburger to arrow ANIMATION
                //getActivity().menu
                Log.d(TAG, "slideOffset: " + slideOffset);

                if (slideOffset > .5) {
                    //toolbar.setScaleX(1 - slideOffset);
                    //toolbar.setScaleY(1 - slideOffset);

                    onDrawerOpened(drawerView);
                } else {
                    //toolbar.setScaleX(1- slideOffset);
                    //toolbar.setScaleY(1 - slideOffset);
                    onDrawerClosed(drawerView);
                }
            }

        }; // Drawer Toggle Object Made

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

    private void selectItem(int position, String from) {
        Log.d(TAG, "selectItem, position: " + position + "from " + from);
        mCurrentSelectedPosition = position;

        if (mDrawerRecyclerView != null) {
            ((MainActivity) getActivity()).onSectionAttached(mCurrentSelectedPosition);
        }
        if (Drawer != null) {
            Drawer.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }

    }

    public void setItemChecked(int position, boolean callNavDrawerItemSelected) {
        Log.d(TAG, "setItemChecked, pos: " + position);
        mCurrentSelectedPosition = position;
        boolean isLikedEmpty = ((MainActivity) getActivity()).getMusic(LIKED_POSITION).size() == 0;

        if (!(position == LIKED_POSITION && isLikedEmpty)) {
            Log.d(TAG, "setItemChecked, !(position == 2 && isLikedEmpty)");
            adapter.setItemToActive(position);
        } else
            Log.d(TAG, "setItemChecked, position == 2 && isLikedEmpty");

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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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

    public void openDrawer() {
        Drawer.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {

        //if( isDrawerOpen( ) )

        Drawer.closeDrawer(mFragmentContainerView);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
