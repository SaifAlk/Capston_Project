package com.saif.gogopharmacy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saif.gogopharmacy.PharmacyDetails;
import com.saif.gogopharmacy.R;
import com.saif.gogopharmacy.model.Pharmacy;
import com.saif.gogopharmacy.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowPharmacyAdapter extends RecyclerView.Adapter<ShowPharmacyAdapter.ViewHolder> {

    // reference for ArrayList and Context
    private ArrayList<Pharmacy> list;
    private Context context;

    // constructor that accepts tow Attributes
    // List and Context
    public ShowPharmacyAdapter(ArrayList<Pharmacy> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowPharmacyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_show_pharmacy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowPharmacyAdapter.ViewHolder holder, int position) {
        // Get the item from the list
        // and handled with transfer model
        Pharmacy pharmacy = list.get(position);
        String user_id = pharmacy.getUserId();
        String pharmacy_image = pharmacy.getProfile_image();
        String pharmacy_name = pharmacy.getShop_name();
        String delivery_fee = pharmacy.getDelivery_fee();

        holder.pharmacy_name.setText(pharmacy_name);
        holder.delivery_fee.setText(delivery_fee);
        try{
            Picasso.get()
                    .load(pharmacy_image)
                    .placeholder(R.drawable.medicine)
                    .into(holder.pharmacy_image);
        } catch (Exception e) {
            holder.pharmacy_image.setImageResource(R.drawable.medicine);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // navigate to PharmacyDetails page and send the pharmacyId
                Intent intent = new Intent(context, PharmacyDetails.class);
                intent.putExtra("pharmacy_id", user_id);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size(); // return the size of the list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // UI reference
        private ImageView pharmacy_image;
        private TextView pharmacy_name;
        private TextView delivery_fee;
        private RatingBar pharmacy_rating;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pharmacy_image = itemView.findViewById(R.id.img_pharmacy_card);
            pharmacy_name = itemView.findViewById(R.id.tv_pharmacy_name_card);
            delivery_fee = itemView.findViewById(R.id.tv_pharmacy_delivery_fee);
            pharmacy_rating = itemView.findViewById(R.id.rb_pharmacy);
        }
    }
}
