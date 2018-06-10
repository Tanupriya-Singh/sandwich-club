package com.udacity.sandwichclub.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.udacity.sandwichclub.R;
import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) throws JSONException {

        Sandwich sandwich;

        JSONObject root = new JSONObject(json);
        JSONObject name = root.optJSONObject("name");
        String mainName = name.optString("mainName");
        JSONArray alsoKnownAsArray = name.optJSONArray("alsoKnownAs");
        ArrayList<String> alsoKnownAs = new ArrayList<>();
        if ( alsoKnownAsArray!= null) {
            for (int i = 0; i < alsoKnownAsArray.length(); i++) {
                alsoKnownAs.add(alsoKnownAsArray.optString(i));
            }
        }
        String placeOfOrigin = root.optString("placeOfOrigin");
        String description = root.optString("description");
        String image = root.optString("image");
        JSONArray ingredientsArray = root.optJSONArray("ingredients");
        ArrayList<String> ingredients = new ArrayList<>();
        if ( ingredientsArray!= null) {
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredients.add(ingredientsArray.optString(i));
            }
        }
        sandwich = new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, image, ingredients);
        return sandwich;
    }


}
