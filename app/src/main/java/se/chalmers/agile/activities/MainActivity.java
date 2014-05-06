package se.chalmers.agile.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;

import se.chalmers.agile.R;
import se.chalmers.agile.constants.Constants;
import se.chalmers.agile.fragments.BranchFragment;
import se.chalmers.agile.fragments.LastUpdatesFragment;
import se.chalmers.agile.fragments.NavigationDrawerFragment;
import se.chalmers.agile.fragments.NotepadFragment;
import se.chalmers.agile.fragments.RepositoryFragment;
import se.chalmers.agile.fragments.SettingsFragment;
import se.chalmers.agile.receivers.NeedForUpdateReceiver;
import se.chalmers.agile.timer.CountDownTimer;
import se.chalmers.agile.utils.AppPreferences;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RepositoryFragment.OnRepositoryFragmentInteractionListener,
        BranchFragment.OnBranchFragmentInteractionListener {

    //Handler to handle communication between threads
    final Handler handler = new Handler();
    //Constants for the position of the positions of the items in the drawer menu
    private final int ENTRY_REPOSITORIES = 0;
    private final int ENTRY_BRANCHES = 1;
    private final int ENTRY_COMMITS = 2;
    private final int ENTRY_NOTEPAD = 3;
    private final int ENTRY_SETTINGS = 4;
    private final int ENTRY_LOGOUT = 5;
    RepositoryFragment repoFrag = null;
    AppPreferences appPreferences = null;
    BroadcastReceiver updateReceiver = new NeedForUpdateReceiver();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    //Countdowntimer
    private CountDownTimer countdownTimer;
    private NotepadFragment notepadFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        appPreferences = AppPreferences.getInstance();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment f = null;
        FragmentManager fragmentManager = getFragmentManager();
        //TODO: if fragment already create avoid creating a new instance
        switch (position) {
            case ENTRY_REPOSITORIES:
                if (repoFrag == null) {
                    Log.d("repoFrag", "created from fragmentManager");
                    repoFrag = RepositoryFragment.createInstance();
                }
                f = repoFrag;
                getActionBar().setSubtitle(getString(R.string.title_repos));
                break;
            case ENTRY_BRANCHES:
                f = BranchFragment.createInstance();
                getActionBar().setSubtitle(getString(R.string.title_branches));
                break;
            case ENTRY_COMMITS:
                f = LastUpdatesFragment.createInstance();
                getActionBar().setSubtitle(getString(R.string.title_commits));
                break;
            case ENTRY_NOTEPAD:
                if (notepadFragment == null)
                    notepadFragment = NotepadFragment.createInstance();
                f = notepadFragment;
                getActionBar().setSubtitle(getString(R.string.title_notepad));
                break;
            case ENTRY_SETTINGS:
                f = SettingsFragment.createInstance();
                getActionBar().setSubtitle(getString(R.string.title_settings));
                break;
            case ENTRY_LOGOUT:
                Log.d("Debug", appPreferences.logOut() + "");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            default:
                if (repoFrag == null) {
                    Log.d("repoFrag", "created from fragmentManager");
                    repoFrag = RepositoryFragment.createInstance();
                }
                f = repoFrag;
                getActionBar().setSubtitle(getString(R.string.title_repos));
                break;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void restoreActionBar(Menu menu) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        //Get the countdown se.chalmers.agile.timer and start it if it is not already started
        countdownTimer = CountDownTimer.getInstance(Constants.SECOND, handler, getApplicationContext());
        countdownTimer.startTimer(menu.findItem(R.id.timer_button));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar(menu);
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
        switch (id) {
            case (R.id.timerPause):
                countdownTimer.pauseTimer();
                break;
            case (R.id.timerStart):
                countdownTimer.resumeTimer();
                break;
            case (R.id.timerReset):
                countdownTimer.resetTimer();
                break;
            case R.id.insert:
                Intent i = new Intent(this, NoteEdit.class);
                startActivity(i);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRepositoryInteraction(Repository repo) {
        appPreferences.appendRepository(repo.generateId());
        //Switch to BranchFragment in NavigationDrawerFragment
        mNavigationDrawerFragment.selectItem(1);
    }

    @Override
    public void onBranchInteraction(String repoName, RepositoryBranch branch) {
        appPreferences.appendBranch(repoName, branch.getName());
        //Switch to LastUpdatesFragment in NavigationDrawerFragment
        mNavigationDrawerFragment.selectItem(2);
    }

    //Methods for handling the broadcaster receiver lifecycle
    public void registerReceiver() {
            this.registerReceiver(updateReceiver, new IntentFilter(
                    "android.intent.action.TIME_TICK"));
            //TODO: remove after testing that it works
            Toast.makeText(this, "Registered broadcast receiver", Toast.LENGTH_SHORT)
                    .show();
    }

    public void unregisterReceiver(){
        this.unregisterReceiver(updateReceiver);
        //TODO: remove after testing that it works
        Toast.makeText(this, "Unregistered broadcast receiver", Toast.LENGTH_SHORT)
                .show();
    }

}
