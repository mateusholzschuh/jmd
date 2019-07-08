package com.jmd.modelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Promocao implements Comparable<Promocao> {

    private String uid;
    private String nome;
    private String descricao;
    private String imagem;
    private Float  preco;
    private Long validade;
    private Long timestamp;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    private String mercadoUID;

    public String getMercadoUID() {
        return mercadoUID;
    }

    public void setMercadoUID(String mercadoUID) {
        this.mercadoUID = mercadoUID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }

    public Long getValidade() {
        return validade;
    }

    public void setValidade(Long validade) {
        this.validade = validade;
    }

    @Override
    public int compareTo(Promocao o) {
        return (int) (o.getTimestamp()-this.getTimestamp());
    }
}
