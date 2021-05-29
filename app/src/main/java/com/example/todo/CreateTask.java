package com.example.todo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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