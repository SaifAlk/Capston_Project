package com.saif.gogopharmacy.model;

public class Product
{
    private String userId;
    private String product_name;
    private String product_description;
    private String category;
    private String price;
    private String discount_price;
    private String Quantity;
    private String final_price;
    private String image;
    private Long time_millis;

    public Product() {
    }

    public Product(String userId, String product_name, String product_description, String category, String price, String discount_price, String Quantity, String final_price, String image, Long time_millis) {
        this.userId = userId;
        this.product_name = product_name;
        this.product_description = product_description;
        this.category = category;
        this.price = price;
        this.discount_price = discount_price;
        this.Quantity = Quantity;
        this.final_price = final_price;
        this.image = image;
        this.time_millis = time_millis;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getFinal_price() {
        return final_price;
    }

    public void setFinal_price(String final_price) {
        this.final_price = final_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getTime_millis() {
        return time_millis;
    }

    public void setTime_millis(Long time_millis) {
        this.time_millis = time_millis;
    }
}

