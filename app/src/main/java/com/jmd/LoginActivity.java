package com.jmd;

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

public class LoginActivity extends AppCompatActivity {

    protected EditText  aliasEmail,
                        aliasSenha;

    protected Button btnLogin,
                     btnCadastro;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        aliasEmail = findViewById(R.id.editLoginEmail);
        aliasSenha = findViewById(R.id.editLoginSenha);

        btnLogin    = findViewById(R.id.btnLoginEntrar);
        btnCadastro = findViewById(R.id.btnLoginCadastre);

        auth = FirebaseAuth.getInstance();

        /**
         * Botão logar. Faz autenticação e encaminha para a activity interna.
         */
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implementar verificação dos campos
                //
                logar(aliasEmail.getText().toString(), aliasSenha.getText().toString());
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

    private void logar(String email, String senha) {
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // usuario logou
                    // user = auth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Login feito com sucesso", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getBaseContext(),BaseActivity.class);
                    startActivity(i);
                } else {
                    // problema no login
                    Log.d("FireAuth", task.getException().getMessage());
                }
            }
        });
    }
}
