package com.jmd;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmd.fragments.ManterPromocaoFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListEmptyFragment extends Fragment {


    public ListEmptyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Activity FAB
         */
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_white_24dp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // infla fragment de add promoção
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.base_container, new ManterPromocaoFragment())
                        .commit();
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_empty, container, false);
    }

}
