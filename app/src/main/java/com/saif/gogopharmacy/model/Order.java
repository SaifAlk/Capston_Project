package com.saif.gogopharmacy.model;

public class Order {
    private String order_id;
    private String order_by;
    private String order_to;
    private String total_amount;
    private String state;
    private Long time_millis;

    public Order() {
    }

    public Order(String order_id, String order_by, String order_to, String total_amount, String state, Long time_millis) {
        this.order_id = order_id;
        this.order_by = order_by;
        this.order_to = order_to;
        this.total_amount = total_amount;
        this.state = state;
        this.time_millis = time_millis;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getOrder_to() {
        return order_to;
    }

    public void setOrder_to(String order_to) {
        this.order_to = order_to;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getTime_millis() {
        return time_millis;
    }

    public void setTime_millis(Long time_millis) {
        this.time_millis = time_millis;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", order_by='" + order_by + '\'' +
                ", order_to='" + order_to + '\'' +
                ", total_amount='" + total_amount + '\'' +
                ", state='" + state + '\'' +
                ", time_millis=" + time_millis +
                '}';
    }
}