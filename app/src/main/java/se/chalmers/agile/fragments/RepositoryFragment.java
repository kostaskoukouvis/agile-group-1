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

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.LoginActivity;

/**
 */
public class RepositoryFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnRepositoryFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepositoryFragment() {
    }

    // TODO: Rename and change types of parameters
    public static RepositoryFragment createInstance() {
        RepositoryFragment fragment = new RepositoryFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments();*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Call the async task
        new RepositoryTask().execute();
        // TODO: Change Adapter to display your content

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ArrayAdapter<GHRepository> adapter = (ArrayAdapter<GHRepository>)l.getAdapter();
        GHRepository selected = adapter.getItem(position);
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
        public void onRepositoryInteraction(GHRepository repo);
    }


    private class RepositoryTask extends AsyncTask<Void, Void, ArrayList<GHRepository>> {
        public RepositoryTask() {
            super();
        }

        @Override
        protected ArrayList<GHRepository> doInBackground(Void... voids) {
            ArrayList<GHRepository> result = new ArrayList<GHRepository>();
            SharedPreferences sharedPref = getActivity().getApplication().getBaseContext().getSharedPreferences("Application",Context.MODE_PRIVATE);

            String un = sharedPref.getString(LoginActivity.USERNAME_STR, LoginActivity.NOT_LOGGED_IN);
            String pwd = sharedPref.getString(LoginActivity.PASSWORD_STR, LoginActivity.NOT_LOGGED_IN);

            GitHub conn = null;
            try {
                conn = GitHub.connectUsingPassword(un, pwd);
                GHMyself myself = conn.getMyself();
                result.addAll(myself.getAllRepositories().values());
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
        protected void onPostExecute(ArrayList<GHRepository> ghRepositories) {
            //TODO: change R.layout.simple_list_item
            setListAdapter(new RepositoryArrayAdapter(getActivity(), R.id.repoName, ghRepositories));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(ArrayList<GHRepository> ghRepositories) {
            super.onCancelled(ghRepositories);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class RepositoryArrayAdapter extends ArrayAdapter<GHRepository>{

        private ArrayList<GHRepository> repos = null;
        private Context context = null;

        public RepositoryArrayAdapter(Context context, int resource, ArrayList<GHRepository> repos) {
            super(context, resource);
            this.context = context;
            this.repos = repos;
        }

        @Override
        public int getCount() {
            return repos.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.rows_repository, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.repoName);
            textView.setText(repos.get(position).getName());
            return convertView;

        }

        @Override
        public GHRepository getItem(int position) {
            return repos.get(position);
        }
    }

}
