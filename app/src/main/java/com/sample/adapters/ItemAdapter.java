package com.sample.adapters;

/*
 * Created by mohit on 27/04/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sample.R;
import com.sample.modals.ItemPOJO;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private ArrayList<ItemPOJO> itemList;
    private Context ctx;

    public ItemAdapter(Context ctx, ArrayList<ItemPOJO> itemList) {
        this.itemList = itemList;
        this.ctx = ctx;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvDes)
        TextView tvDes;

        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.tvCost)
        TextView tvCost;

        @BindView(R.id.imgView)
        ImageView imgView;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(ctx.getString(R.string.name) + ": " + itemList.get(position).getName());
        holder.tvDes.setText(ctx.getString(R.string.des) + ": " + itemList.get(position).getDescription());
        holder.tvLocation.setText(ctx.getString(R.string.location) + ": " + itemList.get(position).getLocation());
        holder.tvCost.setText(ctx.getString(R.string.cost) + ": " + itemList.get(position).getCost());

        Glide.with(ctx).load(itemList.get(position).getImage()).placeholder(R.drawable.broken_image)
                .error(R.drawable.broken_image).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
