package se.chalmers.agile.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;

import se.chalmers.agile.R;
import se.chalmers.agile.fragments.BranchFragment;
import se.chalmers.agile.fragments.RepositoryFragment;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RepositoryFragment.OnRepositoryFragmentInteractionListener,
        BranchFragment.OnBranchFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
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

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRepositoryInteraction(Repository repo) {
        SharedPreferences getRepoName = this.getPreferences(Context.MODE_PRIVATE);
        String repoName = getRepoName.getString(RepositoryFragment.REPOSITORY_STR, "");
        if (repoName.equals(""))
            repoName = repo.getName();
        else {
            String[] arr = repoName.split(RepositoryFragment.REPOSITORY_SEPARATOR);
            boolean itExists = false;
            /*for (String str : arr) {
                if (itExists) break;
                itExists = str.equals(repo.getName());
            }*/

            if (!itExists)
                repoName += RepositoryFragment.REPOSITORY_SEPARATOR + repo.getName();
        }
        SharedPreferences.Editor editor = getRepoName.edit();
        editor.putString(RepositoryFragment.REPOSITORY_STR, repoName);
        editor.commit();
    }

    @Override
    public void onBranchInteraction(String repoName, RepositoryBranch branch) {
        Log.d("REPONAME", repoName + " " + branch.getName());
    }
}
