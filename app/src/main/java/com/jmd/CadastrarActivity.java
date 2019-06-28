package com.jmd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jmd.modelo.Mercado;

public class CadastrarActivity extends AppCompatActivity {

    protected EditText aliasNome,
                        aliasEmail,
                        aliasSenha,
                        aliasTelefone,
                        aliasEndereco,
                        aliasGPS;

    protected Button aliasBtnBuscarGPS,
                     aliasBtnRegistrar,
                     aliasBtnVoltar;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        aliasNome     = findViewById(R.id.editCadastroNome);
        aliasEmail    = findViewById(R.id.editCadastroEmail);
        aliasSenha    = findViewById(R.id.editCadastroSenha);
        aliasTelefone = findViewById(R.id.editCadastroTelefone);
        aliasEndereco = findViewById(R.id.editCadastroEndereco);
        aliasGPS      = findViewById(R.id.editCadastroGPS);

        aliasBtnBuscarGPS = findViewById(R.id.btnCadastroBuscarGPS);
        aliasBtnRegistrar = findViewById(R.id.btnCadastroCadastrar);
        aliasBtnVoltar    = findViewById(R.id.btnCadastroVoltar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        /**
         * Botão de cadastar.
         */
        aliasBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario(aliasEmail.getText().toString(), aliasSenha.getText().toString());
            }
        });

        /**
         * Botão pra buscar coordenadas do GPS.
         */
        aliasBtnBuscarGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * Botão voltar. Encerra a activity de cadastro
         */
        aliasBtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cadastrarUsuario(String email, String senha) {
        final AlertDialog.Builder b = new AlertDialog.Builder(this);


        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Mercado mercado = new Mercado();
                    mercado.setNome(aliasNome.getText().toString());
                    mercado.setEndereco(aliasEndereco.getText().toString());
                    mercado.setTelefone(aliasTelefone.getText().toString());

                    // salva informações sobre o mercado no banco
                    DatabaseReference mercadoRef = database.getReference("mercados");
                    // novo nó
                    mercadoRef = mercadoRef.child(mercadoRef.push().getKey());

                    mercadoRef.setValue(mercado).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                b.setTitle("Usuario criado");
                                b.setMessage(auth.getCurrentUser().toString()+"\n"+auth.getCurrentUser().getEmail());
                                b.show();
                            } else {
                                b.setTitle("Ops");
                                b.setMessage("Ocorreu um problema ao efetuar o cadastro");
                                b.show();
                                Log.d("FireDB", task.getException().getMessage());
                            }
                        }
                    });


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(CadastrarActivity.this, "Erro ao autenticar "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
