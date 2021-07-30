package com.damon.ecommerce.Model;

public class Sellers {

    private String address,email,name,phone,uid,image,sid;

    public Sellers() {
    }

    public Sellers(String address, String email, String name, String phone, String uid, String image,String sid) {
        this.address = address;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.uid = uid;
        this.image = image;
        this.sid = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
