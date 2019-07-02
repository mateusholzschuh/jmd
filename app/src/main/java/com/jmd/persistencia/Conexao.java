package com.jmd.persistencia;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Conexao {
    // instanciar Firebase
    private static FirebaseAuth firebaseAuth;
    // escutar o status da autenticação firebase
    private static FirebaseAuth.AuthStateListener authStateListener;
    // intancia do usuario do firebase
    private static FirebaseUser firebaseUser;

    private Conexao() {
    }

    //instanciar somente uma conexão!!! Singonton
    public static FirebaseAuth getFirebaseAuth(){
        if (firebaseAuth==null){
            inicializarFirebase();
        }
        return firebaseAuth;
    }

    private static void inicializarFirebase() {
        firebaseAuth= FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    firebaseUser= user;
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static FirebaseUser getFirebaseUser(){
        return firebaseUser;
    }

    public static void logOut(){
        firebaseAuth.signOut();
    }
}
