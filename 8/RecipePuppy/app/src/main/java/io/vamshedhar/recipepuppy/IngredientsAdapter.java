package io.vamshedhar.recipepuppy;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/13/17 3:25 PM.
 * vchinta1@uncc.edu
 */


public class IngredientsAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> ingredients;
    IngredientsInterface IData;

    public IngredientsAdapter(Context context, ArrayList<String> objects, IngredientsInterface IData) {
        super(context, R.layout.ingredient_item, objects);
        this.context = context;
        this.ingredients = objects;
        this.IData = IData;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ingredient_item, parent, false);
            holder = new ViewHolder();
            holder.ingredientET = (EditText) convertView.findViewById(R.id.ingredientET);
            holder.actionButton = (ImageView) convertView.findViewById(R.id.removeIngredientBtn);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

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
            holder.ingredientET.requestFocus();
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
                    IData.onRemoveClick(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder{
        EditText ingredientET;
        ImageView actionButton;
    }

    public interface IngredientsInterface {
        // TODO: Update argument type and name
        void onAddClick(String value);
        void onRemoveClick(int position);
    }
}
