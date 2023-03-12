package com.saif.gogopharmacy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saif.gogopharmacy.R;
import com.saif.gogopharmacy.model.Cart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerCartOrdersAdapter extends RecyclerView.Adapter<CustomerCartOrdersAdapter.ViewHolder> {

    // reference for ArrayList and Context
    private ArrayList<Cart> list;
    private Context context;

    // constructor that accepts tow Attributes
    // List and Context
    public CustomerCartOrdersAdapter(ArrayList<Cart> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerCartOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerCartOrdersAdapter.ViewHolder holder, int position) {
        // Get the item from the list
        // and handled with cart model
        Cart cart = list.get(position);
        String pharmacy_id = cart.getPharmacy_id();
        String product_image = cart.getImage();
        String product_name = cart.getProduct_name();
        String price = cart.getPrice();
        String quantity = cart.getQuantity();

        holder.product_name.setText(product_name);
        holder.price.setText(price);
        holder.quantity.setText(quantity);
        try { Picasso.get().load(product_image).placeholder(R.drawable.medicine).into(holder.product_image);
        } catch (Exception e) {
            holder.product_image.setImageResource(R.drawable.medicine);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reference.child("Cart")
                        .child(holder.firebaseAuth.getUid())
                        .child(pharmacy_id)
                        .child(product_name)
                        .removeValue();
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
        private TextView quantity;
        private ImageButton delete;

        // Firebase reference
        private FirebaseAuth firebaseAuth;
        private FirebaseDatabase firebaseDatabase;
        private DatabaseReference reference;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_image = itemView.findViewById(R.id.img_product_card);
            product_name = itemView.findViewById(R.id.tv_product_name_card);
            price = itemView.findViewById(R.id.tv_product_price_card);
            quantity = itemView.findViewById(R.id.tv_product_qty_count);
            delete = itemView.findViewById(R.id.btn_Delete);

            // initiate firebase auth
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference();

        }
    }
}
