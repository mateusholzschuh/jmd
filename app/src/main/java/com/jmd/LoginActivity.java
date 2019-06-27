package com.jmd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    protected EditText  aliasEmail,
                        aliasSenha;

    protected Button btnLogin,
                     btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        aliasEmail = findViewById(R.id.editLoginEmail);
        aliasSenha = findViewById(R.id.editLoginSenha);

        btnLogin    = findViewById(R.id.btnLoginEntrar);
        btnCadastro = findViewById(R.id.btnLoginCadastre);

        /**
         * Botão logar. Faz autenticação e encaminha para a activity interna.
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implementar autenticação
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
}
