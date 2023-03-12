package com.saif.gogopharmacy.model;

public class Cart {

    private String product_name;
    private String price;
    private String image;
    private String quantity;
    private String pharmacy_id;

    public Cart() {
    }

    public Cart(String product_name, String price, String image, String quantity, String pharmacy_id) {
        this.product_name = product_name;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
        this.pharmacy_id = pharmacy_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "product_name='" + product_name + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", quantity='" + quantity + '\'' +
                ", product_id='" + pharmacy_id + '\'' +
                '}';
    }
}
