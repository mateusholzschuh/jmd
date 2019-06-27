package com.jmd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
}
