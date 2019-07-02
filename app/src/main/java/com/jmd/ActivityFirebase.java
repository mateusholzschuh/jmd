package com.jmd;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class ActivityFirebase extends AppCompatActivity {

    protected FirebaseAuth auth;
    protected FirebaseDatabase database;
    protected FirebaseStorage storage;

    protected ProgressBar progressBar;

    public void toggleProgressbar (boolean ativo) {
        if (progressBar != null) {
            progressBar.setVisibility(ativo ? View.VISIBLE : View.GONE);

        }
    }
}
