package io.vamshedhar.recipepuppy;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/30/17 8:01 PM.
 * vchinta1@uncc.edu
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CpuUsageInfo;
import android.os.TestLooperManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Anjani Reddy on 23-10-2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>  {
    ArrayList<DishRecipe> mData;
    Context mContext;

    public RecipeAdapter(ArrayList<DishRecipe> data, Context mContext)
    {
        this.mData = data;
        this.mContext = mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public TextView mTextViewname,mTextViewIngr,mTextViewUrl;
        public ImageView imageView;
        private Context mCon;

        public ViewHolder(View v) {
            super(v);
            mTextViewname = v.findViewById(R.id.dishTitle);
            mTextViewIngr = v.findViewById(R.id.ingredientsList);
            mTextViewUrl = v.findViewById(R.id.recipeURL);
            imageView = v.findViewById(R.id.dishImage);

        }


    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        DishRecipe Recipie = mData.get(position);

        holder.mTextViewname.setText(Recipie.getDishTitle());
        holder.mTextViewIngr.setText(Recipie.getDishIngredients());
        holder.mTextViewUrl.setText(Recipie.getDishURL());
        if (!Recipie.getImageURL().equals("")){
            Picasso.with(this.mContext).load(Recipie.getImageURL()).error(R.drawable.no_image).into(holder.imageView);
        } else{
            holder.imageView.setImageResource(R.drawable.no_image);
        }

        holder.mTextViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView urlView = (TextView) view;

                String url = urlView.getText().toString().trim();

                if(!url.equals("")){
                    Log.d(MainActivity.TAG, url);

                    if (!url.startsWith("http://") && !url.startsWith("https://")){
                        url = "http://" + url;
                    }

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(browserIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
