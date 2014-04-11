package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.agile.R;
import se.chalmers.agile.utils.AppPreferences;

/**
 */
public class RepositoryFragment extends ListFragment {

    private RepositoryTask repoTask = null;
    private RepositoryArrayAdapter repoAdapter = null;


    private OnRepositoryFragmentInteractionListener mListener;
    private ListView mList = null;

    private List<Repository> repositoryCollection;

    private static final String TAG = "RepositoryFragment";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepositoryFragment() {
    }

    // TODO: Rename and change types of parameters
    public static RepositoryFragment createInstance() {
        RepositoryFragment fragment = new RepositoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (repoTask == null)
            repoTask = new RepositoryTask();

        //Flag for saving the state when rotating the screen
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (repositoryCollection == null)
            repoTask.execute();
        else setListAdapter(new RepositoryArrayAdapter(getActivity(), R.id.repoName));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRepositoryFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.app_list_view, container, false);
        mList = (ListView) ll.findViewById(android.R.id.list);
        return ll;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ArrayAdapter<Repository> adapter = (ArrayAdapter<Repository>) l.getAdapter();
        Repository selected = adapter.getItem(position);
        mListener.onRepositoryInteraction(selected);
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
    public interface OnRepositoryFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onRepositoryInteraction(Repository repo);
    }

    /**
     * Asynch task to fetch the repositories
     */
    private class RepositoryTask extends AsyncTask<Void, Void, List<Repository>> {
        public RepositoryTask() {
            super();
        }

        @Override
        protected List<Repository> doInBackground(Void... voids) {
            Log.d(TAG, "fetching repository from gitHub");
            List<Repository> result = new ArrayList<Repository>();
            AppPreferences appPreferences = AppPreferences.getInstance();

            String un = appPreferences.getUser();
            String pwd = appPreferences.getPassword();

            RepositoryService service = new RepositoryService();
            service.getClient().setCredentials(un, pwd);
            try {
                result = service.getRepositories();
                Log.d("REPOSIZE", result.size() + "");
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
        protected void onPostExecute(List<Repository> repositories) {
            //TODO: change R.layout.simple_list_item
            if (repositoryCollection == null) {
                repositoryCollection = repositories;
                repoAdapter = new RepositoryArrayAdapter(getActivity(), R.id.repoName);
            }
            setListAdapter(repoAdapter);
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            setListAdapter(repoAdapter);
        }

        @Override
        protected void onCancelled(List<Repository> repositories) {
            super.onCancelled(repositories);
        }
    }

    /**
     * Custom adapter for the repository list
     */
    private class RepositoryArrayAdapter extends ArrayAdapter<Repository> {


        private Context context = null;

        public RepositoryArrayAdapter(Context context, int resource) {
            super(context, resource);
            this.context = context;

        }

        @Override
        public int getCount() {
            return repositoryCollection.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.rows_repository, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.repoName);
            textView.setText(repositoryCollection.get(position).getName());
            return convertView;

        }


        @Override
        public Repository getItem(int position) {
            return repositoryCollection.get(position);
        }
    }

}
