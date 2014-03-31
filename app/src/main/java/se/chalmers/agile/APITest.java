package se.chalmers.agile;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;

/**
 * Created by Marcello on 31/03/2014.
 */
public class APITest extends AsyncTask <Void, Void, String> {

    private EditText textArea;

    public APITest(EditText textArea) {
        super();
        this.textArea = textArea;
    }

    @Override
    protected String doInBackground(Void... strings) {

        Log.d("API", "Start doInBackground");
        RepositoryService service = new RepositoryService();
        StringBuffer buf = new StringBuffer();
        try {
            for (Repository repo : service.getRepositories("marcyb5st")) {
                Log.d("API", repo.toString());
                buf.append(repo.getName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        textArea.setText(s);
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
