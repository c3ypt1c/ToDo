package com.example.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;

public class TaskAdapter extends
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> AllTasks; // Storage for tasks
    private ArrayList<Task> IncompleteTasks; // Storage for incomplete tasks
    private final View view;
    private final MainMenu fragment;
    private final FragmentManager fragmentManager;
    private boolean showCompleted;
    private final boolean isLandscape;

    final static class TaskViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        public final CheckBox done;
        public final TextView desc;
        public final TextView descTitle;
        public final AppCompatImageButton expand;
        public final AppCompatImageButton delete;
        public final View itemView;
        public final View[] collapsable;
        public final View[] collapsableLandscape;
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
            collapsableLandscape = new View[] {desc, descTitle};
            this.taskAdapter = taskAdapter;
        }

        public void Expand() {
            for(View element : taskAdapter.isLandscape ? collapsableLandscape : collapsable) element.setVisibility(View.VISIBLE);
            expand.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);

            // Hide the description if the description is empty
            if(desc.getText().toString().length() == 0) {
                descTitle.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
            }
        }

        public void Collapse() {
            for (View element : taskAdapter.isLandscape ? collapsableLandscape : collapsable) element.setVisibility(View.GONE);
            expand.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
        }
    }

    public TaskAdapter(View view, MainMenu fragment, FragmentManager fm, ArrayList<Task> Tasks, boolean showCompleted) {
        this.view = view;
        this.fragment = fragment;
        this.fragmentManager = fm;
        this.showCompleted = showCompleted;
        this.AllTasks = Tasks;
        this.isLandscape = view.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE; //Check if landscape
        SetCompleted(showCompleted);
    }

    public void UpdateTaskList(ArrayList<Task> tasks) {
        this.AllTasks = tasks;

        ArrayList<Task> notCompletedTasks = new ArrayList<>();
        // Filter not completed tasks
        for (Task task : AllTasks) if (!task.isDone()) notCompletedTasks.add(task);
        this.IncompleteTasks = notCompletedTasks;
    }

    public void SetCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
        ArrayList<Task> notCompletedTasks = new ArrayList<>();
        // Filter not completed tasks
        for (Task task : AllTasks) if (!task.isDone()) notCompletedTasks.add(task);
        this.IncompleteTasks = notCompletedTasks;

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
        // Set internals
        Task currentTask = (!showCompleted ? IncompleteTasks : AllTasks).get(position);

        // Set initial data
        holder.done.setChecked(currentTask.isDone());
        holder.title.setText(currentTask.getTaskName());
        holder.desc.setText(currentTask.getTaskDesc());

        // Landscape management
        if(isLandscape) {
            if(currentTask.getTaskDesc().length() == 0) holder.expand.setVisibility(View.GONE);
            else holder.expand.setVisibility(View.VISIBLE);
        }

        // Set saved expand status
        if(currentTask.getExpand()) holder.Expand();
        else holder.Collapse();
        view.requestLayout();

        // Add listeners
        // On check
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update List
                if(currentTask.toggleDone()) {
                    IncompleteTasks.remove(currentTask);
                } else {
                    IncompleteTasks.add(currentTask);
                }

                if(!showCompleted) fragment.updateRecycler();
            }
        });

        // Edit task
        holder.title.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("internal_temporary", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("last_edit_id", currentTask.getId());
                editor.commit();

                Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_editTask);
            }
        });

        // collapse
        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTask.toggleExpand()) holder.Expand();
                else holder.Collapse();

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
        return (!showCompleted ? IncompleteTasks : AllTasks).size();
    }

}
