package com.example.todo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MainMenu extends Fragment {

    TaskAdapter taskAdapter;
    RecyclerView recyclerView;
    TasksHelper tasksHelper;
    TextView empty;
    Menu menu;

    boolean showCompleted;

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

        // Add navigation to fab
        view.findViewById(R.id.fragment_to_do_main_menu_fab).setOnClickListener(
                v -> Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_createTask)
            );

        // Get saved data
        tasksHelper = new TasksHelper(getContext());
        showCompleted = sharedPreferences.getBoolean("showCompletedTasks", false);

        // Set up RecyclerView and TaskAdapter
        recyclerView = view.findViewById(R.id.activity_main_recycler_view);
        taskAdapter = new TaskAdapter(view, this, getParentFragmentManager(), tasksHelper.getAllTasks(), showCompleted);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        // TextView for empty tasks
        empty = view.findViewById(R.id.activity_main_empty_indicator_text_view);
        if(taskAdapter.getItemCount() > 0) empty.setVisibility(View.GONE);

        // Add menu to toolbar
        Toolbar toolbar = view.findViewById(R.id.fragment_to_do_main_menu_toolbar);
        toolbar.inflateMenu(R.menu.menu);

        // Set values for menu
        menu = toolbar.getMenu();
        menu.findItem(R.id.menu_show_finished).setChecked(showCompleted);

        menu.findItem(R.id.menu_toggle_all).setTitle(getString( shouldCollapse() ? R.string.menu_collapse_all : R.string.menu_expand_all));

        // Add listener to menus
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.menu_show_finished:
                        // Handle check and update settings
                        item.setChecked(!item.isChecked());
                        showCompleted = item.isChecked();
                        editor.putBoolean("showCompletedTasks", item.isChecked());
                        editor.apply();

                        // Reload recycler view
                        taskAdapter.SetCompleted(item.isChecked());
                        updateFragmentChildViews();
                        return true;

                    case R.id.menu_remove_finished: {
                        // Make confirmation dialog
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());

                        alertDialogBuilder.setTitle("Confirm Delete?");
                        alertDialogBuilder.setMessage("Are you sure you would like to delete all finished tasks?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove all finished tasks
                                Toast toast = Toast.makeText(view.getContext(), "Tasks deleted!", Toast.LENGTH_SHORT);
                                toast.show();
                                tasksHelper.RemoveAllFinishedTasks();
                                updateFragmentChildViews();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            } // Do nothing
                        });
                        alertDialogBuilder.create().show();
                        return true;
                    }
                    case R.id.menu_remove_all: {
                        // Make confirmation dialog
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());

                        alertDialogBuilder.setTitle("Confirm Delete?");
                        alertDialogBuilder.setMessage("Are you sure you would like to delete ALL tasks?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove all finished tasks
                                Toast toast = Toast.makeText(view.getContext(), "All tasks deleted!", Toast.LENGTH_SHORT);
                                toast.show();
                                tasksHelper.RemoveAllTasks();
                                updateFragmentChildViews();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            } // Do nothing
                        });
                        alertDialogBuilder.create().show();
                        return true;
                    }
                    case R.id.menu_toggle_all:
                        // Perform update
                        boolean shouldCollapse = !shouldCollapse();
                        tasksHelper.SetExpandStatus(shouldCollapse);
                        updateFragmentChildViews();
                        return true;

                    default:
                        return false;
                }
            }
        });

        return view;
    }

    /**
     * The function refreshes the views in the fragment
     */
    void updateFragmentChildViews(Boolean shouldCollapse) {
        // Update text view, just in case.
        if(taskAdapter.getItemCount() > 0) empty.setVisibility(View.GONE);
        else empty.setVisibility(View.VISIBLE);

        // Set the visuals in the menu and set expand status
        MenuItem collapseItem = menu.findItem(R.id.menu_toggle_all);
        if(taskAdapter.getItemCount() == 0) collapseItem.setVisible(false);
        else {
            collapseItem.setVisible(true);
            collapseItem.setTitle(getString(shouldCollapse ? R.string.menu_collapse_all : R.string.menu_expand_all));
        }

        // Update adapter
        taskAdapter.UpdateTaskList(tasksHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
        recyclerView.requestLayout(); // might need forceLayout
    }

    void updateFragmentChildViews() {
        updateFragmentChildViews(shouldCollapse());
    }

    boolean shouldCollapse() {
        // Check collapse
        boolean shouldCollapse = true;
        for (Task task : tasksHelper.getAllTasks()) {
            // Skip tasks that don't show on recycler
            if (!showCompleted && task.isDone()) continue;

            // check if should collapse
            if(!task.getExpand()) {
                shouldCollapse = false;
                break;
            }
        }
        return shouldCollapse;
    }

}