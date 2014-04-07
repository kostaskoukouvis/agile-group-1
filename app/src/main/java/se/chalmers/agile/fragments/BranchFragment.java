package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.LoginActivity;


public class BranchFragment extends ListFragment {

    public final static String BRANCH_STR = "branch";
    private String repositoryName;

    public final static String BRANCH_SEPARATOR = "###";

    private OnBranchFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BranchFragment() {
    }

    /**
     * @return The Instance of the BranchFregment
     */
    public static BranchFragment createInstance() {
        BranchFragment fragment = new BranchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repositoryName = getRepoFromPreferences();

        if (getArguments() != null) {
            repositoryName = getArguments().getString(BRANCH_STR);
        }
        //Call the async task
        new BranchTask().execute();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBranchFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ArrayAdapter<RepositoryBranch> adapter = (ArrayAdapter<RepositoryBranch>) l.getAdapter();
        RepositoryBranch selected = adapter.getItem(position);
        mListener.onBranchInteraction(repositoryName,selected);
    }

    public void updateBranch(String repositoryName) {
        this.repositoryName = repositoryName;
        setListAdapter(null);
        new BranchTask().execute();

    }

    private String getRepoFromPreferences(){
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        String str = sp.getString(RepositoryFragment.REPOSITORY_STR, "");
        String [] arr = str.split(RepositoryFragment.REPOSITORY_SEPARATOR);
        Log.d("RepoFtched",arr[arr.length - 1] );
        return arr[arr.length - 1];
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBranchFragmentInteractionListener {
        public void onBranchInteraction(String repoName, RepositoryBranch branch);
    }

    /**
     * Asynch task to getting the branches
     */
    private class BranchTask extends AsyncTask<Void, Void, List<RepositoryBranch>> {
        public BranchTask() {
            super();
        }

        @Override
        protected List<RepositoryBranch> doInBackground(Void... voids) {
            List<RepositoryBranch> result = new ArrayList<RepositoryBranch>();
            SharedPreferences sharedPref = getActivity().getApplication().getBaseContext().getSharedPreferences("Application", Context.MODE_PRIVATE);

            String un = sharedPref.getString(LoginActivity.USERNAME_STR, LoginActivity.NOT_LOGGED_IN);
            String pwd = sharedPref.getString(LoginActivity.PASSWORD_STR, LoginActivity.NOT_LOGGED_IN);

            RepositoryService rs = new RepositoryService();
            rs.getClient().setCredentials(un, pwd);
            IRepositoryIdProvider repositoryId = RepositoryId.create(un, repositoryName);
            Log.d("BRANCHTASK", repositoryId.generateId());

            try {
                result = rs.getBranches(repositoryId);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
                this.cancel(true);
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<RepositoryBranch> branches) {
            setListAdapter(new BranchArrayAdapter(getActivity(), R.id.branchName, branches));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(List<RepositoryBranch> branches) {
            super.onCancelled(branches);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class BranchArrayAdapter extends ArrayAdapter<RepositoryBranch> {

        private List<RepositoryBranch> branches = null;
        private Context context = null;

        public BranchArrayAdapter(Context context, int resource, List<RepositoryBranch> branches) {
            super(context, resource);
            this.context = context;
            this.branches = branches;
        }

        @Override
        public int getCount() {
            Log.d("GETCOUNT", branches.size() + "");
            return branches.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.rows_branch, null);
            }
            Log.d("Branch", branches.get(position).getName());
            TextView textView = (TextView) convertView.findViewById(R.id.branchName);
            textView.setText(branches.get(position).getName());
            return convertView;

        }

        @Override
        public RepositoryBranch getItem(int position) {
            return branches.get(position);
        }
    }



}