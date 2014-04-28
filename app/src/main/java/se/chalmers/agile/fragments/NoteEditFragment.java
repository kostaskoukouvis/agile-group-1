package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import se.chalmers.agile.R;
import se.chalmers.agile.database.NotesTable;
import se.chalmers.agile.providers.MyNotesContentProvider;

/**
 * Created by iKotsos on 28/4/14.
 */
public class NoteEditFragment extends Fragment {
    private EditText mTitleText;
    private EditText mBodyText;

    private Uri noteUri;

    public static NoteEditFragment createInstance() {
        NoteEditFragment fragment = new NoteEditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // check from the saved Instance
        noteUri = (bundle == null) ? null : (Uri) bundle.getParcelable(MyNotesContentProvider.CONTENT_ITEM_TYPE);
        fillData(noteUri);
       }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTitleText = (EditText) getActivity().findViewById(R.id.title);
        mBodyText = (EditText) getActivity().findViewById(R.id.body);
        Button confirmButton = (Button) getActivity().findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    makeToast();
                } else {
                    getActivity().setResult(0);
                    getActivity().finish();
                }
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.note_edit, container, false);
        return ll;
    }

    public void fillData(Uri uri) {
        String[] projection = { NotesTable.KEY_TITLE,
                NotesTable.KEY_BODY,};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
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

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyNotesContentProvider.CONTENT_ITEM_TYPE, noteUri);
    }

    @Override
    public void onPause() {
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
            noteUri = getActivity().getContentResolver().insert(MyNotesContentProvider.CONTENT_URI, values);
        } else {
            // Update note
            getActivity().getContentResolver().update(noteUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(getActivity(), "Please enter a Title",
                Toast.LENGTH_LONG).show();
    }
}
