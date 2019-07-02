package com.jmd;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class LoginActivity extends ActivityFirebase {

    private EditText  aliasEmail,
                        aliasSenha;

    private Button  btnLogin,
                    btnCadastro;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Campos da activity
         */
        aliasEmail = findViewById(R.id.editLoginEmail);
        aliasSenha = findViewById(R.id.editLoginSenha);

        btnLogin    = findViewById(R.id.btnLoginEntrar);
        btnCadastro = findViewById(R.id.btnLoginCadastre);

        /**
         * Progressbar
         */
        progressBar = findViewById(R.id.progressBarLogin);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        /**
         * Firebase instances
         */
        auth = FirebaseAuth.getInstance();


        /**
         * Botão logar. Faz autenticação e encaminha para a activity interna.
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = aliasEmail.getText().toString();
                String senha = aliasSenha.getText().toString();

                // TODO: Implementar verificação dos campos
                if (email == null || email.equals("") ||
                    senha == null || senha.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "Campos EMAIL e SENHA são obrigatórios!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // mostra progressbar
                progressBar.setVisibility(View.VISIBLE);

                // tenta login
                logar(email, senha);
            }
        });

        /**
         * Botão cadastre-se. Abre a activity de cadastro
         */
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), CadastrarActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Função logar | Faz requisição ao firebase comparando os dados de login
     * @param email
     * @param senha
     */
    private void logar(String email, String senha) {
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // esconde o progressbar
                progressBar.setVisibility(View.GONE);

                // verifica se o login foi concluido com sucesso
                if (task.isSuccessful()) {

                    // usuario logou
                    Toast.makeText(LoginActivity.this, "Login feito com sucesso", Toast.LENGTH_SHORT).show();

                    // abre activity interna
                    Intent i = new Intent(getBaseContext(), BaseActivity.class);
                    startActivity(i);

                    // fecha activity do login
                    finish();
                } else {
                    // problema no login
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("FireAuth", task.getException().getMessage());
                }
            }
        });
    }
}
