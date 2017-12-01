package io.vamshedhar.recipepuppy;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/25/17 7:39 PM.
 * vchinta1@uncc.edu
 */

public class DishRecipe {
    String id, dishTitle, dishIngredients, dishURL, imageURL;

    public DishRecipe() {
    }

    public DishRecipe(String dishTitle, String dishIngredients, String dishURL, String imageURL) {
        this.dishTitle = dishTitle;
        this.dishIngredients = dishIngredients;
        this.dishURL = dishURL;
        this.imageURL = imageURL;
    }
}
