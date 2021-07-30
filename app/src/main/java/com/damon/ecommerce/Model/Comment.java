package com.damon.ecommerce.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String commentid;
    private String sellerid;
    private String postid;
    private String respuesta;
    private String uidVendedor;

    public Comment(String comment, String publisher, String commentid,String sellerid,String postid,String respuesta,String uidVendedor) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
        this.sellerid = sellerid;
        this.postid = postid;
        this.respuesta = respuesta;
        this.uidVendedor = uidVendedor;
    }

    public String getUidVendedor() {
        return uidVendedor;
    }

    public void setUidVendedor(String uidVendedor) {
        this.uidVendedor = uidVendedor;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}
