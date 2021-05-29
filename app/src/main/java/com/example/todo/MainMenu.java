package com.example.todo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MainMenu extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_main_menu, container, false);

        // Add navigation to fab
        view.findViewById(R.id.fragment_to_do_main_menu_fab).setOnClickListener(
                v -> Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_createTask)
            );

        // Get Data
        TasksHelper tasksHelper = new TasksHelper(getContext());

        TextView empty = view.findViewById(R.id.activity_main_empty_indicator_text_view);
        if(tasksHelper.getAllTasks().size() > 0) empty.setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.activity_main_recycler_view);
        TaskAdapter taskAdapter = new TaskAdapter(view, tasksHelper.getAllTasks());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(taskAdapter);
        Log.w("AAAAAAAAAAAAAAa", "onCreateView: "+taskAdapter.getItemCount());

        return view;
    }


}