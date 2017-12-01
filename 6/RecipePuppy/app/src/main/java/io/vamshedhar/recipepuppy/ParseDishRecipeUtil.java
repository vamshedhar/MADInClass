package io.vamshedhar.recipepuppy;

import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 9/25/17 8:00 PM.
 * vchinta1@uncc.edu
 */

public class ParseDishRecipeUtil {

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
            }

            xmlInnerText.setLength(0);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }


    }
}
