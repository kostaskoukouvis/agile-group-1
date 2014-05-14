package se.chalmers.agile.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.agile.R;
import se.chalmers.agile.database.NotesTable;
import se.chalmers.agile.providers.MyNotesContentProvider;
import se.chalmers.agile.utils.AppPreferences;

/*
 * NoteEdit allows to enter a new note
 * or to change an existing
 */

public class NoteEdit extends Activity implements View.OnKeyListener {

    private EditText mTitleText;
    private EditText mBodyText;
    private Map<String, String> macros;
    private Uri noteUri;

    /**
     * Checks if any macro is applicable.
     */
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        EditText noteView = ((EditText) view);
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            String text = noteView.getText().toString();
            String key = text.substring(Math.max(text.length() - 2, 0));
            if (macros.containsKey(key)) {
                text = text.substring(0, text.length() - 2).concat(macros.get(key));
                noteView.setText(text);
                noteView.setSelection(text.length());
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all the macros.
     */
    private Map<String, String> getMacros() {
        Map<String, String> macros = new HashMap<String, String>();
        String macroPrefix = getString(R.string.macro_prefix);
        for (Field field : R.string.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()) && field.getType().equals(int.class)) {
                try {
                    if (field.getName().startsWith(macroPrefix)) {
                        int id = field.getInt(null);
                        macros.put(field.getName().substring(2), getString(id));
                    }
                } catch (Exception e) {
                }
            }
        }
        macros.putAll(AppPreferences.getInstance().getMacros());
        return macros;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.note_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mTitleText.setOnKeyListener(this);
        mBodyText = (EditText) findViewById(R.id.body);
        mBodyText.setOnKeyListener(this);
        Button confirmButton = (Button) findViewById(R.id.confirm);
        macros = getMacros();
        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        noteUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(MyNotesContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            noteUri = extras
                    .getParcelable(MyNotesContentProvider.CONTENT_ITEM_TYPE);

            fillData(noteUri);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        findViewById(R.id.macros_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(NoteEdit.this);
                dialog.setContentView(R.layout.macro_dialog);
                dialog.setTitle("Defined macros");
                TableLayout table = (TableLayout) dialog.findViewById(R.id.macro_table);
                for (Map.Entry<String, String> keyValue : macros.entrySet()) {
                    TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.macro_row, null, false);
                    table.addView(row);
                    TextView key = (TextView) getLayoutInflater().inflate(R.layout.macro_element, null, false);
                    key.setLayoutParams(new TableRow.LayoutParams(0));
                    key.setText(keyValue.getKey());
                    row.addView(key);

                    TextView value = (TextView) getLayoutInflater().inflate(R.layout.macro_element, null, false);
                    value.setText(keyValue.getValue());
                    value.setLayoutParams(new TableRow.LayoutParams(1));
                    row.addView(value);
                }
                dialog.show();
            }
        });
    }

    private void fillData(Uri uri) {
        String[] projection = {NotesTable.KEY_TITLE,
                NotesTable.KEY_BODY,};
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();

            mTitleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.KEY_TITLE)));
            mBodyText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.KEY_BODY)));

            // always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyNotesContentProvider.CONTENT_ITEM_TYPE, noteUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {

        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        // only save if either summary or description
        // is available

        if (body.length() == 0 && title.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(NotesTable.KEY_TITLE, title);
        values.put(NotesTable.KEY_BODY, body);

        if (noteUri == null) {
            // New note
            noteUri = getContentResolver().insert(MyNotesContentProvider.CONTENT_URI, values);
        } else {
            // Update note
            getContentResolver().update(noteUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(NoteEdit.this, "Please enter a Title",
                Toast.LENGTH_LONG).show();
    }
}