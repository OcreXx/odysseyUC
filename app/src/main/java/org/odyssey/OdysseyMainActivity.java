package org.odyssey;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.odyssey.fragments.AlbumTracksFragment;
import org.odyssey.fragments.ArtistAlbumsFragment;
import org.odyssey.fragments.MyMusicFragment;
import org.odyssey.fragments.SavedPlaylistsFragment;
import org.odyssey.fragments.SettingsFragment;
import org.odyssey.listener.OnAlbumSelectedListener;
import org.odyssey.listener.OnArtistSelectedListener;
import org.odyssey.views.NowPlayingView;

public class OdysseyMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnArtistSelectedListener, OnAlbumSelectedListener {

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odyssey_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null) {
                return;
            }

            Fragment myMusicFragment = new MyMusicFragment();

            myMusicFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, myMusicFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        NowPlayingView nowPlayingView = (NowPlayingView) findViewById(R.id.now_playing_layout);

        nowPlayingView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        NowPlayingView nowPlayingView = (NowPlayingView) findViewById(R.id.now_playing_layout);

        nowPlayingView.onPause();
    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            // enable navigation bar when backstack empty
            if (fragmentManager.getBackStackEntryCount() == 0) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.odyssey_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case android.R.id.home:
                if(fragmentManager.getBackStackEntryCount() > 0) {
                    onBackPressed();
                } else {
                    // back stack empty so enable navigation drawer

                    mDrawerToggle.setDrawerIndicatorEnabled(true);

                    if(mDrawerToggle.onOptionsItemSelected(item)) {
                        return true;
                    }
                }
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // clear backstack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment fragment = null;

        if (id == R.id.nav_my_music) {
            fragment = new MyMusicFragment();
        } else if (id == R.id.nav_saved_playlists) {
            fragment = new SavedPlaylistsFragment();
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        return true;
    }

    @Override
    public void onArtistSelected(String artist, long artistID) {
        // Create fragment and give it an argument for the selected article
        ArtistAlbumsFragment newFragment = new ArtistAlbumsFragment();
        Bundle args = new Bundle();
        args.putString(ArtistAlbumsFragment.ARG_ARTISTNAME, artist);
        args.putLong(ArtistAlbumsFragment.ARG_ARTISTID, artistID);

        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("ArtistFragment");

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onAlbumSelected(String albumKey, String albumTitle, String albumArtURL, String artistName) {
        // Create fragment and give it an argument for the selected article
        AlbumTracksFragment newFragment = new AlbumTracksFragment();
        Bundle args = new Bundle();
        args.putString(AlbumTracksFragment.ARG_ALBUMKEY, albumKey);
        args.putString(AlbumTracksFragment.ARG_ALBUMTITLE, albumTitle);
        args.putString(AlbumTracksFragment.ARG_ALBUMART, albumArtURL);
        args.putString(AlbumTracksFragment.ARG_ALBUMARTIST, artistName);

        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("AlbumTracksFragment");

        // Commit the transaction
        transaction.commit();
    }

    public void setUpToolbar(String title, boolean scrollingEnabled, boolean drawerIndicatorEnabled) {

        // set drawer state
        mDrawerToggle.setDrawerIndicatorEnabled(drawerIndicatorEnabled);

        // set title
        setTitle(title);

        // set scrolling behaviour
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        if (scrollingEnabled) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            AppBarLayout layout = (AppBarLayout) findViewById(R.id.appbar);
            layout.setExpanded(true, false);
            params.setScrollFlags(0);
        }
    }
}
