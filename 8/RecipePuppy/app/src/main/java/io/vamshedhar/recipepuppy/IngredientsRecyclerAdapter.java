package io.vamshedhar.recipepuppy;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/30/17 8:47 PM.
 * vchinta1@uncc.edu
 */

public class IngredientsRecyclerAdapter extends RecyclerView.Adapter<IngredientsRecyclerAdapter.ViewHolder>  {
    ArrayList<String> ingredients;
    Context context;
    IngredientsInterface IData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText ingredientET;
        FloatingActionButton actionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ingredientET = itemView.findViewById(R.id.ingredientET);
            actionButton = itemView.findViewById(R.id.removeIngredientBtn);
        }
    }

    public interface IngredientsInterface {
        // TODO: Update argument type and name
        void onAddClick(String value);
        void onRemoveClick(int position);
    }

    public IngredientsRecyclerAdapter(Context context, ArrayList<String> objects, IngredientsInterface IData){
        this.context = context;
        this.ingredients = objects;
        this.IData = IData;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String[] ingredient = {ingredients.get(position)};

        holder.ingredientET.setText(ingredient[0]);


        holder.ingredientET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ingredient[0] = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (position == ingredients.size() - 1){
            holder.actionButton.setImageResource(R.drawable.add);
            holder.actionButton.setTag("ADD");
            if (ingredients.size() > 1){
                holder.ingredientET.requestFocus();
            }
        } else {
            holder.actionButton.setImageResource(R.drawable.remove);
            holder.actionButton.setTag(Integer.toString(position));
        }

        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ImageView btn = (ImageView) view;

            String tag = (String) btn.getTag();

            if (tag.equals("ADD")){
                IData.onAddClick(ingredient[0]);
            } else {
                IData.onRemoveClick(Integer.parseInt(tag));
            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }


}
