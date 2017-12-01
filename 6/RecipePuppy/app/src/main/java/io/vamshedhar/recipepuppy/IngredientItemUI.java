package io.vamshedhar.recipepuppy;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/25/17 6:50 PM.
 * vchinta1@uncc.edu
 */

public class IngredientItemUI extends LinearLayout {

    public TextView ingredient;
    public FloatingActionButton removeIngredientBtn;

    public IngredientItemUI(Context context) {
        super(context);
        inflateXML(context);
    }
    private void inflateXML(Context context) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View IngredientItemView = inflater.inflate(R.layout.ingredient_item, this);
        this.ingredient = (EditText) findViewById(R.id.ingredientET);
        this.removeIngredientBtn = (FloatingActionButton) findViewById(R.id.removeIngredientBtn);
    }
}
