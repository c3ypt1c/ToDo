package com.example.todo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final ArrayList<Task> Tasks; // Storage for tasks

    class TaskViewHolder extends RecyclerView.ViewHolder {

        public final TextView title;
        final TaskAdapter taskAdapter;

        public TaskViewHolder(@NonNull View itemView, TaskAdapter taskAdapter) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_item_task_entry_title);
            this.taskAdapter = taskAdapter;
        }
    }

    public TaskAdapter(Context context, ArrayList<Task> Tasks) {
        this.Tasks = Tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.w("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAa", "IT'S INFLATINGGGGGGGGGGGGGG ");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(layoutInflater == null) Log.w("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAa", "onCreateViewHolder: LayoutInflater null");

        View view = layoutInflater.inflate(R.layout.recycler_item_task_entry, parent, false);

        if(view == null) Log.w("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAa", "onCreateViewHolder: view null");

        return new TaskViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        String currentTitle = Tasks.get(position).getTaskName();
        Log.w("TaskAdapter:onBindViewHolder", "onBindViewHolder: Bound" + position + " to " + currentTitle);
        holder.title.setText(currentTitle);
    }

    @Override
    public int getItemCount() {
        return Tasks.size();
    }


}
