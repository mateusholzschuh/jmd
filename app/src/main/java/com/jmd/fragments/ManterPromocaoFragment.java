package com.jmd.fragments;


import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jmd.BaseActivity;
import com.jmd.R;
import com.jmd.modelo.Promocao;
import com.jmd.persistencia.PromocaoDAO;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManterPromocaoFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    protected ImageView aliasImage;
    protected EditText  aliasNome,
                        aliasPreco,
                        aliasValidade,
                        aliasDescricao;
    protected Button      btnButton;
    protected ImageButton btnData;
    protected ProgressBar progressIMG;

    boolean ALTERAR_FOTO = false;

    Calendar myCalendar = Calendar.getInstance();
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference ref;
    PromocaoDAO dao;
    Promocao promocao;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    public ManterPromocaoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (ref != null) {
            ref.removeEventListener(recuperaPromo());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manter_promocao, container, false);

        /**
         * Activity title
         */
        getActivity().setTitle("Manter promoção");

        /**
         * Activity FAB
         */
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_save_white_24dp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // salva promo
                salvarPromocao();
            }
        });
        fab.setOnLongClickListener(null);

        /**
         * Campos do fragment
         */
        aliasImage     = v.findViewById(R.id.imageManterProduto);
        aliasNome      = v.findViewById(R.id.editManterNome);
        aliasPreco     = v.findViewById(R.id.editManterPreco);
        aliasValidade  = v.findViewById(R.id.editManterValidade);
        aliasDescricao = v.findViewById(R.id.editManterDescricao);

        btnButton = v.findViewById(R.id.btnManterSalvar);
        btnData   = v.findViewById(R.id.btnManterDate);

        progressIMG = v.findViewById(R.id.progressBarManter);

        ALTERAR_FOTO = false;

        /**
         * Firebase
         */
        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        /**
         * Verifica se vei algum parametro
         */
        Bundle bundle = getArguments();

        if (bundle != null && bundle.getString("PROMO_UUID", null) != null)
        {
            ((BaseActivity) getActivity()).toggleProgressbar(true);
            dao = PromocaoDAO.getInstance();

            dao.buscar(bundle.getString("PROMO_UUID"), recuperaPromo());
//            ref = FirebaseDatabase.getInstance().getReference("promocoes");
//            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .child(bundle.getString("PROMO_UUID")).addValueEventListener(recuperaPromo());
        } else {
            promocao = null;
        }

        /**
         * Image chooser
         */
        aliasImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        /**
         * EditText da data, quando interagir no botão abrir o DatePicker
         */
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // converte a data
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

                        // atualiza o campo
                        aliasValidade.setText(sdf.format(myCalendar.getTime()));
                    }
                },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        /**
         * Botão de salvar
         */
        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarPromocao();
            }
        });
        
        // Inflate the layout for this fragment
        return v;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(getContext()).load(mImageUri)
                    .resize(400,400)
                    .centerCrop()
                    .into(aliasImage);
            ALTERAR_FOTO = true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        if (mImageUri != null) {
            final String filename = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
            final StorageReference fileReference = mStorageRef.child(filename);

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(listener)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext() , e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressIMG.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private ValueEventListener recuperaPromo() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                promocao = dataSnapshot.getValue(Promocao.class);
                promocao.setUid(dataSnapshot.getKey());

                if (promocao !=null) {
                    atualizaCampos(promocao);
                }

                if (promocao.getImagem() != null && !promocao.getImagem().isEmpty())
                    Picasso.with(getContext()).load(promocao.getImagem())
                        .resize(400,400)
                        .centerCrop()
                        .into(aliasImage);

                // TODO: WTF? COMO REMOVER DEPOIS DE PEGAR OS DADOS
                if(((BaseActivity) getActivity()) != null)
                    ((BaseActivity) getActivity()).toggleProgressbar(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Ops! Ocorreu um erro! " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                if(((BaseActivity) getActivity()) != null)
                    ((BaseActivity) getActivity()).toggleProgressbar(false);
            }
        };
    }

    private Date formataData(String string) {
        try {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));
            return sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void salvarPromocao () {
        // TODO: fazer verificação/validação dos campos!
        String nome, descricao, validade, preco;

        nome      = aliasNome.getText().toString();
        descricao = aliasDescricao.getText().toString();
        validade  = aliasValidade.getText().toString();
        preco     = aliasPreco.getText().toString();

        // verifica campos
        if (nome == null   || validade == null   || preco == null ||
                nome.isEmpty() || validade.isEmpty() || preco.isEmpty())
        {
            Toast.makeText(getContext(), "Há campos obrigatórios não preenchidos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (promocao == null)
            promocao = new Promocao();

        promocao.setNome(nome);
        promocao.setPreco(Float.valueOf(preco));
        promocao.setDescricao(descricao);
        promocao.setValidade(validade);

        ((BaseActivity) getActivity()).toggleProgressbar(true);

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference refPromo = database.getReference("promocoes");
//        refPromo = refPromo.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

//        String key = refPromo.push().getKey();
//
//        if (promocao.getUid() != null)
//            key = promocao.getUid();
//        refPromo.child(key).setValue(promocao).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    limparCampos();
//                    Toast.makeText(getContext(), "Promoção salva com sucesso!", Toast.LENGTH_SHORT).show();
//
//                    // abre a listagem de ofertas
//                    getActivity().getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.base_container, new ListarPromocoesFragment())
//                            .commit();
//                } else {
//                    Toast.makeText(getContext(), "Ops! Houve um erro ao salvar. " + task.getException(), Toast.LENGTH_SHORT).show();
//                }
//                ((BaseActivity) getActivity()).toggleProgressbar(false);
//            }
//        });
        final PromocaoDAO pdao = PromocaoDAO.getInstance();

        if(ALTERAR_FOTO) {
            // DELETA ANTIGA FOTO
            if (promocao.getImagem() != null && !promocao.getImagem().isEmpty()) {
                StorageReference imageRef = mStorageRef.getStorage().getReferenceFromUrl(promocao.getImagem());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        promocao.setImagem(null);
                    }
                });
            }

            uploadFile(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Upload completo", Toast.LENGTH_SHORT).show();
                    //taskSnapshot.getDownloadUrl();
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            promocao.setImagem(uri.toString());

                            pdao.salvar(promocao, getActivity(), new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        // ok
                                        limparCampos();
                                        Toast.makeText(getContext(), "Promoção salva com sucesso", Toast.LENGTH_SHORT).show();

                                        // vai para listagem
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.base_container, new ListarPromocoesFragment())
                                                .commit();

                                    } else {
                                        Toast.makeText(getContext(), "Ops! Houve um erro ao salvar", Toast.LENGTH_SHORT).show();
                                    }
                                    // desativa o progress bar
                                    ((BaseActivity) getActivity()).toggleProgressbar(false);
                                }
                            });
                        }
                    });
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue("a");
                }
            });
        }
        else {
            // so altera dados convencionais
            pdao.salvar(promocao, getActivity(), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // ok
                        limparCampos();
                        Toast.makeText(getContext(), "Promoção salva com sucesso", Toast.LENGTH_SHORT).show();

                        // vai para listagem
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.base_container, new ListarPromocoesFragment())
                                .commit();

                    } else {
                        Toast.makeText(getContext(), "Ops! Houve um erro ao salvar", Toast.LENGTH_SHORT).show();
                    }
                    // desativa o progress bar
                    ((BaseActivity) getActivity()).toggleProgressbar(false);
                }
            });
        }

    }

    private void limparCampos () {
        aliasNome.setText("");
        aliasValidade.setText("");
        aliasDescricao.setText("");
        aliasPreco.setText("");
    }

    private void atualizaCampos (Promocao p) {
        aliasNome.setText(p.getNome());
        aliasValidade.setText(p.getValidade());
        aliasDescricao.setText(p.getDescricao());
        aliasPreco.setText(p.getPreco().toString());

        myCalendar.setTime(formataData(p.getValidade()));
    }

}
