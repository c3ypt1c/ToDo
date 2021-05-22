package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TasksHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 0;
    private static final String DATABASE_TABLE = "tasksDB";
    private static final String DATABASE_NAME = "tasks";
    private static final String TAG = "Database";

    public TasksHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String database_table = "CREATE TABLE " + DATABASE_TABLE + " ( id INTEGER PRIMARY KEY, task TEXT, task_desc TEXT, done INTEGER )";
        Log.d(TAG, "Made database");
        db.execSQL(database_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Updated database");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    ArrayList<Task> getAllTasks() {
        ArrayList<Task> Tasks = new ArrayList<>();
        String select = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select, null);

        if(cursor.moveToFirst()) { // Moves cursor to front line
            do {
                int taskID = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskDesk = cursor.getString(2);
                boolean taskDone = cursor.getInt(3) == 1; // 1 is done
                Task task = new Task(taskID, taskName, taskDesk, taskDone, this);
                Log.d(TAG, "Created task: \n" + task.toString());
                Tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close(); // close the cursor

        return Tasks;
    }

    public void UpdateTask(int id, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DATABASE_TABLE, values, "id = ?", new String[] {String.valueOf(id)});
    }

    public void AddTask(String name, String desc, boolean done) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", name);
        contentValues.put("task_desc", desc);
        contentValues.put("done", done ? 1 : 0); // 1 is done
        db.insert(DATABASE_TABLE, null, contentValues);
        Log.d(TAG, "Task added");
    }

    public void RemoveAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + DATABASE_TABLE);
        onCreate(db);
    }

    public void RemovedAllFinishedTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, "done = 1", null);
    }
}
