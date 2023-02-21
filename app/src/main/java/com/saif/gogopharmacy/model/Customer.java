package com.saif.gogopharmacy.model;

public class Customer
{
    private String userId;
    private String full_name;
    private String phone_number;
    private String email;
    private String complete_address;
    private String profile_image;
    private boolean online;
    private String account_type;
    private Long time_millis;

    public Customer() {
    }

    public Customer(String userId, String full_name, String phone_number, String email, String complete_address, String profile_image, boolean online, String account_type, Long time_millis) {
        this.userId = userId;
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.email = email;
        this.complete_address = complete_address;
        this.profile_image = profile_image;
        this.online = online;
        this.account_type = account_type;
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

    public String getComplete_address() {
        return complete_address;
    }

    public void setComplete_address(String complete_address) {
        this.complete_address = complete_address;
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

    public Long getTime_millis() {
        return time_millis;
    }

    public void setTime_millis(Long time_millis) {
        this.time_millis = time_millis;
    }
}
