package io.vamshedhar.itunes;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/23/17 7:30 PM.
 * vchinta1@uncc.edu
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FilteredAppAdapter extends RecyclerView.Adapter<FilteredAppAdapter.ViewHolder> {
    ArrayList<TopApp> filteredApps;
    Context context;
    DatabaseDataManager dbManager;
    IDataAdapter activity;

    public interface IDataAdapter{
        void updateData(String id);
    }

    public FilteredAppAdapter(ArrayList<TopApp> filteredApps, Context context, DatabaseDataManager dbManager, IDataAdapter activity) {
        this.filteredApps = filteredApps;
        this.context = context;
        this.activity = activity;
        this.dbManager = dbManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_app_item_rel, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TopApp topApp = filteredApps.get(position);
        holder.filteredApp = topApp;

        holder.textViewName.setText(topApp.name);
        holder.textViewPrice.setText("Price: USD " + topApp.price);

        holder.imageViewDelete.setTag(topApp.id);

        double price = Double.parseDouble(topApp.price);

        if (!topApp.imageUrl.equals("")){
            Picasso.with(context)
                    .load(topApp.imageUrl)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.no_image)
                    .into(holder.imageViewImage);
        } else {
            holder.imageViewImage.setImageResource(R.drawable.no_image);
        }

        if (price <= 1.99){
            holder.imageViewPrice.setImageResource(R.drawable.price_low);
        } else if (price > 5.99){
            holder.imageViewPrice.setImageResource(R.drawable.price_high);
        } else {
            holder.imageViewPrice.setImageResource(R.drawable.price_medium);
        }

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView delImage = (ImageView) view;

                Log.d(MainActivity.TAG, "Clicked");

                String id = (String) delImage.getTag();
                activity.updateData(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredApps.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPrice;
        ImageView imageViewImage, imageViewPrice, imageViewDelete;

        TopApp filteredApp;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
            imageViewPrice = (ImageView) itemView.findViewById(R.id.imageViewPrice);
            imageViewImage = (ImageView) itemView.findViewById(R.id.imageViewFilteredApp);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("demo", email.sender);

                }
            });
        }
    }
}