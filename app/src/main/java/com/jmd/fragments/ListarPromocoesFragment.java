package com.jmd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmd.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListarPromocoesFragment extends Fragment {

    RecyclerView aliasRecycler;

    public ListarPromocoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listar_promocoes, container, false);

        aliasRecycler = v.findViewById(R.id.recyclerListarPromocoes);

        // Inflate the layout for this fragment
        return v;
    }

}
