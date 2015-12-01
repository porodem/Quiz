package com.porodem.quiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.Random;

public class Quiz extends AppCompatActivity {

    //for dialog
    int btn;
    final int DIALOG = 1;
    LinearLayout view;
    TextView tvCount;
    //счетчик выбранных (заполненных) ингредиентов для MyClickListener в диалоге
    //подтверждения выбора
    int items = 0;
    //для флага - какая кнопка с ингредиентам была нажата,
    int flafIng;

    //ingredients checket by user
    //массив ингредиентов выбор которых подтвердил пользователь
    int[] ingByUser;

    //массив верных ингредиентов для текущего предмета
    int[] correctIngr;

    public static final String LOG = "myLogs";

    ImageView ivQuizImg;

    ImageView imgPicked1;
    ImageView imgPicked2;
    ImageView imgPicked3;
    ImageView imgPicked4;

    ImageButton check1but;
    ImageButton check2but;
    ImageButton check3but;
    ImageButton check4but;
    ImageButton check5but;
    ImageButton check6but;

    TypedArray itemImg;

    Item firstIng;
    Item secondIng;

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //get resurses for all images
        Resources res = getResources();
        itemImg = res.obtainTypedArray(R.array.item_images);

        ivQuizImg = (ImageView)findViewById(R.id.mainImg);

        imgPicked1 = (ImageView)findViewById(R.id.imagePicked1);
        imgPicked2 = (ImageView)findViewById(R.id.imagePicked2);
        imgPicked3 = (ImageView)findViewById(R.id.imagePicked3);
        imgPicked4 = (ImageView)findViewById(R.id.imagePicked4);

        check1but = (ImageButton)findViewById(R.id.check1);
        check2but = (ImageButton)findViewById(R.id.check2);
        check3but = (ImageButton)findViewById(R.id.check3);
        check4but = (ImageButton)findViewById(R.id.check4);
        check5but = (ImageButton)findViewById(R.id.check5);
        check6but = (ImageButton)findViewById(R.id.check6);


        //open DB connection
        db = new DB(this);
        db.open();


        //try take data from JSON object
        //check for existed table, if exist dont parse JSON agein



