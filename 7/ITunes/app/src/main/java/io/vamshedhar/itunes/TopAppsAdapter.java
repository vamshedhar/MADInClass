package io.vamshedhar.itunes;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/13/17 3:25 PM.
 * vchinta1@uncc.edu
 */


public class TopAppsAdapter extends ArrayAdapter<TopApp> {
    Context context;
    ArrayList<TopApp> topApps;

    public TopAppsAdapter(Context context, ArrayList<TopApp> objects) {
        super(context, R.layout.top_apps_item, objects);
        this.context = context;
        this.topApps = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.top_apps_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.appTitle);
            holder.price = (TextView) convertView.findViewById(R.id.appPrice);
            holder.thumbnil = (ImageView) convertView.findViewById(R.id.appThumbnil);
            holder.priceRating = (ImageView) convertView.findViewById(R.id.priceRange);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        TopApp topApp = topApps.get(position);

        holder.title.setText(topApp.getName());
        holder.price.setText("$" +topApp.price);

        if (!topApp.thumbUrl.equals("")){
            Picasso.with(context)
                    .load(topApp.thumbUrl)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.no_image)
                    .into(holder.thumbnil);
        } else {
            holder.thumbnil.setImageResource(R.drawable.no_image);
        }

        Double price = Double.parseDouble(topApp.price);

        if (price <= 1.99){
            holder.priceRating.setImageResource(R.drawable.price_low);
        } else if (price > 5.99){
            holder.priceRating.setImageResource(R.drawable.price_high);
        } else {
            holder.priceRating.setImageResource(R.drawable.price_medium);
        }

//        holder.favImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences pref = context.getSharedPreferences("MovieSearch", Context.MODE_PRIVATE);
//
//                Gson gson = new Gson();
//                String json = pref.getString(MainActivity.FAVORITE_LIST, null);
//                Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
//                ArrayList<Movie> favMovies = gson.fromJson(json, type);
//
//                ImageView favImage = (ImageView) view;
//                int position = (int) favImage.getTag();
//                Movie track = movies.get(position);
//
//                if (favMovies.indexOf(track) != -1){
//                    favMovies.remove(track);
//                    favImage.setImageResource(android.R.drawable.btn_star_big_off);
//                    Toast.makeText(context, "Removed from Favorites!", Toast.LENGTH_SHORT).show();
//                } else {
//                    favMovies.add(track);
//                    favImage.setImageResource(android.R.drawable.btn_star_big_on);
//                    Toast.makeText(context, "Marked as Favorite!", Toast.LENGTH_SHORT).show();
//                }
//
//                SharedPreferences.Editor editor = pref.edit();
//
//                Gson storeGson = new Gson();
//                String storeJson = storeGson.toJson(favMovies);
//
//                editor.putString(MainActivity.FAVORITE_LIST, storeJson);
//
//                editor.commit();
//            }
//        });

        return convertView;
    }

    static class ViewHolder{
        TextView title, price;
        ImageView thumbnil, priceRating;
    }
}
