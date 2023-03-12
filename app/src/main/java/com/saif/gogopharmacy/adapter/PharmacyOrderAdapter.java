package com.saif.gogopharmacy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saif.gogopharmacy.CustomerProductDetails;
import com.saif.gogopharmacy.R;
import com.saif.gogopharmacy.model.Order;
import com.saif.gogopharmacy.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PharmacyOrderAdapter extends RecyclerView.Adapter<PharmacyOrderAdapter.ViewHolder> {

    // reference for ArrayList and Context
    private ArrayList<Order> list;
    private Context context;

    // constructor that accepts tow Attributes
    // List and Context
    public PharmacyOrderAdapter(ArrayList<Order> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PharmacyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_pharmacy_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyOrderAdapter.ViewHolder holder, int position) {
        // Get the item from the list
        // and handled with order model
        Order order = list.get(position);
        String orderID = order.getOrder_id();
        String orderAmount = order.getTotal_amount();
        String order_state = order.getState();
        Long order_date = order.getTime_millis();

        holder.order_id.setText(orderID);
        holder.order_amount.setText(orderAmount);
        holder.order_state.setText(order_state);

        if (order_state.equals("preparing")) {
            holder.order_state.setTextColor(context.getResources().getColor(R.color.primary_200));
        } else if (order_state.equals("confirm")) {
            holder.order_state.setTextColor(context.getResources().getColor(R.color.confirmed_color));
        } else {
            holder.order_state.setTextColor(context.getResources().getColor(R.color.confirmed_color));
        }

        Date date = new Date(order_date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = formatter.format(date);
        holder.order_date.setText(formattedDate);


    }

    @Override
    public int getItemCount() {
        return list.size(); // return the size of the list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // UI reference
        private TextView order_id;
        private TextView order_amount;
        private TextView order_state;
        private TextView order_date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.tv_order_id_value_card);
            order_amount = itemView.findViewById(R.id.tv_order_amount_value_card);
            order_state = itemView.findViewById(R.id.tv_order_state_card);
            order_date = itemView.findViewById(R.id.tv_order_date_card);
        }
    }
}
