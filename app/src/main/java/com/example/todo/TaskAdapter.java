package com.example.todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TaskAdapter extends
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<Task> Tasks; // Storage for tasks
    private final View view;
    private final FragmentManager fragmentManager;
    private final Fragment fragment;

    final static class TaskViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final CheckBox done;
        final TaskAdapter taskAdapter;

        public TaskViewHolder(@NonNull View itemView, TaskAdapter taskAdapter) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_item_task_entry_title);
            done = itemView.findViewById(R.id.recycler_item_task_entry_done);
            this.taskAdapter = taskAdapter;
        }
    }

    public TaskAdapter(View view, Fragment fragment, ArrayList<Task> Tasks) {
        this.view = view;
        this.Tasks = Tasks;
        this.fragment = fragment;
        this.fragmentManager = fragment.getParentFragmentManager();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_item_task_entry, parent, false);

        return new TaskViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task currentTask = Tasks.get(position);

        Log.w("TaskAdapter:onBindViewHolder", "onBindViewHolder: Bound" + position + " to " + currentTask.getTaskName());

        // Set initial data
        holder.done.setChecked(currentTask.isDone());
        holder.title.setText(currentTask.getTaskName());

        // Add listeners
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTask.toggleDone();
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                Log.w("AAAAAA", "onClick: Clicked!");

                fragmentManager.setFragmentResultListener("edit_id", fragment, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                        holder.done.setChecked(currentTask.isDone());
                        holder.title.setText(currentTask.getTaskName());
                        Log.w("AAAAAA", "onClick: Updated!");
                    }
                });

                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("internal_temporary", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Log.w("AAAAAA", "onClick: adding to last_edit_id: " + currentTask.getId());
                editor.putInt("last_edit_id", currentTask.getId());
                editor.commit();

                Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_editTask);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Tasks.size();
    }


}
