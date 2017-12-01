package io.vamshedhar.recipepuppy;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/2/17 6:53 PM.
 * vchinta1@uncc.edu
 */

public class GetResultsFromServer extends AsyncTask<String, Integer, ArrayList<DishRecipe>> {

    String BASE_URL;
    IData activity;

    public GetResultsFromServer(String BASE_URL, IData activity) {
        this.BASE_URL = BASE_URL;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<DishRecipe> dishRecipes) {
        super.onPostExecute(dishRecipes);
        activity.setupData(dishRecipes);

    }

    @Override
    protected ArrayList<DishRecipe> doInBackground(String... params) {
        RequestParams request = new RequestParams("GET", BASE_URL);

        ArrayList<DishRecipe> dishRecipes = new ArrayList<>();

        request.addParam("q", params[0]);
        request.addParam("i", params[1]);

        try {
            HttpURLConnection con = request.setupConnection();
            con.connect();
            int statusCode = con.getResponseCode();
            if(statusCode == HttpURLConnection.HTTP_OK){

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder builder = new StringBuilder();

                String line = reader.readLine();

                while (line != null){
                    builder.append(line);
                    line = reader.readLine();
                }

                dishRecipes =  ParseDishRecipeUtil.DishRecipeJSONParser.parseDishRecipes(builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dishRecipes;
    }

    public interface IData{
        void setupData(ArrayList<DishRecipe> recipes);
    }
}
