package se.chalmers.agile.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.NoteEdit;
import se.chalmers.agile.database.NotesTable;
import se.chalmers.agile.providers.MyNotesContentProvider;

/**
 * Created by iKotsos on 28/4/14.
 */
public class NotepadFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;

    OnNoteSelectedListener mListener;


    public static NotepadFragment createInstance() {
        NotepadFragment fragment = new NotepadFragment();
        return fragment;
    }
    /** Called when the activity is first created. */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // create the menu based on the XML definition

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.notes_list, container, false);
        return ll;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("e", "did");
        fillData();
        registerForContextMenu(getListView());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNoteSelectedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnNoteSelectedListener");
        }
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                createNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(MyNotesContentProvider.CONTENT_URI + "/"
                        + info.id);
                getActivity().getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void createNote() {
        Intent i = new Intent(getActivity(), NoteEdit.class);
        startActivity(i);
    }

    // Opens the second activity if an entry is clicked
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), NoteEdit.class);
        Uri todoUri = Uri.parse(MyNotesContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(MyNotesContentProvider.CONTENT_ITEM_TYPE, todoUri);

        startActivity(i);
  //      Uri noteUri = ContentUris.withAppendedId(MyNotesContentProvider.CONTENT_URI, id);
  //      mListener.onNoteSelected(noteUri);
    }


    public interface OnNoteSelectedListener {
        public void onNoteSelected(Uri noteUri);
    }

    private void fillData() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { NotesTable.KEY_TITLE };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.label };

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.notes_row, null, from,
                to, 0);

        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { NotesTable.KEY_ROWID, NotesTable.KEY_TITLE };
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                MyNotesContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

}
