package io.vamshedhar.recipepuppy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/25/17 8:00 PM.
 * vchinta1@uncc.edu
 */

public class ParseDishRecipeUtil {

    static public class  DishRecipeJSONParser{
        static ArrayList<DishRecipe> parseDishRecipes(String data) throws JSONException {

            ArrayList<DishRecipe> dishRecipies = new ArrayList<>();

            JSONObject root = new JSONObject(data);
            JSONArray results = root.getJSONArray("results");

            for(int i = 0; i < results.length(); i++){
                JSONObject recipe = results.getJSONObject(i);

                DishRecipe dishRecipe = new DishRecipe(recipe.getString("title"), recipe.getString("ingredients"), recipe.getString("href"), recipe.getString("thumbnail"));

                dishRecipies.add(dishRecipe);
            }
            return dishRecipies;
        }
    }
}
