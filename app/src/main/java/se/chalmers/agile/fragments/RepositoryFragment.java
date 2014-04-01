package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import se.chalmers.agile.activities.LoginActivity;
import se.chalmers.agile.fragments.dummy.DummyContent;

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

    private OnFragmentInteractionListener mListener;

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
            mListener = (OnFragmentInteractionListener) activity;
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

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


    private class RepositoryTask extends AsyncTask<Void, Void, Collection<GHRepository>> {
        public RepositoryTask() {
            super();
        }

        @Override
        protected Collection<GHRepository> doInBackground(Void... voids) {
            ArrayList<GHRepository> result = new ArrayList<GHRepository>();
            SharedPreferences sharedPref = getActivity().getApplication().getBaseContext().getSharedPreferences("Application",Context.MODE_PRIVATE);
            Log.d("preferencies", sharedPref.toString());
            String un = sharedPref.getString(LoginActivity.USERNAME_STR, LoginActivity.NOT_LOGGED_IN);
            String pwd = sharedPref.getString(LoginActivity.PASSWORD_STR, LoginActivity.NOT_LOGGED_IN);
            Log.d("user", un+" "+pwd);
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
        protected void onPostExecute(Collection<GHRepository> ghRepositories) {
            //TODO: change R.layout.simple_list_item
            setListAdapter(new ArrayAdapter<GHRepository>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, (ArrayList<GHRepository>) ghRepositories));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Collection<GHRepository> ghRepositories) {
            super.onCancelled(ghRepositories);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class RepositoryArrayAdapter extends ArrayAdapter<GHRepository>{

        public RepositoryArrayAdapter(Context context, int resource) {
            super(context, resource);
        }
    }

}
