package com.jmd;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.jmd.modelo.Local;
import com.jmd.modelo.Mercado;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CadastrarActivity extends ActivityFirebase implements LocationListener{

    protected EditText aliasNome,
            aliasEmail,
            aliasSenha,
            aliasTelefone,
            aliasEndereco,
            aliasGPS;

    protected Button aliasBtnBuscarGPS,
            aliasBtnRegistrar,
            aliasBtnVoltar;

    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        /**
         * GPS permissões
         */
        startGettingLocations();

        /**
         * Campos da activity
         */
        aliasNome = findViewById(R.id.editCadastroNome);
        aliasEmail = findViewById(R.id.editCadastroEmail);
        aliasSenha = findViewById(R.id.editCadastroSenha);
        aliasTelefone = findViewById(R.id.editCadastroTelefone);
        aliasEndereco = findViewById(R.id.editCadastroEndereco);
        aliasGPS = findViewById(R.id.editCadastroGPS);

        aliasBtnBuscarGPS = findViewById(R.id.btnCadastroBuscarGPS);
        aliasBtnRegistrar = findViewById(R.id.btnCadastroCadastrar);
        aliasBtnVoltar = findViewById(R.id.btnCadastroVoltar);

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

                nome = aliasNome.getText().toString();
                email = aliasEmail.getText().toString();
                senha = aliasSenha.getText().toString();
                gps = aliasGPS.getText().toString();
                endereco = aliasEndereco.getText().toString();
                telefone = aliasTelefone.getText().toString();

                if (nome == null || email == null || senha == null ||
                        gps == null || endereco == null || telefone == null ||
                        nome.equals("") || email.equals("") || senha.equals("") || gps.equals("") ||
                        endereco.equals("") || telefone.equals("")) {
                    Toast.makeText(CadastrarActivity.this, "Todos os campos são OBRIGATÓRIOS", Toast.LENGTH_SHORT).show();
                    return;
                }

                // mostra progressbar
                progressBar.setVisibility(View.VISIBLE);

                // tenta cadastrar
                cadastrarUsuario(email, senha, nome, endereco, telefone, mLocation);
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

                // TODO: Achar a maneira ideal
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(CadastrarActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(CadastrarActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, (LocationListener) CadastrarActivity.this);

                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(bestProvider);

                if (location == null) {
                    Toast.makeText(getApplicationContext(), "Sinal GPS não encontrado!", Toast.LENGTH_SHORT).show();
                }
                if (location != null) {
                    Log.e("locatin", "location--" + location);

                    Log.e("latitude at beginning",
                            "@@@@@@@@@@@@@@@" + location.getLatitude());
//                    onLocationChanged(location);
                    mLocation = location;
                }


                if (mLocation != null) {
                    aliasGPS.setText(mLocation.getLatitude() + ", " + mLocation.getLongitude());
                    preencheEndereco();
                }
                // da um tempo pra animação
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);

                    }
                }, 2500);
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
                                  final String endereco, final String telefone, final Location gps) {
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
                    mercado.setLocal(new Local(gps));

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

    private void preencheEndereco() {
        if (mLocation == null) return;

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        double latitude = mLocation.getLatitude();
        double longitude = mLocation.getLongitude();

        Log.e("latitude", "latitude--" + latitude);
        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                //aliasEndereco.setText(state + " , " + city + " , " + country);
                aliasEndereco.setText(address + " , " + knownName + " , " + postalCode);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // codigo para copiar e colar em toda aplicação
    // é um chamado para liberar as permissões do usuário.
    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;// Distance in meters
        long MIN_TIME_BW_UPDATES = 100 * 10;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions

            // check permissions for later versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }


        //Checks if FINE LOCATION and COARSE Location were granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }
        } else {
            Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS desativado!");
        alertDialog.setMessage("Ativar GPS?");
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
