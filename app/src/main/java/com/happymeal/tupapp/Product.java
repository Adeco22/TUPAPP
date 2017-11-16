package com.happymeal.tupapp;

public class Product {
        private String item;
        private String contact;
        private String description;
        private String id;
        private String category;
        private String subcategory;
        private String price;
        private String seller;
        private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        category = category.replace("%20"," ");
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        subcategory = subcategory.replace("%20"," ");
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        description = description.replace("%20"," ");
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeller() {
        seller = seller.replace("%20"," ");
        return seller;
    }

    public void setSeller(String seller) {

        this.seller = seller;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Product(String id,String item,String category ,String subcategory , String contact, String price,String description, String seller, String status) {

        this.id = id;
        this.item = item;
        this.category = category;
        this.subcategory = subcategory;
        this.contact = contact;
        this.price = price;
        this.description = description;
        this.seller = seller;
        this.status = status;
    }
}
