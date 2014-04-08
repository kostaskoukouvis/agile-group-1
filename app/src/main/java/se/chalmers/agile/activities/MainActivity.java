package se.chalmers.agile.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
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
import se.chalmers.agile.fragments.RepositoryFragment;
import se.chalmers.agile.timer.CountDownTimer;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RepositoryFragment.OnRepositoryFragmentInteractionListener,
        BranchFragment.OnBranchFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    //Countdowntimer
    private CountDownTimer countdownTimer;

    //Handler to handle communication between threads
    final Handler handler = new Handler();

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment f = null;
        //TODO: if fragment already create avoid creating a new instance
        switch (position) {
            case 0:
                f = RepositoryFragment.createInstance();
                break;
            case 1:
                f = BranchFragment.createInstance();
                break;
            case 2:
                f = LastUpdatesFragment.createInstance();
                break;
            default:
                f = RepositoryFragment.createInstance();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar(Menu menu) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        //Get the countdown se.chalmers.agile.timer and start it if it is not already started
        countdownTimer = CountDownTimer.getInstance(Constants.TIMER_START_TIME, Constants.SECOND, handler);
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
            default:
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRepositoryInteraction(Repository repo) {
        SharedPreferences getRepoName = getApplication().getApplicationContext().getSharedPreferences("Application", Context.MODE_PRIVATE);
        String repoName = getRepoName.getString(RepositoryFragment.REPOSITORY_STR, "");
        if (repoName.equals(""))
            repoName = repo.generateId();
        else {
            String[] arr = repoName.split(RepositoryFragment.REPOSITORY_SEPARATOR);
            boolean itExists = false;
            /*for (String str : arr) {
                if (itExists) break;
                itExists = str.equals(repo.getName());
            }*/

            if (!itExists)
                repoName += RepositoryFragment.REPOSITORY_SEPARATOR + repo.generateId();
        }
        SharedPreferences.Editor editor = getRepoName.edit();
        editor.putString(RepositoryFragment.REPOSITORY_STR, repoName);
        editor.commit();

        //Switch to BranchFragment in NavigationDrawerFragment
        mNavigationDrawerFragment.selectItem(1);
    }

    @Override
    public void onBranchInteraction(String repoName, RepositoryBranch branch) {
        SharedPreferences getBranchName = getApplication().getApplicationContext().getSharedPreferences("Application", Context.MODE_PRIVATE);
        String branchName = getBranchName.getString(BranchFragment.BRANCH_STR, "");
        if (branchName.equals(""))
            branchName = branch.getName();
        else {
            String[] arr = branchName.split(BranchFragment.BRANCH_SEPARATOR);
            boolean itExists = false;


            if (!itExists)
                branchName += BranchFragment.BRANCH_SEPARATOR + branch.getName();
        }
        SharedPreferences.Editor editor = getBranchName.edit();
        editor.putString(BranchFragment.BRANCH_STR, branchName);
        editor.commit();

        Log.d("REPONAME", repoName + " " + branch.getName());

        //Switch to LastUpdatesFragment in NavigationDrawerFragment
        mNavigationDrawerFragment.selectItem(2);
    }
}
