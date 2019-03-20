package com.guesscelebrity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button optionOne;
    Button optionTwo;
    Button optionThree;
    Button optionFour;
    ImageView celeb;
    HashMap<String,String> listCeleb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        public Bitmap doInBackground(String... strings){

            return null;
        }
    }
}
