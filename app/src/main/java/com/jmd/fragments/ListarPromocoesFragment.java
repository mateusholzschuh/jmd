package com.jmd.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jmd.BaseActivity;
import com.jmd.R;
import com.jmd.adaptadores.PromocaoAdapter;
import com.jmd.modelo.Promocao;
import com.jmd.persistencia.PromocaoDAO;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListarPromocoesFragment extends Fragment {

    protected RecyclerView aliasRecycler;
    protected List<Promocao> listPromocoes;
    protected PromocaoAdapter adapter;
    protected Promocao promocao;

    // Firebase
    FirebaseDatabase database;
    DatabaseReference ref;

    public ListarPromocoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listar_promocoes, container, false);

        /**
         * Activity title
         */
        getActivity().setTitle("Promoções");

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

        /**
         * Activity progressbar
         */
        ((BaseActivity) getActivity()).toggleProgressbar(true);

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Adicionar nova promoção", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        aliasRecycler = v.findViewById(R.id.recyclerListarPromocoes);
        promocao = new Promocao();
        database = FirebaseDatabase.getInstance();

        aliasRecycler.setLayoutManager(new LinearLayoutManager(v.getContext()));
        aliasRecycler.setItemAnimator(new DefaultItemAnimator());

        // TODO: FETCH ALL RECORDS FROM FIREBASE HERE
        listPromocoes = new ArrayList<>();
//        database = FirebaseDatabase.getInstance();
//        ref = database.getReference("promocoes");
//        ref = ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        ref.addListenerForSingleValueEvent(recuperaDados());
        PromocaoDAO.getInstance().buscarTodos(recuperaDados());

//        promocao = new Promocao();
//        promocao.setUid("@@@");
//        promocao.setMercadoUID("123");
//        promocao.setNome("Acucar");
//        promocao.setValidade("12/04/2097");
//        promocao.setPreco(12.88f);
//        listPromocoes.add(promocao);
//
//        promocao = new Promocao();
//        promocao.setUid("!@#");
//        promocao.setMercadoUID("123");
//        promocao.setNome("Arroz");
//        promocao.setValidade("12/04/2097");
//        promocao.setPreco(129.88f);
//        listPromocoes.add(promocao);

        aliasRecycler.setAdapter(adapter = new PromocaoAdapter(v.getContext(), listPromocoes, onClickPromocao()));

        // Inflate the layout for this fragment
        return v;
    }

    protected PromocaoAdapter.PromocaoOnClickListener onClickPromocao() {
//        Solve that with fragment manager !
//        final Intent i = new Intent(getContext(), ManterActivity.class);
        return new PromocaoAdapter.PromocaoOnClickListener() {
            @Override
            public void onClickPromocao(PromocaoAdapter.PromocoesViewHolder holder, int idx) {

                promocao = listPromocoes.get(idx);

                // envia objeto clicado para o manter...
                ManterPromocaoFragment fragment = new ManterPromocaoFragment();

                Bundle bundle = new Bundle();
                bundle.putString("PROMO_UUID", promocao.getUid());

                fragment.setArguments(bundle);

                // carrega fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.base_container, fragment)
                        .commit();
            }
        };
    }

    private ValueEventListener recuperaDados () {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPromocoes.clear();

                for (DataSnapshot promoSnapshot : dataSnapshot.getChildren()) {
                    Promocao promo = promoSnapshot.getValue(Promocao.class);
                    promo.setUid(promoSnapshot.getKey());
                    listPromocoes.add(promo);
                }

                adapter.notifyDataSetChanged();
                if(((BaseActivity) getActivity()) != null)
                    ((BaseActivity) getActivity()).toggleProgressbar(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ops! Ocorreu um erro.", Toast.LENGTH_SHORT).show();
                if(((BaseActivity) getActivity()) != null)
                    ((BaseActivity) getActivity()).toggleProgressbar(false);
            }
        };
    }


}