        if (db.checkOnEmpty() == false) {
            try {
                Log.d(LOG, "-- Start TRY block --");
                JSONObject obj = new JSONObject(loadJSONFromRaw());
                JSONArray m_jArray = obj.getJSONArray("item");
                for (int i = 0; i < m_jArray.length(); i++) {

                    JSONObject jo_inside = m_jArray.getJSONObject(i);
                    String title = jo_inside.getString("title");
                    int imgJSON = i;
                    String sound = "sfx";
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
                    Log.d(LOG, "-- item: " + title + " added.");

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
                    Log.d(LOG, "----- recipe added ---- |OK|");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG, "-- TRY DOESN'T WORK --");
        }

        //look all data containing in DB
        /*
        Cursor c;
        c = db.getAllIngr();
        logCursor(c);
        c.close();
        */

        //take first random Item
        Item item = db.getRandomItem();


        ivQuizImg.setImageResource(itemImg.getResourceId(item.img, 1));
        Log.d(LOG, "item img = " + item.img);

        //get ingr pack for this item
        int num = item.itemID;
        Log.d(LOG, "item ID = " + num);
        //take correct ingredients for item
        int[] ingrpack = db.getCurrentIngr(num);
        //создаем массив длинна которого равна количеству правильных ингредиентов
        correctIngr = new int[ingrpack.length];
        String x = Arrays.toString(ingrpack);
        if (ingrpack[0] != 0) {
            int lvl = item.level -1;
            //find out how many ingredients need add
            int moreIngr = 6 - ingrpack.length;
            int[] ingrToQuiz = db.getIngToQuiz(moreIngr, lvl);
            //создаем новый массив с элементами которые будем использовать в вариантах ответов
            //массив заполняется из двух массивов (предыдущих) какой именно элемент из какого
            //определяется исходя из кол-ва верных ингредиентов необходимых для сборки предмета
            int[] allIngrToQuiz = new int[6];
            allIngrToQuiz[0] = ingrpack[0];
            allIngrToQuiz[1] = ingrpack[1];
            //если для сборки нужно 4 вещи (дополнительных приходит 2) , то первые 2 берем из первого массива, а 5 и 6 из второго
            if (ingrToQuiz.length == 2) {
                allIngrToQuiz[2] = ingrpack[2];
                allIngrToQuiz[3] = ingrpack[3];
                allIngrToQuiz[4] = ingrToQuiz[0];
                allIngrToQuiz[5] = ingrToQuiz[1];
            }
            if (ingrToQuiz.length == 3) {
                allIngrToQuiz[2] = ingrpack[2];
                allIngrToQuiz[3] = ingrToQuiz[0];
                allIngrToQuiz[4] = ingrToQuiz[1];
                allIngrToQuiz[5] = ingrToQuiz[2];
            }
            if (ingrToQuiz.length == 4) {
                allIngrToQuiz[2] = ingrToQuiz[0];
                allIngrToQuiz[3] = ingrToQuiz[1];
                allIngrToQuiz[4] = ingrToQuiz[2];
                allIngrToQuiz[5] = ingrToQuiz[3];
            }

            //перемешиваем элементы масссива
            Random rnd = new Random();
            for (int i = allIngrToQuiz.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                // Simple swap
                int a = allIngrToQuiz[index];
                allIngrToQuiz[index] = allIngrToQuiz[i];
                allIngrToQuiz[i] = a;
            }


            //пробуем по полученным ID ингредиентов получить названия и поместить на кнопки
            //первая кнопка и первый ингредиент
            firstIng = db.getParticularItem(allIngrToQuiz[0]);
            check1but.setBackgroundResource(itemImg.getResourceId(firstIng.img, 1));
            //вторая кнопка и второй ингредиент
            secondIng = db.getParticularItem(allIngrToQuiz[1]);
            check2but.setImageResource(itemImg.getResourceId(secondIng.img, 1));
            //3
            Item thirdIng = db.getParticularItem(allIngrToQuiz[2]);
            check3but.setImageResource(itemImg.getResourceId(thirdIng.img, 1));
            //4
            Item fourthIng = db.getParticularItem(allIngrToQuiz[3]);
            check4but.setImageResource(itemImg.getResourceId(fourthIng.img, 1));
            //5
            Item fifthIng = db.getParticularItem(allIngrToQuiz[4]);
            check5but.setImageResource(itemImg.getResourceId(fifthIng.img, 1));
            //6
            Item sixthIng = db.getParticularItem(allIngrToQuiz[5]);
            check6but.setImageResource(itemImg.getResourceId(sixthIng.img, 1));
            //7

        }

        Log.d(LOG, "-- Array ingredients: " + x);

        //get items with quiz level
        int lvl = item.level - 1;
        //Log.d(LOG, "level current item = " + lvl);
        //db.getIngToQuiz(numing, lvl);

        db.close();

    }
    //metod for log cursor
    void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG, str);
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG, "Cursor is null");
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


    public void click_check1(View view) {
        btn = view.getId();

        /*Item firstIng = db.getParticularItem(allIngrToQuiz[0]);
        check1but.setImageResource(itemImg.getResourceId(firstIng.img, 1));
        */
        showDialog(DIALOG);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("custom dialog");
        //create view from dialog.xml
        view = (LinearLayout)getLayoutInflater()
                .inflate(R.layout.dialog, null);
        //install it as dialog body containing
        //устанавливаем ее, как содержимое тела диалога
        adb.setView(view);
        adb.setPositiveButton(R.string.add, myClickListener);
        adb.setNegativeButton(R.string.cancle, myClickListener);
        //find textView
        tvCount = (TextView)view.findViewById(R.id.tvCount);

        return adb.create();
    }

    /*
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    Log.d(LOG, "Poitive button WORK");
                    finish();
                    break;
            }
        };
        */

    @Override
    protected void onPrepareDialog(int id, Dialog dialog){
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG) {
            // Находим TextView для отображения параметров предмета
            TextView tvTime = (TextView) dialog.getWindow().findViewById(
                    R.id.param);
            //Button btnAdd = (Button)dialog.getWindow().findViewById(R.id.btnApply);

            ImageView ivItem = (ImageView)dialog.getWindow().findViewById(R.id.ivItem);

            switch (btn) {
                case R.id.check1:
                    tvTime.setText(firstIng.param);
                    ivItem.setImageResource(itemImg.getResourceId(firstIng.img, 1));
                    Log.d(LOG, "Button for DIALOG = " + firstIng.title + " ID: " + R.id.check1);
                    //так как мы выбрали первый предмет, то ставим флаг в значение = 1
                    flafIng = 1;
                    break;
                case R.id.check2:
                    tvTime.setText(secondIng.param);
                    ivItem.setImageResource(itemImg.getResourceId(secondIng.img, 1));
                    Log.d(LOG, "Button for DIALOG = " + secondIng.title + " ID: " + R.id.check1);
                    flafIng = 2;
                    break;
            }
            /*
            if (btn == R.id.check1) {
                Log.d(LOG, "Button for DIALOG = " + firstIng.title);
            }
            if (btn == R.id.check2) {
                Log.d(LOG, "Button for DIALOG = " + secondIng.title);
            }
            */
        }
    }
// Click Listener for dialog buttons
    //******************************
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {



            Drawable img;
            //определяем нажатую кнопку в диалоговом окне
            switch (which) {

                // положительная кнопка (подтверждение выбора)
                case Dialog.BUTTON_POSITIVE:

                    Log.d(LOG, "Poitive button WORK");
                    //set picket item to first imageView
                    //если выбранных вещей на данный момент ноль...
                    if (items==0) {
                        //в зависимости от значения флага выбираем какую картинку использовать
                        //для заполнения первого ответа
                        switch (flafIng) {

                            case 1:
                                correctIngr[0] = firstIng.itemID;
                                imgPicked1.setImageResource(itemImg.getResourceId(firstIng.img, 1));
                                break;
                            case 2:
                                imgPicked1.setImageResource(itemImg.getResourceId(secondIng.img, 1));
                                break;
                        }

                        items+=1;
                        //break;
                        //если уже выбрана одна вещь, тогда делаем то же самое
                        //для ячейки второго выбранного предмета
                    } else if (items == 1) {

                        switch (flafIng) {
                            case 1:
                                imgPicked2.setImageResource(itemImg.getResourceId(firstIng.img, 1));
                                break;
                            case 2:
                                imgPicked2.setImageResource(itemImg.getResourceId(secondIng.img, 1));
                                //break;
                        }

                        items+=1;
                        break;
                    }
                    Log.d(LOG, "Poitive button items count: " + items);
                    break;
                case Dialog.BUTTON_NEGATIVE:

                    break;

            }
            }
        };

    public void click_check2(View view) {
        btn = view.getId();
        showDialog(DIALOG);
    }

    public void click_check3(View view) {
    }

    public void click_check4(View view) {
    }

    public void click_check5(View view) {
    }

    public void click_check6(View view) {
    }
}