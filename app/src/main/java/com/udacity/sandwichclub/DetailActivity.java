package com.udacity.sandwichclub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Target;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    Toolbar toolbar;
    ActionBar actionBar;
    TextView alsoTextView;
    TextView ingredientsTextView;
    TextView descriptionTextView;
    TextView originText;
    TextView placeOfOriginLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        alsoTextView = findViewById(R.id.also_known_as_label);
        ingredientsTextView = findViewById(R.id.ingredients_label);
        descriptionTextView = findViewById(R.id.description_label);
        originText = findViewById(R.id.origin_tv);
        placeOfOriginLabel = findViewById(R.id.place_of_origin_label);
        ImageView ingredientsIv = findViewById(R.id.image_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];

        Sandwich sandwich = null;
        try {
            sandwich = JsonUtils.parseSandwichJson(json);
            populateUI(sandwich);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        final Sandwich sandwich1 = sandwich;

        Picasso.with(this)
                .load(sandwich.getImage())
                .error(R.drawable.empty)
                .into(ingredientsIv);

//        Picasso.with(this)
//                .load(sandwich.getImage())
//                .into(ingredientsIv);

        setTitle(sandwich.getMainName());

        actionBar = getSupportActionBar();

        new ImageLoadTask().execute(sandwich.getImage());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Sandwich sandwich) {

        TextView originText = findViewById(R.id.origin_tv);
        TextView description = findViewById(R.id.description_tv);
        TextView alsoKnownTv = findViewById(R.id.also_known_tv);
        TextView ingredients = findViewById(R.id.ingredients_tv);

        if ((sandwich.getPlaceOfOrigin()).equals(""))
            originText.setText("Not known");
        else
            originText.setText(sandwich.getPlaceOfOrigin());

        if ((sandwich.getDescription().equals("")))
            description.setText("Not known");
        else
            description.setText(sandwich.getDescription());

        if (sandwich.getAlsoKnownAs().size() != 0)
            for (int i = 0; i < sandwich.getAlsoKnownAs().size(); i++)
                alsoKnownTv.append(sandwich.getAlsoKnownAs().get(i) + "\n");
        else
            alsoKnownTv.setText("Not known");

        if (sandwich.getIngredients().size() != 0)
            for (int i = 0; i < sandwich.getIngredients().size(); i++)
                ingredients.append(sandwich.getIngredients().get(i) + "\n");
        else
            ingredients.setText("Not known");
    }

    public void createPaletteAsync(Bitmap bitmap) {


        if (bitmap != null)
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {

                    int mutedColor = palette.getVibrantSwatch().getRgb();
                    float[] hsv = new float[3];
                    Color.colorToHSV(mutedColor, hsv);
                    hsv[2] *= 0.8f;
                    int color = Color.HSVToColor(hsv);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(color);
                        actionBar.setBackgroundDrawable(new ColorDrawable(mutedColor));
                        setColor(mutedColor);
                    }
                    else setColor(getResources().getColor(R.color.colorPrimary));
                }
            });

        else {
            int color = getResources().getColor(R.color.colorPrimary);
            setColor(color);
        }
    }

    public void setColor(int color){
        alsoTextView.setTextColor(color);

        ingredientsTextView.setTextColor(color);

        descriptionTextView.setTextColor(color);

        originText.setTextColor(color);

        placeOfOriginLabel.setTextColor(color);
    }

    private class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String string = strings[0];
            Bitmap bitmap = null;
            try {
                bitmap = Picasso.with(getApplicationContext()).load(string).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            createPaletteAsync(bitmap);
        }
    }

}
