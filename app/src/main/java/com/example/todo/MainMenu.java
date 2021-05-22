package com.example.todo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        view.findViewById(R.id.fragment_to_do_main_menu_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get all input elements
                

                //Put all the values into database


                Navigation.findNavController(view).navigate(R.id.action_toDoMainMenu_to_createTask);
            }
        });
        return view;
    }
}