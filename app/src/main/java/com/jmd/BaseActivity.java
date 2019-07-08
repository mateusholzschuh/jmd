package com.jmd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jmd.fragments.ListarPromocoesFragment;
import com.jmd.fragments.ManterPromocaoFragment;
import com.jmd.modelo.Mercado;

public class BaseActivity extends ActivityFirebase
        implements NavigationView.OnNavigationItemSelectedListener {

    protected TextView name, email;

    protected FrameLayout container;
    Mercado user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        container = findViewById(R.id.base_container);
        progressBar = findViewById(R.id.progressBarBase);
        toggleProgressbar(false);


        /**
         * Firebase instances
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final String uuid = auth.getCurrentUser().getUid();
        final String uemail = auth.getCurrentUser().getEmail();

        database.getReference("mercados").child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Mercado.class);
                user.setUuid(uuid);
                user.setEmail(uemail);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                auth.signOut();
                finish();
            }
        });


        /**
         * FAB
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /**
         * Navigation Drawer
         */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        name  = headerView.findViewById(R.id.nav_name);
        email = headerView.findViewById(R.id.nav_email);

        /**
         * Abre a lista de promoções do mercado
         */
        getSupportFragmentManager().beginTransaction()
                .replace(container.getId(), new ListarPromocoesFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // se o navigation drawer ta aberto, entao fecha
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        // se tiver algum fragment aberto e que não seja o da lista, infla o fragment do listar
        } else if (getSupportFragmentManager().getFragments().get(0) != null &&
                ListarPromocoesFragment.class != getSupportFragmentManager().getFragments().get(0).getClass()) {
            // troca o fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(container.getId(), new ListarPromocoesFragment())
                    .commit();

        // caso contrario, fecha o app
        } else {
//            String s = getSupportFragmentManager().getFragments().get(0).getClass().getSimpleName();
//            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_promo) {
            getSupportFragmentManager().beginTransaction()
                    .replace(container.getId(), new ManterPromocaoFragment())
                    .commit();

        } else if (id == R.id.nav_promocoes) {
            getSupportFragmentManager().beginTransaction()
                    .replace(container.getId(), new ListarPromocoesFragment())
                    .commit();

        } else if (id == R.id.nav_logout) {
            if (auth.getCurrentUser() != null) {
                // firebase signout
                auth.signOut();

                // open login activity
                startActivity(new Intent(this, LoginActivity.class));

                // close this activity
                finish();
                Toast.makeText(this, "Logout efetuado com sucesso", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUI(Mercado user) {
        name.setText(user.getNome());
        email.setText(user.getEmail());
    }
}
