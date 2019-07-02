package com.jmd.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jmd.R;
import com.jmd.modelo.Promocao;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PromocaoAdapter extends RecyclerView.Adapter<PromocaoAdapter.PromocoesViewHolder> {
    private final List<Promocao> promocoes;
    private final Context context;
    private final PromocaoOnClickListener onClickListener;

    public interface PromocaoOnClickListener {
        void onClickPromocao(PromocoesViewHolder holder, int idx);
    }

    public PromocaoAdapter(Context context, List<Promocao> promocoes, PromocaoOnClickListener onClickListener) {
        this.context = context;
        this.promocoes = promocoes;
        this.onClickListener = onClickListener;
    }

    @Override
    public PromocoesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Este método cria uma subclasse de RecyclerView.ViewHolder
        // Infla a view do layout
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_listar_promocoes, viewGroup, false);
        // Cria a classe do ViewHolder
        PromocoesViewHolder holder = new PromocoesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PromocoesViewHolder holder, final int position) {
        // Este método recebe o índice do elemento, e atualiza as views que estão dentro do ViewHolder
        Promocao c = promocoes.get(position);
        // Atualizada os valores nas views
        holder.tNome.setText(c.getNome());
        holder.tPreco.setText("R$ " + c.getPreco().toString());
        holder.tValidade.setText("VALIDA ATÉ:\n" + c.getValidade());

//        TODO: IMPLEMENTAR COM FIREBASE STORAGE !!!
//        if (c.getFoto()!= null) {
//            //converte byte[] para Bitmap
//            Bitmap bitmap = BitmapFactory.decodeByteArray(c.getFoto(), 0, c.getFoto().length);
//            //carrega a imagem na ImageView do item da ListView
//            holder.img.setImageBitmap(bitmap);
//        } else {
//            //carrega a imagem padrão (se não houver imagem no Cursor)
//            holder.img.setImageResource(R.mipmap.ic_launcher);
//        }
//        // holder.img.setImageResource(c.foto);
        if (c.getImagem() != null && !c.getImagem().isEmpty()) {
            Picasso.with(holder.view.getContext()).load(c.getImagem())
                    .resize(400, 400)
                    .centerCrop()
                    .into(holder.img);
        }
        else {
            holder.img.setImageResource(R.mipmap.ic_launcher);
        }

        // Click
        if (onClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Chama o listener para informar que clicou no Cachorro
                    onClickListener.onClickPromocao(holder, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.promocoes != null ? this.promocoes.size() : 0;
    }

    // Subclasse de RecyclerView.ViewHolder. Contém todas as views.
    public static class PromocoesViewHolder extends RecyclerView.ViewHolder {
        public TextView tNome;
        public TextView tPreco;
        public TextView tValidade;
        ImageView img;
        ImageView imgStatus;
//        ProgressBar progress;
        private View view;

        public PromocoesViewHolder(View view) {
            super(view);
            this.view = view;

            // Cria as views para salvar no ViewHolder
            tNome       = view.findViewById(R.id.txtNomeAdapterList);
            tPreco      = view.findViewById(R.id.txtPrecoAdapterList);
            tValidade   = view.findViewById(R.id.txtValidadeAdapterList);
            img         = view.findViewById(R.id.imgAdapterList);
            imgStatus   = view.findViewById(R.id.imgStatusAdapterList);

//            progress = (ProgressBar) view.findViewById(R.id.progress);
        }
    }
}
