package se.chalmers.agile.activities;


import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;


import org.kohsuke.github.GitHub;

import java.io.IOException;

import se.chalmers.agile.R;

//TODO s
// 1. Loading screen while checking the credentials.
// 2. Store the credentials.

/**
 * Defines the behaviour of the login screen activity.
 */
public class LoginActivity extends ActionBarActivity {

    public String USERNAME_STR = "Username";
    public String PASSWORD_STR = "Password";
    private String NOT_LOGGED_IN = "notSet";


    private EditText userName;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String un = sharedPref.getString(USERNAME_STR, NOT_LOGGED_IN);
        String pwd = sharedPref.getString(PASSWORD_STR, NOT_LOGGED_IN);
        
        if (!un.equals(NOT_LOGGED_IN) && !pwd.equals(NOT_LOGGED_IN)) {
            startContainerActivity();
        } else {
            userName = (EditText) findViewById(R.id.usernameText);
            password = (EditText) findViewById(R.id.passwordText);
        }
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

    protected void startContainerActivity(){
        Intent intent = new Intent(LoginActivity.this, ContainerActivity.class);
        startActivity(intent);
    }

    /**
     * Async task to check credentials.
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                return GitHub.connectUsingPassword(args[0], args[1]).isCredentialValid();
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
            Toast.makeText(LoginActivity.this, R.string.connection_problem, Toast.LENGTH_SHORT).show();
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
            //TODO
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(USERNAME_STR, userName.getText().toString());
            editor.putString(PASSWORD_STR, password.getText().toString());
            editor.commit();

        }
    }
}
