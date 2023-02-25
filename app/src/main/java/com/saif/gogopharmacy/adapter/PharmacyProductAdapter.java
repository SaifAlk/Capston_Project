package com.saif.gogopharmacy.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saif.gogopharmacy.R;
import com.saif.gogopharmacy.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PharmacyProductAdapter extends RecyclerView.Adapter<PharmacyProductAdapter.ViewHolder> {

    // reference for ArrayList and Context
    private ArrayList<Product> list;
    private Context context;

    // constructor that accepts tow Attributes
    // List and Context
    public PharmacyProductAdapter(ArrayList<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PharmacyProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_pharmacy_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyProductAdapter.ViewHolder holder, int position) {
        // Get the item from the list
        // and handled with transfer model
        Product product = list.get(position);
        String user_id = product.getUserId();
        String product_name = product.getProduct_name();
        String price = product.getPrice();
        String discount_percent = product.getDiscount_total_percent();
        String final_price = product.getFinal_price();
        String product_image = product.getImage();

        holder.product_name.setText(product_name);
        holder.price.setText(price + "AED");
        holder.price_after_discount.setText(final_price + "AED");
        holder.discount_percent.setText(discount_percent);
        if(discount_percent.length() > 0){
            holder.discount_percent.setVisibility(View.VISIBLE);
            holder.price_after_discount.setVisibility(View.VISIBLE);
            // add strike through on price
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.discount_percent.setVisibility(View.INVISIBLE);
            holder.price_after_discount.setVisibility(View.INVISIBLE);
        }

        try { Picasso.get().load(product_image).placeholder(R.drawable.medicine).into(holder.product_image);
        } catch (Exception e) {
            holder.product_image.setImageResource(R.drawable.medicine);
        }


    }

    @Override
    public int getItemCount() {
        return list.size(); // return the size of the list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // UI reference
        private ImageView product_image;
        private TextView product_name;
        private TextView price;
        private TextView discount_percent;
        private TextView price_after_discount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_name = itemView.findViewById(R.id.tv_product_name_card);
            price = itemView.findViewById(R.id.tv_product_price_card);
            discount_percent = itemView.findViewById(R.id.tv_product_discount_card);
            price_after_discount = itemView.findViewById(R.id.tv_product_price_after_discount_card);
            product_image = itemView.findViewById(R.id.img_product_card);
        }
    }
}
