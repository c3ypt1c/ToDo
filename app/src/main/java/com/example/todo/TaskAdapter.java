package com.example.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<Task> Tasks; // Storage for tasks
    private final View view;
    private final Fragment fragment;
    private final FragmentManager fragmentManager;

    final static class TaskViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final CheckBox done;
        public final TextView desc;
        public final TextView descTitle;
        public final androidx.appcompat.widget.AppCompatImageButton expand;
        public final androidx.appcompat.widget.AppCompatImageButton delete;
        public final View itemView;
        public final View[] collapsable;
        public boolean expanded = false;
        final TaskAdapter taskAdapter;

        public TaskViewHolder(@NonNull View itemView, TaskAdapter taskAdapter) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_item_task_entry_title);
            done = itemView.findViewById(R.id.recycler_item_task_entry_done);
            descTitle = itemView.findViewById(R.id.recycler_item_task_description);
            desc = itemView.findViewById(R.id.recycler_item_task_description_contents);
            expand = itemView.findViewById(R.id.recycler_item_task_entry_expand_button);
            delete = itemView.findViewById(R.id.recycler_item_task_delete_button);
            this.itemView = itemView;

            collapsable = new View[] {desc, descTitle, delete}; //Add collapsable-s here
            this.taskAdapter = taskAdapter;
        }
    }

    public TaskAdapter(View view, Fragment fragment, FragmentManager fm, ArrayList<Task> Tasks) {
        this.view = view;
        this.Tasks = Tasks;
        this.fragment = fragment;
        this.fragmentManager = fm;
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
        holder.desc.setText(currentTask.getTaskDesc());


        // Add listeners
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTask.toggleDone();
            }
        });

        // Edit task
        holder.title.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                Log.w("AAAAAA", "onClick: Clicked!");

                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("internal_temporary", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Log.w("AAAAAA", "onClick: adding to last_edit_id: " + currentTask.getId());
                editor.putInt("last_edit_id", currentTask.getId());
                editor.commit();

                Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_editTask);
            }
        });

        // collapse
        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("AAAAAAAAAAAAAAAAa", "expanded");
                if(!holder.expanded) {
                    for(View element : holder.collapsable) element.setVisibility(View.VISIBLE);
                    holder.expand.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                } else {
                    for(View element : holder.collapsable) element.setVisibility(View.GONE);
                    holder.expand.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                }
                holder.expanded = !holder.expanded;
                view.requestLayout();
            }
        });

        // delete
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get confirm
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());

                alertDialogBuilder.setTitle("Confirm Delete?");
                alertDialogBuilder.setMessage("Are you sure you would like to delete task: '" + currentTask.getTaskName() + "'");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentTask.Delete();
                        Toast toast = Toast.makeText(view.getContext(), "Task deleted!", Toast.LENGTH_SHORT);
                        toast.show();

                        //Make recycler disappear on delete, hacky method but works.
                        FragmentTransaction tr = fragmentManager.beginTransaction();
                        tr.detach(fragment);
                        tr.commit();

                        tr = fragmentManager.beginTransaction();
                        tr.attach(fragment);
                        tr.commit();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {} // Do nothing
                });
                alertDialogBuilder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return Tasks.size();
    }

}
