package se.chalmers.agile.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;

import se.chalmers.agile.constants.Constants;
import se.chalmers.agile.R;
import se.chalmers.agile.fragments.BranchFragment;
import se.chalmers.agile.fragments.LastUpdatesFragment;
import se.chalmers.agile.fragments.NavigationDrawerFragment;
import se.chalmers.agile.fragments.NoteEditFragment;
import se.chalmers.agile.fragments.NotepadFragment;
import se.chalmers.agile.fragments.RepositoryFragment;
import se.chalmers.agile.fragments.SettingsFragment;
import se.chalmers.agile.timer.CountDownTimer;
import se.chalmers.agile.utils.AppPreferences;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RepositoryFragment.OnRepositoryFragmentInteractionListener,
        BranchFragment.OnBranchFragmentInteractionListener, NotepadFragment.OnNoteSelectedListener {

    RepositoryFragment repoFrag = null;
    AppPreferences appPreferences = null;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    //Countdowntimer
    private CountDownTimer countdownTimer;
    private NotepadFragment notepadFragment;
    private NoteEditFragment editor;

    //Handler to handle communication between threads
    final Handler handler = new Handler();

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

        notepadFragment = NotepadFragment.createInstance();
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
            case 0:
                if (repoFrag == null) {
                    Log.d("repoFrag", "created from fragmentManager");
                    repoFrag = RepositoryFragment.createInstance();
                }
                f = repoFrag;
                getActionBar().setSubtitle(getString(R.string.title_repos));
                break;
            case 1:
                f = BranchFragment.createInstance();
                getActionBar().setSubtitle(getString(R.string.title_branches));
                break;
            case 2:
                f = LastUpdatesFragment.createInstance();
                getActionBar().setSubtitle(getString(R.string.title_commits));
                break;
            case 3:
            //    if (notepadFragment==null)
            //    notepadFragment = NotepadFragment.createInstance();
                f = notepadFragment;
                getActionBar().setSubtitle(getString(R.string.title_notepad));
                break;
            case 4:
                f = SettingsFragment.createInstance();
                getActionBar().setSubtitle(getString(R.string.title_settings));
                break;
            default:
                if (repoFrag == null) {
                    Log.d("repoFrag", "created from fragmentManager");
                    repoFrag = RepositoryFragment.createInstance();
                }
                f = repoFrag;
                getActionBar().setSubtitle(getString(R.string.title_repos));
                break;
        }
        if (f!=null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, f);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.addToBackStack(null);
            transaction.commit();
        }
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
        switch(id){
            case(R.id.timerPause):
                countdownTimer.pauseTimer();
                break;
            case(R.id.timerStart):
                countdownTimer.resumeTimer();
                break;
            case(R.id.timerReset):
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
        appPreferences.appendBranch(repoName,branch.getName());
        //Switch to LastUpdatesFragment in NavigationDrawerFragment
        mNavigationDrawerFragment.selectItem(2);
    }

    @Override
    public void onNoteSelected(Uri noteUri) {
        editor = NoteEditFragment.createInstance();
        editor.fillData(noteUri);
    }
}
