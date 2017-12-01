package io.vamshedhar.recipepuppy;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/25/17 7:39 PM.
 * vchinta1@uncc.edu
 */

public class DishRecipe {
    String dishTitle, dishIngredients, dishURL, imageURL;

    public DishRecipe(String dishTitle, String dishIngredients, String dishURL, String imageURL) {
        this.dishTitle = dishTitle;
        this.dishIngredients = dishIngredients;
        this.dishURL = dishURL;
        this.imageURL = imageURL;
    }

    public String getDishTitle() {
        return dishTitle;
    }

    public void setDishTitle(String dishTitle) {
        this.dishTitle = dishTitle;
    }

    public String getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(String dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    public String getDishURL() {
        return dishURL;
    }

    public void setDishURL(String dishURL) {
        this.dishURL = dishURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "DishRecipe{" +
                "dishTitle='" + dishTitle + '\'' +
                ", dishIngredients='" + dishIngredients + '\'' +
                ", dishURL='" + dishURL + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}
