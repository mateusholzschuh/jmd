package com.jmd.persistencia;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

    /**
     * <h1>Remove uma promoção.</h1>
     * @param promocao A promocao a ser removida
     * @param activity A activity do contexto atual
     * @param listener Callback da resposta
     */
    public void apagar (final Promocao promocao, final Activity activity, final OnCompleteListener listener) {
        // pega referencia da promo
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // pega ref da imagem da promo
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(promocao.getImagem());
        // deleta imagem primeiro
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // remove nó da promo
                ref.child(promocao.getUid()).removeValue().addOnCompleteListener(activity, listener);
            }
        });
    }

    /**
     * <h1>Buscar uma promoção pelo <i>UUID</i> dela.</h1>
     * @param uuid ID da promoção
     * @param listener Callback da busca
     */
    public void buscar (String uuid, ValueEventListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child(uuid).addListenerForSingleValueEvent(listener);
    }

    /**
     * <h1>Buscar todas as promoções do mercado que está logado no sistema.</h1>
     * @param listener Callback da busca
     */
    public void buscarTodos (ValueEventListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * <h1>Buscar todas as promoções do mercado que está logado no sistema, e manter sincronizado com o firebase.</h1>
     * @param listener Callback da busca
     */
    public void buscarTodosSincronizado (ValueEventListener listener) {
        ref = database.getReference(PROMO).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(listener);
    }

    /**
     * <h1>Buscar todas as promoções do mercado com o <b>UUID</b> passado como parâmetro.</h1>
     * @param uuidMercado ID do mercado
     * @param listener Callback da busca
     */
    public void buscarTodas (String uuidMercado, ValueEventListener listener) {
        ref = database.getReference(PROMO).child(uuidMercado);
        ref.addListenerForSingleValueEvent(listener);
    }

    /**
     * <h1>Buscar todas as promoções do mercado com o <i>UUID</i> passado como parâmetro, e manter sincronizado com o firebase.</h1>
     * @param uuidMercado ID do mercado
     * @param listener Callback da busca
     */
    public void buscarTodasSincronizado (String uuidMercado, ValueEventListener listener) {
        ref = database.getReference(PROMO).child(uuidMercado);

        ref.addValueEventListener(listener);
    }
}
