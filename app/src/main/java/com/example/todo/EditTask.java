package com.example.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class EditTask extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateTitle(View view, String newTitle) {
        Toolbar toolbar = view.findViewById(R.id.fragment_edit_task_toolbar);
        String titleString = view.getResources().getString(R.string.fragment_edit_task_toolbar_title) + " '" + newTitle + "'";
        toolbar.setTitle(titleString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        // Get id from preferences then get task
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("internal_temporary", Context.MODE_PRIVATE);
        int taskId = sharedPreferences.getInt("last_edit_id", -1);
        TasksHelper tasksHelper = new TasksHelper(view.getContext());
        Task task = tasksHelper.GetTaskById(taskId);

        // Set the title of the toolbar
        updateTitle(view, task.getTaskName());

        // Set the input and add animation
        TextView taskNameField = view.findViewById(R.id.fragment_edit_task_name_field);
        taskNameField.setText(task.getTaskName());
        taskNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTitle(view, taskNameField.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set description
        TextView taskDescField = view.findViewById(R.id.fragment_edit_task_description_field);
        taskDescField.setText(task.getTaskDesc());

        // Set completed
        CheckBox taskCompleted = view.findViewById(R.id.fragment_edit_task_completed_field);
        taskCompleted.setChecked(task.isDone());

        // Add finalise button
        Button updateTask = view.findViewById(R.id.fragment_edit_task_button);
        updateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Submit changes to database
                task.setTaskName(taskNameField.getText().toString());
                task.setTaskDesc(taskNameField.getText().toString());
                task.setDone(taskCompleted.isChecked());

                Navigation.findNavController(view).navigate(R.id.action_editTask_to_toDoMainMenu);
            }
        });

        return view;
    }

}
