package se.chalmers.agile.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import constants.Constants;
import se.chalmers.agile.R;
import se.chalmers.agile.fragments.BranchFragment;
import se.chalmers.agile.fragments.LastUpdatesFragment;
import se.chalmers.agile.fragments.RepositoryFragment;
import timer.CountDownTimer;


public class ContainerActivity extends Activity implements ActionBar.TabListener, RepositoryFragment.OnRepositoryFragmentInteractionListener, BranchFragment.OnBranchFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    ActionBar actionBar;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private CountDownTimer countdownTimer;
    final Handler handler = new Handler();

    private boolean isRepoChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        // Set up the action bar.
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        List<Fragment> tmp = new ArrayList<Fragment>();
        tmp.add(RepositoryFragment.createInstance());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), tmp);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.container, menu);
        getMenuInflater().inflate(R.menu.timer, menu);

        //Get the countdown timer and start it
        countdownTimer = CountDownTimer.getInstance(Constants.TIMER_START_TIME, Constants.SECOND, handler);
        countdownTimer.startTimer(menu.findItem(R.id.timer_button));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.timerPause) {
            countdownTimer.pauseTimer();
        } else if (id == R.id.timerStart) {
            countdownTimer.resumeTimer();
        } else if (id == R.id.timerReset) {
            countdownTimer.resetTimer();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * The method called when a repository is clicked in the repository fragment
     *
     * @param repo The repository clicked
     */
    @Override
    public void onRepositoryInteraction(Repository repo) {

        BranchFragment bf = null;
        try {
            bf = (BranchFragment) mSectionsPagerAdapter.getItem(1);
        } catch (IndexOutOfBoundsException e) {
            bf = BranchFragment.createInstance(repo);
            mSectionsPagerAdapter.addFragment(bf, 1, "branches");
            return;
        } catch (ClassCastException e) {
            bf = BranchFragment.createInstance(repo);
            mSectionsPagerAdapter.addFragment(bf, 1, "branches");
            return;
        }

        bf.updateBranch(repo.getName());
        mViewPager.setCurrentItem(1);

    }

    /**
     * The method called when the a branch is called
     *
     * @param branch
     */
    @Override
    public void onBranchInteraction(String repoName, RepositoryBranch branch) {

        SharedPreferences sharedPref = getApplication().getBaseContext().getSharedPreferences("Application", Context.MODE_PRIVATE);

        String un = sharedPref.getString(LoginActivity.USERNAME_STR, LoginActivity.NOT_LOGGED_IN);

        Fragment luf = (Fragment) LastUpdatesFragment.createInstance(un + "/" + repoName, branch.getName());
        mSectionsPagerAdapter.addFragment(luf, 2, "Last commits");

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        private ActionBar.Tab branchTab;
        private ActionBar.Tab commitsTab;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * Add a fragment to the Page viewer
         *
         * @param f        the fragment to be added
         * @param position the position in the 0-indexed tab list (it moves the other tab on the right)
         * @param tabName  the title of the tab
         */
        public void addFragment(Fragment f, int position, String tabName) {
            if (f instanceof BranchFragment) {
                if (branchTab == null) {
                    branchTab = actionBar.newTab()
                            .setText(tabName)
                            .setTabListener(ContainerActivity.this);
                    actionBar.addTab(branchTab);
                }
                if (fragments.size() > 1 && fragments.get(position) instanceof BranchFragment) {
                    Fragment tmp = fragments.remove(position);
                    ContainerActivity.this.getFragmentManager()
                            .beginTransaction()
                            .remove(tmp)
                            .commit();
                }
                fragments.add(position, f);
                notifyDataSetChanged();
                actionBar.selectTab(branchTab);
                return;
            }
            if (f instanceof LastUpdatesFragment) {
                if (commitsTab == null) {
                    commitsTab = actionBar.newTab()
                            .setText(tabName)
                            .setTabListener(ContainerActivity.this);
                    actionBar.addTab(commitsTab);
                }
                if (fragments.size() > 2 && fragments.get(position) instanceof LastUpdatesFragment) {
                    Fragment tmp = fragments.remove(position);
                    ContainerActivity.this.getFragmentManager()
                            .beginTransaction()
                            .remove(tmp)
                            .commit();
                }
                fragments.add(position, f);
                notifyDataSetChanged();
                actionBar.selectTab(commitsTab);
                return;
            }


        }

        @Override
        //TODO change names for fragments
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}

