package com.damon.ecommerce.Model;

public class Reportar {

    private String productID,sellerID,id,uidReport,reporte,comentario,key;

    public Reportar() {
    }

    public Reportar(String productID, String sellerID, String id, String uidReport,String reporte,String  comentario,String key) {
        this.productID = productID;
        this.sellerID = sellerID;
        this.id = id;
        this.uidReport = uidReport;
        this.reporte = reporte;
        this.comentario =comentario;
        this.key = key ;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUidReport() {
        return uidReport;
    }

    public void setUidReport(String uidReport) {
        this.uidReport = uidReport;
    }
}


