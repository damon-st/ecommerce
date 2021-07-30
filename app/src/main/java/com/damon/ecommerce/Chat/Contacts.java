package com.damon.ecommerce.Chat;

public class Contacts {


    public String name ,status,image,phone;

    public Contacts(){

    }

    public Contacts(String name, String status, String image,String phone) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
