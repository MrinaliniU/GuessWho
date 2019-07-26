package com.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    There are better ways to download images from internet we will do that in other apps.
 */

public class MainActivity extends AppCompatActivity {
    Button optionOne;
    Button optionTwo;
    Button optionThree;
    Button optionFour;
    TextView correctCounterView;
    ImageView celebView;
    ArrayList<String> celebURL = new ArrayList<>();
    ArrayList<String> celebName = new ArrayList<>();
    int chosenCeleb = 0;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    int correctAnswerCounter = 0;

    private void parseSourceForURLAndCelebName(String stringToParse){
        // Some fun stuff with strings
        Pattern p = Pattern.compile("img src=\"(.*?)\"");
        Matcher m = p.matcher(stringToParse);
        while(m.find()){
            celebURL.add(m.group(1));

        }
        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(stringToParse);
        while(m.find()){
            celebName.add(m.group(1));
        }
    }

    public void onButtonClick(View view){
        if(view.getTag().toString().equals(String.valueOf(locationOfCorrectAnswer))){
            correctAnswerCounter++;
            correctCounterView.setText(String.valueOf(correctAnswerCounter));
        }else{
            Toast.makeText(this, "Wrong! It was " + celebName.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        getNextQuestion();
    }

    public void onReset(View view){
        correctAnswerCounter = 0;
        correctCounterView.setText(String.valueOf(correctAnswerCounter));
    }


    public void getNextQuestion(){
        Bitmap celebImage;
        try{
            // choose a random celeb
            Random random = new Random();
            chosenCeleb = random.nextInt(celebURL.size());
            DownloadImage imageDownloadTask = new DownloadImage();
            celebImage = imageDownloadTask.execute(celebURL.get(chosenCeleb)).get();
            celebView.setImageBitmap(celebImage);
            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectCeleb;
            for(int i = 0; i < 4; i++){
                if(i == locationOfCorrectAnswer){
                    answers[i] = celebName.get(chosenCeleb);
                }else{
                    incorrectCeleb = random.nextInt(celebURL.size());
                    while(incorrectCeleb == chosenCeleb){
                        incorrectCeleb = random.nextInt(celebURL.size());
                    }
                    answers[i] = celebName.get(incorrectCeleb);
                }
            }
            optionOne.setText(answers[0]);
            optionTwo.setText(answers[1]);
            optionThree.setText(answers[2]);
            optionFour.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String result;
        celebView = findViewById(R.id.imageView);
        optionOne = findViewById(R.id.button0);
        optionTwo = findViewById(R.id.button1);
        optionThree = findViewById(R.id.button2);
        optionFour = findViewById(R.id.button3);
        correctCounterView = findViewById(R.id.textView);
        /* On Create you can create the DownLoadTask that will download the
        Celeb Image.
         */
        DownLoadTask downloadImage = new DownLoadTask();
        try{
            // Here you need to call "get" in order to get the String result from the background task;
            result = downloadImage.execute("http://www.posh24.se/kandisar").get();
            String[] stringArray = result.split("<div class=\"listedArticles\">");
            parseSourceForURLAndCelebName(stringArray[0]);
            getNextQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    AsyncTask has three generic type
    Params - the type of the parameters sent to the task upon execution. He you will see it is String.
    Progress - the type of the progress units published during the background computation. Here it is Void.
    Result - the type of the result of the background computation. Here it is String.
     */
    private static class DownLoadTask extends AsyncTask<String, Void, String>{

        @Override
        /*
        The return type of doInBackground should match the Result type of AsyncTask. In this case
         a String.
         */
        /*
        This is what happens in background.
        1. Create a new URL object
        2. Create URL connection using url.openConnection()
        3. Read InputStream from the urlConnection
         */
        public String doInBackground(String... urls){
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                // Open url Connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect(); // Not sure what difference it makes. It seemed to work without it.
                // Read InputStream from urlConnection
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                /* When you read from this reader you get an int, which when casted to char will
                be chars read one by one
                 */
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    /* read from the reader again to move to next char.
                    data will equal -1 when the end of InputStream is reached.
                     */

                    data = reader.read();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            // return the final result. This should end up being the page source code.
            return result;
        }

    }

    /*
        Another Async Task to download Image.
     */
    public static class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            Bitmap image;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                image = BitmapFactory.decodeStream(urlConnection.getInputStream());
            }catch (Exception e){
                e.printStackTrace();
                /*
                 with this return statement, if bitmap image is null then null pointer
                 exception is caught and you can return null and hence no need to
                 initialize bitmap image as null.
                 */
                return null;
            }
            return image;
        }
    }
}
