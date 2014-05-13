package se.chalmers.agile.activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.OAuthService;

import java.io.IOException;
import java.util.Date;

import se.chalmers.agile.R;
import se.chalmers.agile.receivers.NeedForUpdateReceiver;
import se.chalmers.agile.utils.AppPreferences;

//TODO
// 1. Loading screen while checking the credentials.
// 2. Store the credentials.

/**
 * Defines the behaviour of the login screen activity.
 */
public class LoginActivity extends ActionBarActivity {

    private static String TAG = "LoginActivity";
    public static String USERNAME_STR = "Username";
    public static String PASSWORD_STR = "Password";
    public static String NOT_LOGGED_IN = "notSet";


    private EditText userName;
    private EditText password;

    private AppPreferences prefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);


        prefs = AppPreferences.getInstance();

        String user = prefs.getUser();
        String pwd = prefs.getPassword();

        callReceiver();
        if (!user.isEmpty() && !pwd.isEmpty()) {
            startContainerActivity();
        }

        userName = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);

    }

    private void callReceiver() {
        Log.d(TAG, "Calling receiver");
        Intent intent = new Intent();
        intent.setAction(NeedForUpdateReceiver.ACTION);
        intent.getAction();
        sendBroadcast(intent);

    }


    /**
     * Action to be done after pressing the login button.
     */
    public void tryToLogIn(View v) {
        if (correctInput()) {
            new LoginTask().execute(userName.getText().toString(), password.getText().toString());
        } else {
            Toast.makeText(this, R.string.empty_input, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks that the user introduced something.
     *
     * @return <code>true</code> if it is OK.
     */
    private boolean correctInput() {
        return !userName.getText().toString().isEmpty() && !password.getText().toString().isEmpty();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startContainerActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Async task to check credentials.
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Context context = LoginActivity.this;
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(LoginActivity.this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... args) {
            OAuthService os = new OAuthService();
            os.getClient().setCredentials(args[0], args[1]);
            try {
                os.getAuthorizations();
                return true;
            } catch (RequestException e) {
                return false;
            } catch (IOException exp) {
                this.cancel(true);
            }
            return false;
        }

        /**
         * Connection error handling.
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        /**
         * Processes the answer.
         *
         * @param success Whether the credentials were OK or not.
         */
        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                storeCredentials();
                LoginActivity.this.startContainerActivity();
            } else {
                Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        }

        private void storeCredentials() {
            prefs.setUser(userName.getText().toString());
            prefs.setPassword(password.getText().toString());
        }
    }
}
