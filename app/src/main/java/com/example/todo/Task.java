package com.example.todo;

import android.content.ContentValues;
import androidx.annotation.NonNull;

public class Task {
    private final int id;
    private String taskName;
    private String taskDesc;
    private boolean done;
    private final TasksHelper tasksHelper;

    public Task(int id, String taskName, String taskDesk, boolean done, TasksHelper tasksHelper) {
        this.id = id;
        this.taskName = taskName;
        this.taskDesc = taskDesk;
        this.done = done;
        this.tasksHelper = tasksHelper;
    }

    private ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", taskName);
        contentValues.put("task_desc", taskDesc);
        contentValues.put("done", done ? 1 : 0); // 1 is done
        return contentValues;
    }

    private void update() {
        tasksHelper.UpdateTask(id, getContentValues());
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
        update();
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
        update();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
        update();
    }

    public boolean toggleDone() {
        setDone(!isDone());
        return isDone();
    }

    @NonNull
    @Override
    public String toString() {
        return "Task id: " + id + "\n" +
                "Task: " + taskName + "\n" +
                "Task desc: " + taskDesc + "\n" +
                "Task done: " + done + "\n";
    }
}
