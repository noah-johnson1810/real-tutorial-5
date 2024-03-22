package edu.sdsmt.johnson_n_a.tutorial56;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class ColorSelectActivity extends AppCompatActivity {

    public static final String SELECTOR_COLOR = "ColorSelectActivity.selector_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);
    }

    public void selectColor(int color) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(SELECTOR_COLOR , color);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}