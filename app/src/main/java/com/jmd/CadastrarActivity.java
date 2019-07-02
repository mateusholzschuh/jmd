package com.jmd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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

public class CadastrarActivity extends ActivityFirebase {

    protected EditText aliasNome,
                        aliasEmail,
                        aliasSenha,
                        aliasTelefone,
                        aliasEndereco,
                        aliasGPS;

    protected Button aliasBtnBuscarGPS,
                     aliasBtnRegistrar,
                     aliasBtnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        /**
         * Campos da activity
         */
        aliasNome     = findViewById(R.id.editCadastroNome);
        aliasEmail    = findViewById(R.id.editCadastroEmail);
        aliasSenha    = findViewById(R.id.editCadastroSenha);
        aliasTelefone = findViewById(R.id.editCadastroTelefone);
        aliasEndereco = findViewById(R.id.editCadastroEndereco);
        aliasGPS      = findViewById(R.id.editCadastroGPS);

        aliasBtnBuscarGPS = findViewById(R.id.btnCadastroBuscarGPS);
        aliasBtnRegistrar = findViewById(R.id.btnCadastroCadastrar);
        aliasBtnVoltar    = findViewById(R.id.btnCadastroVoltar);

        /**
         * Progressbar
         */
        progressBar = findViewById(R.id.progressBarCadastrar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        /**
         * Firebase instances
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        /**
         * Botão de cadastar.
         */

        aliasBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implementar verificação dos campos
                String nome, email, senha, endereco, telefone, gps;

                nome  = aliasNome.getText().toString();
                email = aliasEmail.getText().toString();
                senha = aliasSenha.getText().toString();
                gps   = aliasGPS.getText().toString();
                endereco = aliasEndereco.getText().toString();
                telefone = aliasTelefone.getText().toString();
                
                if (nome == null || email == null || senha == null || 
                    gps == null || endereco == null || telefone == null ||
                    nome.equals("") || email.equals("") || senha.equals("") || gps.equals("") ||
                    endereco.equals("") || telefone.equals(""))
                {
                    Toast.makeText(CadastrarActivity.this, "Todos os campos são OBRIGATÓRIOS", Toast.LENGTH_SHORT).show();
                    return;
                }

                // mostra progressbar
                progressBar.setVisibility(View.VISIBLE);

                // tenta cadastrar
                cadastrarUsuario(email, senha, nome, endereco, telefone, gps);
            }
        });

        /**
         * Botão pra buscar coordenadas do GPS.
         */
        aliasBtnBuscarGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mostra progressbar
                progressBar.setVisibility(View.VISIBLE);
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

    private void cadastrarUsuario(String email, String senha, final String nome,
                                  final String endereco, final String telefone, final String gps) {
        final AlertDialog.Builder b = new AlertDialog.Builder(this);


        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // verifica resposta do firebase
                if (task.isSuccessful()) {
                    // tenta inserir demais campos
                    Mercado mercado = new Mercado();
                    mercado.setNome(nome);
                    mercado.setEndereco(endereco);
                    mercado.setTelefone(telefone);
//                    mercado.setLocation(); ???

                    // salva informações sobre o mercado no banco
                    DatabaseReference mercadoRef = database.getReference("mercados");

                    // novo nó
                    mercadoRef = mercadoRef.child(auth.getCurrentUser().getUid());

                    mercadoRef.setValue(mercado).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // esconde progressbar
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                b.setTitle("Usuario criado");
                                b.setMessage(auth.getCurrentUser().toString()+"\n"+auth.getCurrentUser().getEmail());
                                b.show();

                                // usuario cadastrado
                                // abre activity principal
                                Intent i = new Intent(getBaseContext(), BaseActivity.class);
                                startActivity(i);
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
