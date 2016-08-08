package com.example.todolist;

import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        NewItemFragment.OnNewItemAddedListener, LoaderCallbacks<Cursor> {

    // Адаптер для привязки массива
    private ToDoItemAdapter adapter;

    // Массив для хранения списка задач
    private ArrayList<ToDoItem> todoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ссылки на фрагменты
        FragmentManager fm = getFragmentManager();
        ToDoListFragment todoListFragment = (ToDoListFragment) fm
                .findFragmentById(R.id.toDoListFragment);

        // Массив для хранения списка задач
        todoItems = new ArrayList<>();

        // Адаптер для привязки массива к ListView
        int resID = R.layout.todolist_item;
        adapter = new ToDoItemAdapter(this, resID, todoItems);

        // Связываем массив с ListView
        todoListFragment.setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.to_do_list, menu);
//        return true;
//    }

    public void onNewItemAdded(String newItem) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ToDoContentProvider.KEY_TASK, newItem);

        cr.insert(ToDoContentProvider.CONTENT_URI, values);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ToDoContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int keyTaskIndex = cursor
                .getColumnIndexOrThrow(ToDoContentProvider.KEY_TASK);

        todoItems.clear();
        while (cursor.moveToNext()) {
            ToDoItem newItem = new ToDoItem(cursor.getString(keyTaskIndex));
            todoItems.add(newItem);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}