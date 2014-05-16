package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CommitService;

import java.io.IOException;
import java.util.Collection;

import se.chalmers.agile.R;


public class CommitFilesFragment extends ListFragment {

    private String repositoryName, branchName, sha;
    private static final String SHA = "sha_value_of_the_commit";


    public static Fragment newInstance(String repositoryName, String branchName, String sha) {
        Bundle bundle = new Bundle();
        bundle.putString("repositoryName", repositoryName);
        bundle.putString("branchName", branchName);
        bundle.putString(SHA, sha);
        CommitFilesFragment f = new CommitFilesFragment();
        f.setArguments(bundle);
        return f;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repositoryName = getArguments().getString("repositoryName");
        branchName = getArguments().getString("branchName");
        sha = getArguments().getString(SHA);

        // TODO: Change Adapter to display your content
        new FileFetcher().execute(repositoryName, branchName, sha);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.app_list_view, container, false);
        ListView mList = (ListView) ll.findViewById(android.R.id.list);
        return ll;
    }

    private class FileFetcher extends AsyncTask<String, Void, Collection<CommitFile>>{

        @Override
        protected Collection<CommitFile> doInBackground(String... args) {
            CommitService cs = new CommitService();

            cs.getClient().setOAuth2Token(getActivity().getString(R.string.api_key));

            Log.d("TEST",args[0]);
            Log.d("SHA", args[2]);
            String[] project = args[0].split("/");

            IRepositoryIdProvider repositoryId = RepositoryId.create(project[0], project[1]);

            Collection<CommitFile> files = null;

            try {
                files = cs.getCommit(repositoryId, sha).getFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return files;
        }


        @Override
        protected void onPostExecute(Collection<CommitFile> commitFiles) {
            super.onPostExecute(commitFiles);
            setListAdapter(new FilesAdapter(getActivity(),
                    android.R.id.list,
                    commitFiles.toArray(new CommitFile[commitFiles.size()])));
        }
    }




    private class FilesAdapter extends ArrayAdapter<CommitFile> {

        private CommitFile[] commitFiles = null;

        public FilesAdapter(Context context, int resource, CommitFile[] commitFiles) {
            super(context, resource);
            this.commitFiles = commitFiles;

        }

        @Override
        public int getCount() {
            return commitFiles.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final CommitFile file = commitFiles[position];
            if (row == null) {
                LayoutInflater inflater = ((Activity) getActivity()).getLayoutInflater();
                row = inflater.inflate(R.layout.files_row, parent, false);
            }

            TextView title = (TextView) row.findViewById(R.id.fileTitle);
            String s  = file.getFilename();
            title.setText(s.substring(s.lastIndexOf("/")+1));
            final View tmpRow = row;


            if(file.getPatch() != null && !file.getPatch().equals("")) {

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LinearLayout diffContainer = (LinearLayout) tmpRow.findViewById(R.id.diffContainer);
                        final TextView title = (TextView) tmpRow.findViewById(R.id.diffTitle);
                        final TextView body = (TextView) tmpRow.findViewById(R.id.diffBody);

                        if (title.getVisibility() == View.GONE) {
                            title.setVisibility(View.VISIBLE);
                            body.setText(file.getPatch());
                            body.setVisibility(View.VISIBLE);
                            body.setText("");
                            for (String str : file.getPatch().split("\n")) {
                                final int width = str.length();
                                SpannableString span = new SpannableString(str + "\n");
                                if (str.startsWith("+")) {
                                    span.setSpan(new BackgroundColorSpan(Color.GREEN), 0, width, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } else if (str.startsWith("-")) {
                                    span.setSpan(new BackgroundColorSpan(Color.RED), 0, width, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                body.append(span);
                            }

                        } else {
                            title.setVisibility(View.GONE);
                            body.setVisibility(View.GONE);
                        }

                    }
                });
            } else {
                row.setOnClickListener(null);
            }
            return row;
        }
    }









}
