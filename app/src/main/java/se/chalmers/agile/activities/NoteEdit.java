package se.chalmers.agile.activities;

        import android.app.Activity;
        import android.content.ContentValues;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import se.chalmers.agile.R;
        import se.chalmers.agile.database.NotesTable;
        import se.chalmers.agile.providers.MyNotesContentProvider;

/*
 * NoteEdit allows to enter a new note
 * or to change an existing
 */

public class NoteEdit extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;

    private Uri noteUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.note_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        Button confirmButton = (Button) findViewById(R.id.confirm);

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
    }

    private void fillData(Uri uri) {
        String[] projection = { NotesTable.KEY_TITLE,
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