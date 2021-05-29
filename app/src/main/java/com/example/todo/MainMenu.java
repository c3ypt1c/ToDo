package com.example.todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

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

        // Get Shared Preferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("prem", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //editor.putBoolean("showCompletedTasks", false);
        //editor.apply();

        // Add navigation to fab
        view.findViewById(R.id.fragment_to_do_main_menu_fab).setOnClickListener(
                v -> Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_createTask)
            );

        // Get Data
        TasksHelper tasksHelper = new TasksHelper(getContext());

        TextView empty = view.findViewById(R.id.activity_main_empty_indicator_text_view);
        if(tasksHelper.getAllTasks().size() > 0) empty.setVisibility(View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.activity_main_recycler_view);
        TaskAdapter taskAdapter = new TaskAdapter(view, this, getParentFragmentManager(), tasksHelper.getAllTasks(), false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(taskAdapter);

        // Add menu to toolbar
        Toolbar toolbar = view.findViewById(R.id.fragment_to_do_main_menu_toolbar);
        toolbar.inflateMenu(R.menu.menu);

        //((MenuItem)toolbar.findViewById(R.id.menu_remove_finished)).setChecked(sharedPreferences.getBoolean("showCompletedTasks", false));

        //MainMenu mainMenu = this;
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId") // TODO: Fix.
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.menu_show_finished:
                        // Handle check and update settings
                        item.setChecked(!item.isChecked());
                        editor.putBoolean("showCompletedTasks", item.isChecked());
                        editor.apply();

                        // Reload recycler view
                        taskAdapter.SetCompleted(item.isChecked());
                        taskAdapter.notifyDataSetChanged();
                        recyclerView.forceLayout();
                        return true;

                    default:
                        return false;
                }
            }
        });

        Log.w("AAAAAAAAAAAAAAa", "onCreateView: "+taskAdapter.getItemCount());

        return view;
    }

}