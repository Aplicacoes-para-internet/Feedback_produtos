package br.com.feedbackprodutos.model;

import java.time.LocalDateTime;

public class Feedback {

    private int id;
    private Produto produto;
    private Usuario usuario;
    private int nota;
    private String comentario;
    private LocalDateTime dataEnvio;

    public Feedback() {
    }

    public Feedback(
            int id,
            Produto produto,
            Usuario usuario,
            int nota,
            String comentario,
            LocalDateTime dataEnvio
    ) {
        this.id = id;
        this.produto = produto;
        this.usuario = usuario;
        this.nota = nota;
        this.comentario = comentario;
        this.dataEnvio = dataEnvio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}