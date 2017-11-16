package com.happymeal.tupapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.util.ArrayList;


public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<Product> products = new ArrayList<>();


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, price;
        public ImageView thumbnail, overflow;
        ArrayList<Product> products = new ArrayList<>();
        Context mContext;

        public MyViewHolder(View view, Context mContext , ArrayList<Product> products) {
            super(view);
            this.products = products;
            this.mContext = mContext;
            view.setOnClickListener(this);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.title);
            price = (TextView) view.findViewById(R.id.location);
        }

        @Override
        public void onClick(View v){
            int position = getAdapterPosition();
            Product product = this.products.get(position);
            Intent i = new Intent(this.mContext, MyItemDetails.class);
            FileCacher<String> profilecache = new FileCacher<>(mContext,"profile.txt");
            String string="";
            String[] f2;

            try {
                string = profilecache.readCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
            f2 = string.split(",");
            // i.putExtra("clicked_img", touristSpots.getTs_thumbnail_id());
            i.putExtra("id", product.getId());
            i.putExtra("name", product.getItem());
            i.putExtra("seller", product.getSeller());
            i.putExtra("price", product.getPrice());
            i.putExtra("category", product.getCategory());
            i.putExtra("subcategory", product.getSubcategory());
            i.putExtra("contact", product.getContact());
            i.putExtra("status", product.getStatus());
            i.putExtra("description", product.getDescription());
            i.putExtra("username", f2[0]);

            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            Boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            if(connected) {
                this.mContext.startActivity(i);
            }else{
                Toast.makeText(mContext,"Lost internet connection!",Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(mContext, "Emergency Hotlines", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(this.mContext, ProductDetails.class);
//            this.mContext.startActivity(i);
        }

    }

    public MyItemAdapter(Context mContext, ArrayList<Product> products) {
        this.mContext = mContext;
        this.products = products;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ecscard, parent, false);
        MyViewHolder prviewholder = new MyViewHolder(itemView, mContext, products);
        return prviewholder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Product product = products.get(position);
        //holder.thumbnail.setImageResource(touristSpots.getTs_thumbnail_id());
        //holder.thumbnail.setImageBitmap(product.getImage());
        holder.thumbnail.setImageResource(R.drawable.bgpic);
        holder.name.setText(product.getItem());
        holder.price.setText(product.getPrice());
        //holder.count.setText(touristSpots.getNumOftTouristSpots() + " tourist spots");

        // loading album cover using Glide library
        //Glide.with(mContext).load(touristSpots.getTs_thumbnail_id()).into(holder.thumbnail);


    }



    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setFilter(ArrayList<Product> newList){
        products = new ArrayList<>();
        products.addAll(newList);
        notifyDataSetChanged();
    }

}
