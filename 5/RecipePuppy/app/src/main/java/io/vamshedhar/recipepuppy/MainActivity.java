package io.vamshedhar.recipepuppy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RecipePuppy";
    public static final String DISH_NAME_KEY = "DISH_NAME";
    public static final String DISH_INGREDIENTS_KEY = "DISH_INGREDIENTS";

    EditText dishName, newIngredient;
    LinearLayout IngredientList;

    Button searchBtn;

    ArrayList<String> ingredients;

    public boolean isConnectedOnline(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnected()){
            return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dishName = (EditText) findViewById(R.id.dishName);
        newIngredient = (EditText) findViewById(R.id.newIngrediant);

        IngredientList = (LinearLayout) findViewById(R.id.ingredientList);

        searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String dishNameValue = dishName.getText().toString().trim();

                if(dishNameValue.equals("")){
                    Toast.makeText(MainActivity.this, "Please enter a valid Dish Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                ingredients = new ArrayList<>();
                for (int i = 0; i < IngredientList.getChildCount(); i++) {
                    LinearLayout IngredientItem = (LinearLayout) IngredientList.getChildAt(i);

                    EditText IngredientEditText;

                    if (i == IngredientList.getChildCount() - 1){
                        IngredientEditText = (EditText) IngredientItem.getChildAt(0);
                    } else{
                        LinearLayout IngredientItemContainer = (LinearLayout) IngredientItem.getChildAt(0);
                        IngredientEditText = (EditText) IngredientItemContainer.getChildAt(0);
                    }


                    String keyWord =  IngredientEditText.getText().toString().trim();

                    if(!keyWord.equals("")){
                        ingredients.add(keyWord);
                    }

                }

                if(ingredients.size() > 0){

                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET}, 5001);
                    } else {
                        getData();
                    }
                } else{
                    Toast.makeText(MainActivity.this, "Add at least one ingredient", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5001){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                getData();
            } else {
                Toast.makeText(MainActivity.this, "Please Grant required permissions!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getData(){
        if(!isConnectedOnline()){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        String dishNameValue = dishName.getText().toString().trim();

        Log.d(TAG, ingredients.toString());
        Intent intent = new Intent(MainActivity.this, Recipies.class);
        intent.putExtra(DISH_NAME_KEY, dishNameValue);
        intent.putExtra(DISH_INGREDIENTS_KEY, TextUtils.join(",", ingredients));
        startActivity(intent);
    }

    public void onAddIngredientClick(View view){

        if (IngredientList.getChildCount() == 5){
            Toast.makeText(this, "You can add only 5 ingredients", Toast.LENGTH_SHORT).show();
            return;
        }

        String ingredient = newIngredient.getText().toString();

        if(!ingredient.trim().equals("")){
            IngredientItemUI IngredientView = new IngredientItemUI(this);

            View IngredientItemView = IngredientView;

            IngredientView.ingredient.setText(ingredient);

            IngredientView.removeIngredientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IngredientList.removeView((View) view.getParent().getParent());
                }
            });

            IngredientList.addView(IngredientItemView, IngredientList.getChildCount() - 1);

            newIngredient.setText("");
        } else{
            Toast.makeText(this, "Please enter an Ingredient to add", Toast.LENGTH_SHORT).show();
        }

    }
}
