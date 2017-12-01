package io.vamshedhar.recipepuppy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Recipies extends AppCompatActivity implements GetResultsFromServer.IData, GetImageFromServer.IImage {

    public static final String BASE_URL = "http://www.recipepuppy.com/api/";
    public static final String IMAGE_URL = "https://c1.staticflickr.com/5/4286/35513985750_2690303c8b_z.jpg";

    TextView dishTitle, dishIngredients, dishURL, dishLabel, ingredientsLabel, urlLabel, loadingText;
    ImageView dishImage;

    ProgressBar pb, imageProgress;

    ArrayList<DishRecipe> loadedRecipes;

    ImageView firstBtn, lastBtn, nextBtn, prevBtn;

    Button finishBtn;

    int currentIndex;

    String dishNameValue, dishIngredientsValue;

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    public void toggleViews(int visibility){
        dishTitle.setVisibility(visibility);
        dishIngredients.setVisibility(visibility);
        dishURL.setVisibility(visibility);

        dishLabel.setVisibility(visibility);
        ingredientsLabel.setVisibility(visibility);
        urlLabel.setVisibility(visibility);

        nextBtn.setVisibility(visibility);
        lastBtn.setVisibility(visibility);
        prevBtn.setVisibility(visibility);
        firstBtn.setVisibility(visibility);
        finishBtn.setVisibility(visibility);
    }

    public void loadData(int index){
        DishRecipe recipe = loadedRecipes.get(index);

        dishTitle.setText(recipe.dishTitle.replaceAll("\n", "").replaceAll("\r", ""));
        dishIngredients.setText(recipe.dishIngredients);
        dishURL.setPaintFlags(dishURL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dishURL.setText(recipe.dishURL);

    }

    @Override
    public void setupData(ArrayList<DishRecipe> recipes) {
        pb.setProgress(1);

        loadedRecipes = recipes;

        if(loadedRecipes.size() == 0){
            Toast.makeText(Recipies.this, "No Recipes Found", Toast.LENGTH_SHORT).show();
            finish();
        }

        currentIndex = 0;

        loadData(0);

        pb.setProgress(1);
        pb.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);

        toggleViews(View.VISIBLE);

        dishImage.setVisibility(View.INVISIBLE);
        imageProgress.setVisibility(View.VISIBLE);

        new GetImageFromServer(this).execute(IMAGE_URL);
    }

    @Override
    public void setImage(Bitmap bitmap) {
        if(bitmap != null){
            dishImage.setImageBitmap(bitmap);
        } else {
            dishImage.setImageResource(R.drawable.no_image);
        }

        imageProgress.setVisibility(View.INVISIBLE);
        dishImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipies);

        dishTitle = (TextView) findViewById(R.id.dishTitle);
        dishIngredients = (TextView) findViewById(R.id.ingredientsList);
        dishURL = (TextView) findViewById(R.id.recipeURL);
        loadingText = (TextView) findViewById(R.id.loadingText);

        dishLabel = (TextView) findViewById(R.id.dishLabel);
        ingredientsLabel = (TextView) findViewById(R.id.ingredientsLabel);
        urlLabel = (TextView) findViewById(R.id.urlLabel);
        
        dishImage = (ImageView) findViewById(R.id.dishImage);

        pb = (ProgressBar) findViewById(R.id.requestProgress);
        imageProgress = (ProgressBar) findViewById(R.id.imageLoadProgress);

        nextBtn = (ImageView) findViewById(R.id.nextBtn);
        prevBtn = (ImageView) findViewById(R.id.previousBtn);
        firstBtn = (ImageView) findViewById(R.id.firstBtn);
        lastBtn = (ImageView) findViewById(R.id.lastBtn);
        finishBtn = (Button) findViewById(R.id.finishButton);

        loadedRecipes = new ArrayList<>();

        if(getIntent().getExtras() != null){
            if (getIntent().getExtras().containsKey(MainActivity.DISH_NAME_KEY)){
                dishNameValue = getIntent().getExtras().getString(MainActivity.DISH_NAME_KEY);
            }

            if (getIntent().getExtras().containsKey(MainActivity.DISH_INGREDIENTS_KEY)){
                dishIngredientsValue = getIntent().getExtras().getString(MainActivity.DISH_INGREDIENTS_KEY);
            }

            if(isConnectedOnline()){
                pb.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                pb.setMax(1);
                pb.setProgress(0);

                toggleViews(View.INVISIBLE);

                dishImage.setVisibility(View.INVISIBLE);
                new GetResultsFromServer(BASE_URL, this).execute(dishNameValue, dishIngredientsValue);
            } else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void onFinishClick(View view){
        finish();
    }

    public void onNextClick(View view){
        if(currentIndex + 1 < loadedRecipes.size()){
            currentIndex = currentIndex + 1;
            loadData(currentIndex);
        } else {
            Toast.makeText(this, "No more recipes to load", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPreviousClick(View view){
        if(currentIndex - 1 >= 0){
            currentIndex = currentIndex - 1;
            loadData(currentIndex);
        } else {
            Toast.makeText(this, "No more recipes to load", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFirstClick(View view){
        if(currentIndex != 0){
            currentIndex = 0;
            loadData(currentIndex);
        }
    }

    public void onLastClick(View view){
        if(currentIndex != loadedRecipes.size() - 1){
            currentIndex = loadedRecipes.size() - 1;
            loadData(currentIndex);
        }
    }

    public void onURLClick(View view){
        TextView urlView = (TextView) view;


        String url = urlView.getText().toString();

        if(!url.equals("")){
            Log.d(MainActivity.TAG, url);

            if (!url.startsWith("http://") && !url.startsWith("https://")){
                url = "http://" + url;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}
