package com.example.todo;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTask extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);
        Button button = view.findViewById(R.id.fragment_create_task_button);

        // Add back arrow to actionBar
        Toolbar toolBar = view.findViewById(R.id.fragment_create_task_toolbar);
        toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.clearFocus(); // Remove keyboard from view
                // Go back to menu
                Navigation.findNavController(view).popBackStack(R.id.createTask, true);
            }
        });

        // Add button listener to add task.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get all resources
                // Title
                EditText nameField = view.findViewById(R.id.fragment_add_task_name_field);
                String titleText = nameField.getText().toString();

                // Description
                EditText descriptionField = view.findViewById(R.id.fragment_add_task_description_field);
                String descriptionText = descriptionField.getText().toString();

                // Completed
                CheckBox completedField = view.findViewById(R.id.fragment_add_task_completed_field);
                boolean completed = completedField.isChecked();

                // Format texts
                titleText = titleText.trim();
                descriptionText = descriptionText.trim();

                //Check that title is not empty
                if(titleText.length() == 0) {
                    Toast toast = Toast.makeText(getContext(), R.string.fragment_create_task_toast, Toast.LENGTH_SHORT);
                    toast.show();
                    return; // Don't continue and input into database
                }

                //Input into database
                TasksHelper tasksHelper = new TasksHelper(getContext());
                tasksHelper.AddTask(titleText, descriptionText, completed);

                view.clearFocus(); //Remove keyboard from view
                Navigation.findNavController(view).popBackStack(R.id.createTask, true);
            }
        });

        return view;
    }

}