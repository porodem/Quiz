package com.porodem.doatquiz;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class MainActivity extends AppCompatActivity {

    //массив изображеий
    int[] imgArray;

    TypedArray itemImg;

    public static final String LOG = "myLogs";

    DB db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get resurses for all images
        Resources res = getResources();
        itemImg = res.obtainTypedArray(R.array.item_images);

        //image array
        //ivQuizImg.setImageResource(itemImg.getResourceId(DB.COLUMN_IMAGE, 1));
        //item = db.getRandomItem();
        //Integer.toString(itemImg.getResourceId(Integer.parseInt(DB.COLUMN_IMAGE), 1));
        imgArray = new int[134];

        for (int i = 0; i <134; i ++) {
            imgArray[i] = itemImg.getResourceId(i, 1);
            int x = imgArray[i];
            Log.d(LOG, " My Log imgArray: " + x);
        }


        //open DB connection
        db = new DB(this);
        db.open();


        //try take data from JSON object
        //check for existed table, if exist dont parse JSON agein

        if (!db.checkOnEmpty()) {
            try {
                JSONObject obj = new JSONObject(loadJSONFromRaw());
                JSONArray m_jArray = obj.getJSONArray("item");
                for (int i = 0; i < m_jArray.length(); i++) {

                    JSONObject jo_inside = m_jArray.getJSONObject(i);
                    String title = jo_inside.getString("title");
                    int imgJSON = imgArray[i];
                    String sound = jo_inside.getString("sound");
                    int level = jo_inside.getInt("level");
                    int gold = jo_inside.getInt("gold");

                    JSONArray ingr = jo_inside.getJSONArray("recipe");
                    int[] ingrpack = new int[4];
                    for (int u = 0; u < ingr.length(); u++) {
                        int one_ingr = ingr.getInt(u);
                        ingrpack[u] = one_ingr;
                    }
                    String param = jo_inside.getString("param");
                    String use = jo_inside.getString("use");
                    String info = jo_inside.getString("info");
                    db.addItem(title, imgJSON, sound, level, gold, param, use, info);

                    //take every ingridients from JSONArray "ingr" and put them in DB_TABLE_INGR
                    int ingr1;
                    int ingr2;
                    int ingr3;
                    int ingr4;

                    if (ingrpack[0] != 0) {
                        ingr1 = ingrpack[0];
                    } else {
                        ingr1 = 0;
                    }
                    if (ingrpack[1] != 0) {
                        ingr2 = ingrpack[1];
                    } else {
                        ingr2 = 0;
                    }
                    if (ingrpack[2] != 0) {
                        ingr3 = ingrpack[2];
                    } else {
                        ingr3 = 0;
                    }
                    if (ingrpack[3] != 0) {
                        ingr4 = ingrpack[3];
                    } else {
                        ingr4 = 0;
                    }
                    db.addIngr(ingr1, ingr2, ingr3, ingr4);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db.close();


    }

    //get JSON file from "raw" folder of "res" folder
    public String loadJSONFromRaw() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.items);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            is.close();
        }
        String jsonString = writer.toString();
        return jsonString;
    }

    public void click_start(View view) {
        Intent intent = new Intent(MainActivity.this, Quiz.class);
        startActivity(intent);

    }


    public void click_list(View view) {
        Intent intent = new Intent(MainActivity.this, ItemList.class);
        startActivity(intent);
    }

    public void test(View view) {
    }
}
