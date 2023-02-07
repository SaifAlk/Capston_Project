package com.saif.gogopharmacy.model;

import android.net.Uri;

public class Pharmacy
{
    private String userId;
    private String full_name;
    private String shop_name;
    private String delivery_fee;
    private String phone_number;
    private String email;
    private String city;
    private String complete_address;
    private double latitude;
    private double longitude;
    private String profile_image;
    private boolean online;
    private String account_type;
    private String account_state;
    private Long time_millis;

    public Pharmacy() {
    }

    public Pharmacy(String userId, String full_name, String shop_name, String delivery_fee, String phone_number, String email, String city, String complete_address, double latitude, double longitude, String profile_image, boolean online, String account_type, String account_state, Long time_millis) {
        this.userId = userId;
        this.full_name = full_name;
        this.shop_name = shop_name;
        this.delivery_fee = delivery_fee;
        this.phone_number = phone_number;
        this.email = email;
        this.city = city;
        this.complete_address = complete_address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.profile_image = profile_image;
        this.online = online;
        this.account_type = account_type;
        this.account_state = account_state;
        this.time_millis = time_millis;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getComplete_address() {
        return complete_address;
    }

    public void setComplete_address(String complete_address) {
        this.complete_address = complete_address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getAccount_state() {
        return account_state;
    }

    public void setAccount_state(String account_state) {
        this.account_state = account_state;
    }

    public Long getTime_millis() {
        return time_millis;
    }

    public void setTime_millis(Long time_millis) {
        this.time_millis = time_millis;
    }
}
