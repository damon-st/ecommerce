package com.damon.ecommerce.Model;

public class Calificaciones {
    private String sellerID;
    private String userComment;
    private String productID;
    private String comentario;
    private float puntuacion;
    private String estado;

    public Calificaciones() {
    }

    public Calificaciones(String sellerID, String userComment, String productID, String comentario, float puntuacion, String estado) {
        this.sellerID = sellerID;
        this.userComment = userComment;
        this.productID = productID;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.estado = estado;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
