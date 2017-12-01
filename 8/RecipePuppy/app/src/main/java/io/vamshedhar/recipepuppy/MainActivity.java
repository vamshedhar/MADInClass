package io.vamshedhar.recipepuppy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener, GetResultsFromServer.IData, RecipesFragment.OnFragmentInteractionListener {

    public static final String TAG = "RecipePuppy";
    public static final String BASE_URL = "http://www.recipepuppy.com/api/";

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    LinearLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = (LinearLayout) findViewById(R.id.fragmentContainer);

        getFragmentManager().beginTransaction().add(R.id.fragmentContainer, new SearchFragment(), "main_fragment").commit();
    }

    @Override
    public void onSearchData(String name, ArrayList<String> ingredients) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (isConnectedOnline()){
            ingredients.remove(ingredients.size() - 1);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new LoadingFragment(), "loading_fragment")
                    .addToBackStack(null)
                    .commit();
            new GetResultsFromServer("http://www.recipepuppy.com/api/", this).execute(name, TextUtils.join(",", ingredients));
        } else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void setupData(ArrayList<DishRecipe> recipes) {
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
        if (recipes.size() > 0){
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RecipesFragment(), "recipes_fragment")
                    .addToBackStack(null)
                    .commit();
            getFragmentManager().executePendingTransactions();

            RecipesFragment fragment = (RecipesFragment) getFragmentManager().findFragmentByTag("recipes_fragment");
            fragment.loadRecipies(recipes);
        } else {
            Toast.makeText(this, "No Recipes Found!", Toast.LENGTH_SHORT).show();
        }
        
    }

    @Override
    public void onFinishClick() {
        Log.d(MainActivity.TAG, "Clicked");
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(MainActivity.TAG, "Clicked");
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }

    }
}
