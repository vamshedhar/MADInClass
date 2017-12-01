package io.vamshedhar.recipepuppy;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/25/17 8:00 PM.
 * vchinta1@uncc.edu
 */

public class ParseDishRecipeUtil {

    public static class  DishRecipeJSONParser{
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

    public static class DishRecipeSAXParser extends DefaultHandler{
        ArrayList<DishRecipe> recipeArrayList;
        DishRecipe recipe;
        StringBuilder xmlInnerText;

        public ArrayList<DishRecipe> getRecipeArrayList() {
            return recipeArrayList;
        }

        static ArrayList<DishRecipe> parseDishRecipes(InputStream in) throws IOException, SAXException {
            DishRecipeSAXParser parser = new DishRecipeSAXParser();
            Xml.parse(in, Xml.Encoding.UTF_8, parser);
            return parser.getRecipeArrayList();
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            recipeArrayList = new ArrayList<>();
            xmlInnerText = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if(localName.equals("recipe")){
                recipe = new DishRecipe();
                recipe.id = attributes.getValue("id");
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            xmlInnerText.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if(localName.equals("recipe")){
                recipeArrayList.add(recipe);
            } else if(localName.equals("title")){
                recipe.dishTitle = xmlInnerText.toString().trim();
            } else if(localName.equals("href")){
                recipe.dishURL = xmlInnerText.toString().trim();
            } else if(localName.equals("ingredients")){
                recipe.dishIngredients = xmlInnerText.toString().trim();
            } else if(localName.equals("thumbnail")){
                recipe.imageURL = xmlInnerText.toString().trim();
            }

            xmlInnerText.setLength(0);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }


    }

    public static class DishRecipePullParser {

        static ArrayList<DishRecipe> parseDishRecipes(InputStream in) throws XmlPullParserException, IOException {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, "UTF-8");

            ArrayList<DishRecipe> recipeList = new ArrayList<>();
            DishRecipe recipe = null;

            int event = parser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT){
                switch (event){
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("recipe")){
                            recipe = new DishRecipe();
                            recipe.id = parser.getAttributeValue(null, "id");
                        } else if(parser.getName().equals("title")){
                            recipe.dishTitle = parser.nextText().trim();
                        } else if(parser.getName().equals("href")){
                            recipe.dishURL = parser.nextText().trim();
                        } else if(parser.getName().equals("ingredients")){
                            recipe.dishIngredients = parser.nextText().trim();
                        } else if(parser.getName().equals("thumbnail")){
                            recipe.imageURL = parser.nextText().trim();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("recipe")){
                            recipeList.add(recipe);
                        }
                        break;
                }

                event = parser.next();
            }

            return recipeList;
        }
    }
}
