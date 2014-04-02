package se.chalmers.agile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.LoginActivity;


public class BranchFragment extends ListFragment {

    private final static String REPO_STR = "repo";
    private String repositoryName;

    private OnBranchFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BranchFragment() {
    }

    public static BranchFragment createInstance(GHRepository repo){
        Bundle args = new Bundle();
        args.putString(REPO_STR, repo.getName());
        BranchFragment fragment = new BranchFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            repositoryName = getArguments().getString(REPO_STR);
        }
        Log.d("Rname", repositoryName);
        //Call the async task
        new BranchTask().execute();
        // TODO: Change Adapter to display your content
        /*setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));*/
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
        ArrayAdapter<GHBranch> adapter = (ArrayAdapter<GHBranch>)l.getAdapter();
        GHBranch selected = adapter.getItem(position);
        mListener.onBranchInteraction(selected);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBranchFragmentInteractionListener {
        public void onBranchInteraction(GHBranch branch);
    }



private class BranchTask extends AsyncTask<Void, Void, ArrayList<GHBranch>> {
    public BranchTask() {
        super();
    }

    @Override
    protected ArrayList<GHBranch> doInBackground(Void... voids) {
        ArrayList<GHBranch> result = new ArrayList<GHBranch>();
        SharedPreferences sharedPref = getActivity().getApplication().getBaseContext().getSharedPreferences("Application",Context.MODE_PRIVATE);

        String un = sharedPref.getString(LoginActivity.USERNAME_STR, LoginActivity.NOT_LOGGED_IN);
        String pwd = sharedPref.getString(LoginActivity.PASSWORD_STR, LoginActivity.NOT_LOGGED_IN);

        GitHub conn = null;

        try {
            conn = GitHub.connectUsingPassword(un, pwd);
            GHMyself myself = conn.getMyself();
            result.addAll(conn.getRepository(repositoryName).getBranches().values());
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
    protected void onPostExecute(ArrayList<GHBranch> ghBranches) {
        //TODO: change R.layout.simple_list_item
        setListAdapter(new BranchArrayAdapter(getActivity(), R.id.branchName, ghBranches));
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(ArrayList<GHBranch> ghBranches) {
        super.onCancelled(ghBranches);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
    }

private class BranchArrayAdapter extends ArrayAdapter<GHBranch>{

    private ArrayList<GHBranch> branches = null;
    private Context context = null;

    public BranchArrayAdapter(Context context, int resource, ArrayList<GHBranch> branches) {
        super(context, resource);
        this.context = context;
        this.branches = branches;
    }

    @Override
    public int getCount() {
        return branches.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rows_branch, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.branchName);
        textView.setText(branches.get(position).getName());
        return convertView;

    }

    @Override
    public GHBranch getItem(int position) {
        return branches.get(position);
    }
}

}