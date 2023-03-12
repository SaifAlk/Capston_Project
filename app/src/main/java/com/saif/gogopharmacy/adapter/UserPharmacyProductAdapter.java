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
import com.saif.gogopharmacy.PharmacyDetails;
import com.saif.gogopharmacy.R;
import com.saif.gogopharmacy.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserPharmacyProductAdapter extends RecyclerView.Adapter<UserPharmacyProductAdapter.ViewHolder> {

    // reference for ArrayList and Context
    private ArrayList<Product> list;
    private Context context;

    // constructor that accepts tow Attributes
    // List and Context
    public UserPharmacyProductAdapter(ArrayList<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserPharmacyProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_pharmacy_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPharmacyProductAdapter.ViewHolder holder, int position) {
        // Get the item from the list
        // and handled with transfer model
        Product product = list.get(position);
        String product_id = product.getUserId();
        String product_name = product.getProduct_name();
        String price = product.getPrice();
        String quantity = product.getQuantity();
        String final_price = product.getFinal_price();
        String product_image = product.getImage();
        String discount_price = product.getDiscount_price();

        holder.product_name.setText(product_name);
        holder.price.setText(price + "AED");
        holder.price_after_discount.setText(final_price + "AED");
        if(discount_price.length() > 0){
            holder.price_after_discount.setVisibility(View.VISIBLE);
            // add strike through on price
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.price_after_discount.setVisibility(View.INVISIBLE);
        }

        try { Picasso.get().load(product_image).placeholder(R.drawable.medicine).into(holder.product_image);
        } catch (Exception e) {
            holder.product_image.setImageResource(R.drawable.medicine);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CustomerProductDetails.class);
                intent.putExtra("product_id", product_id);
                intent.putExtra("product_name", product_name);
                context.startActivity(intent);

                // store the pharmacy id in SharedPreferences
                SharedPreferences preferences = context.getSharedPreferences("Pharmacy_Id",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putString("pharmacy_id",product_id);
                editor.apply();
            }
        });


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
        private TextView price_after_discount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_name = itemView.findViewById(R.id.tv_product_name_card);
            price = itemView.findViewById(R.id.tv_product_price_card);
            price_after_discount = itemView.findViewById(R.id.tv_product_price_after_discount_card);
            product_image = itemView.findViewById(R.id.img_product_card);
        }
    }
}
