package com.jmd.persistencia;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jmd.modelo.Promocao;

public class PromocaoDAO {
    private static PromocaoDAO instance;

    //private static FirebaseUser user;
    private static FirebaseDatabase database;
    private DatabaseReference ref;

    private String PROMO = "promocoes";

    private PromocaoDAO() {
        //user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
    }

    public static PromocaoDAO getInstance() {
        if (instance == null)
            instance = new PromocaoDAO();
        return instance;
    }

    public void salvar (Promocao promocao, Activity activity, OnCompleteListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String key = (promocao.getUid() != null && !promocao.getUid().isEmpty()) ?
                promocao.getUid() : ref.push().getKey();

        // remove uuid do objeto
        promocao.setUid(null);

        ref.child(key).setValue(promocao).addOnCompleteListener(activity, listener);
    }

    public void apagar (Promocao promocao, Activity activity, OnCompleteListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child(promocao.getUid()).removeValue().addOnCompleteListener(activity, listener);
    }

    public void buscar (String uuid, ValueEventListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child(uuid).addValueEventListener(listener);
    }

    public void buscarTodos (ValueEventListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(listener);
    }
}
