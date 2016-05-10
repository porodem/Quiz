package com.porodem.doatquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Quiz extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    //sound int
    int aganim_venom;
    int armlet_lifestealer;
    int assault_lifestealer;
    int battlefury_jugger;
    int blackking_spiritbreaker;
    int blademail;
    int blink_nightstalker;
    int bloodstone_dp;
    int bootstravel_tinker;
    int butterfly_slark;
    int dagon_nyx;
    int deadalus_draw;
    int desolator_bh;
    int divine_gyro;
    int eye_medusa;

    int holy_shit;

    SoundPool sp;

    //for dialog
    int btn;
    final int DIALOG = 1;
    LinearLayout view;
    TextView tvCount;

    TextView dItemName;
    TextView dParam;

    //item to quiz
    Item item;

    //счетчик выбранных (заполненных) ингредиентов для MyClickListener в диалоге
    //подтверждения выбора
    int items = 0;
    //для флага - какая кнопка с ингредиентам была нажата,
    int flafIng;
    //длина массива верных ингредиентов
    int RAlength;

    //массив изображеий
    int[] imgArray;

    //ingredients checket by user
    //массив ингредиентов выбор которых подтвердил пользователь
    int[] ingByUser;

    //массив верных ингредиентов для текущего предмета
    int[] correctIngr;

    //массив верных ингредиентов для текущего предмета
    int[] ingrpack;

    //окончательный массив с верной длиной для выбраных пользователем вещей
    int[] finalIngByUser;

    //ID выбранного элемента (добавляется к массиву ingrByUser
    int thisID;

    public static final String LOG = "myLogs";

    RatingBar ratingBar;
    //score
    int score;

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

    //объявляем объекты предметов которые будут использованы для работы с диалогом
    Item firstIng;
    Item secondIng;
    Item thirdIng;
    Item fourthIng;
    Item fifthIng;
    Item sixthIng;

    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //add Sound Pool
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);
        aganim_venom = sp.load(this, R.raw.aganim_venom, 1);
        armlet_lifestealer = sp.load(this, R.raw.armlet_lifestealer, 1);
        assault_lifestealer = sp.load(this, R.raw.assault_lifestealer, 1);
        battlefury_jugger = sp.load(this, R.raw.battlefury_jugger, 1);
        blackking_spiritbreaker = sp.load(this, R.raw.blackking_spiritbreaker, 1);
        blademail = sp.load(this, R.raw.blademail, 1);
        blink_nightstalker = sp.load(this, R.raw.blink_nightstalker, 1);
        bloodstone_dp = sp.load(this, R.raw.bloodstone_dp, 1);
        bootstravel_tinker = sp.load(this, R.raw.bootstravel_tinker, 1);
        butterfly_slark = sp.load(this, R.raw.butterfly_slark, 1);
        dagon_nyx = sp.load(this, R.raw.dagon_nyx, 1);
        deadalus_draw = sp.load(this, R.raw.deadalus_draw, 1);
        desolator_bh = sp.load(this, R.raw.desolator_bh, 1);
        divine_gyro = sp.load(this, R.raw.divine_gyro, 1);
        eye_medusa = sp.load(this, R.raw.eye_medusa, 1);
        holy_shit = sp.load(this, R.raw.announcer_kill_holy, 1);

        score = 0;

        //Rating Bar score
        ratingBar = (RatingBar) findViewById(R.id.rating);
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.YELLOW);
        ratingBar.setNumStars(5);
        ratingBar.setRating(0);

        //объявляем массивы для ID правильных и введенных ответов
        ingByUser = new int[4];
        correctIngr = new int[4];

        //get resurses for all images
        Resources res = getResources();
        itemImg = res.obtainTypedArray(R.array.item_images);

        ivQuizImg = (ImageView) findViewById(R.id.mainImg);

        imgPicked1 = (ImageView) findViewById(R.id.imagePicked1);
        imgPicked2 = (ImageView) findViewById(R.id.imagePicked2);
        imgPicked3 = (ImageView) findViewById(R.id.imagePicked3);
        imgPicked4 = (ImageView) findViewById(R.id.imagePicked4);

        check1but = (ImageButton) findViewById(R.id.check1);
        check2but = (ImageButton) findViewById(R.id.check2);
        check3but = (ImageButton) findViewById(R.id.check3);
        check4but = (ImageButton) findViewById(R.id.check4);
        check5but = (ImageButton) findViewById(R.id.check5);
        check6but = (ImageButton) findViewById(R.id.check6);

        db = new DB(this);
/*
        //image array
        //ivQuizImg.setImageResource(itemImg.getResourceId(DB.COLUMN_IMAGE, 1));
        //item = db.getRandomItem();
        //Integer.toString(itemImg.getResourceId(Integer.parseInt(DB.COLUMN_IMAGE), 1));
        imgArray = new int[134];

        for (int i = 0; i <134; i ++) {
            imgArray[i] = itemImg.getResourceId(i, 1);
            int x = imgArray[i];
            Log.d(LOG, " My Log imgArray: " + x );
        }


        //open DB connection
        db = new DB(this);
        db.open();


        //try take data from JSON object
        //check for existed table, if exist dont parse JSON agein



        //следующая строка раньше выглядела так: if (db.checkOnEmpty() == false) {
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


        getNextDish();
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

    */
        getNextDish();
    }

    public void click_check1(View view) {
        btn = view.getId();

        showDialog(DIALOG);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.check);
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


    @Override
    protected void onPrepareDialog(int id, Dialog dialog){
        super.onPrepareDialog(id, dialog);

        if (id == DIALOG) {
            // Находим TextView для отображения названия предмета
            dItemName = (TextView) dialog.getWindow().findViewById(R.id.dItemName);
            // Находим TextView для отображения параметров предмета
            dParam = (TextView) dialog.getWindow().findViewById(
                    R.id.dParam);
            //Button btnAdd = (Button)dialog.getWindow().findViewById(R.id.btnApply);

            ImageView ivItem = (ImageView)dialog.getWindow().findViewById(R.id.ivItem);

            switch (btn) {
                case R.id.check1:
                    dItemName.setText(firstIng.title);
                    dParam.setText(firstIng.param);
                    ivItem.setImageResource(firstIng.img);
                    //так как мы выбрали первый предмет, то ставим флаг в значение = 1
                    flafIng = 1;
                    break;
                case R.id.check2:
                    dItemName.setText(secondIng.title);
                    dParam.setText(secondIng.param);
                    ivItem.setImageResource(secondIng.img);
                    flafIng = 2;
                    break;
                case R.id.check3:
                    dItemName.setText(thirdIng.title);
                    dParam.setText(thirdIng.param);
                    ivItem.setImageResource(thirdIng.img);
                    flafIng = 3;
                    break;
                case R.id.check4:
                    dItemName.setText(fourthIng.title);
                    dParam.setText(fourthIng.param);
                    ivItem.setImageResource(fourthIng.img);
                    flafIng = 4;
                    break;
                case R.id.check5:
                    dItemName.setText(fifthIng.title);
                    dParam.setText(fifthIng.param);
                    ivItem.setImageResource(fifthIng.img);
                    flafIng = 5;
                    break;
                case R.id.check6:
                    dItemName.setText(sixthIng.title);
                    dParam.setText(sixthIng.param);
                    ivItem.setImageResource(sixthIng.img);
                    flafIng = 6;
                    break;
            }
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
                    //set picket item to first imageView
                    //если выбранных вещей на данный момент ноль...
                    if (items==0) {
                        //в зависимости от значения флага выбираем какую картинку использовать
                        //для заполнения первого ответа

                        switch (flafIng) {
                            case 1:
                                thisID = firstIng.itemID;
                                imgPicked1.setImageResource(firstIng.img);
                                break;
                            case 2:
                                thisID = secondIng.itemID;
                                imgPicked1.setImageResource(secondIng.img);
                                break;
                            case 3:
                                thisID = thirdIng.itemID;
                                imgPicked1.setImageResource(thirdIng.img);
                                break;
                            case 4:
                                thisID = fourthIng.itemID;
                                imgPicked1.setImageResource(fourthIng.img);
                                break;
                            case 5:
                                thisID = fifthIng.itemID;
                                imgPicked1.setImageResource(fifthIng.img);
                                break;
                            case 6:
                                thisID = sixthIng.itemID;
                                imgPicked1.setImageResource(sixthIng.img);
                                break;

                        }
                        ingByUser[0] = thisID;
                        items+=1;
                        //break;
                        //если уже выбрана одна вещь, тогда делаем то же самое
                        //для ячейки второго выбранного предмета
                    } else if (items == 1) {

                        switch (flafIng) {
                            case 1:
                                thisID = firstIng.itemID;
                                imgPicked2.setImageResource(firstIng.img);
                                break;
                            case 2:
                                thisID = secondIng.itemID;
                                imgPicked2.setImageResource(secondIng.img);
                                break;
                            case 3:
                                thisID = thirdIng.itemID;
                                imgPicked2.setImageResource(thirdIng.img);
                                break;
                            case 4:
                                thisID = fourthIng.itemID;
                                imgPicked2.setImageResource(fourthIng.img);
                                break;
                            case 5:
                                thisID = fifthIng.itemID;
                                imgPicked2.setImageResource(fifthIng.img);
                                break;
                            case 6:
                                thisID = sixthIng.itemID;
                                imgPicked2.setImageResource(sixthIng.img);
                                break;
                        }
                        ingByUser[1] = thisID;
                        items+=1;

                        if (RAlength == 2) {
                            finalIngByUser = new int[2];
                            finalIngByUser[0] = ingByUser[0];
                            finalIngByUser[1] = ingByUser[1];
                            showRes();
                        }
                    } else if (items == 2) {


                        switch (flafIng) {
                            case 1:
                                thisID = firstIng.itemID;
                                imgPicked3.setImageResource(firstIng.img);
                                break;
                            case 2:
                                thisID = secondIng.itemID;
                                imgPicked3.setImageResource(secondIng.img);
                                break;
                            case 3:
                                thisID = thirdIng.itemID;
                                imgPicked3.setImageResource(thirdIng.img);
                                break;
                            case 4:
                                thisID = fourthIng.itemID;
                                imgPicked3.setImageResource(fourthIng.img);
                                break;
                            case 5:
                                thisID = fifthIng.itemID;
                                imgPicked3.setImageResource(fifthIng.img);
                                break;
                            case 6:
                                thisID = sixthIng.itemID;
                                imgPicked3.setImageResource(sixthIng.img);
                                break;
                        }
                        ingByUser[2] = thisID;
                        items+=1;

                        if (RAlength == 3) {
                            finalIngByUser = new int[3];
                            finalIngByUser[0] = ingByUser[0];
                            finalIngByUser[1] = ingByUser[1];
                            finalIngByUser[2] = ingByUser[2];
                            showRes();
                        }

                    } else if (items == 3) {
                        switch (flafIng) {
                            case 1:
                                thisID = firstIng.itemID;
                                imgPicked4.setImageResource(firstIng.img);
                                break;
                            case 2:
                                thisID = secondIng.itemID;
                                imgPicked4.setImageResource(secondIng.img);
                                break;
                            case 3:
                                thisID = thirdIng.itemID;
                                imgPicked4.setImageResource(thirdIng.img);
                                break;
                            case 4:
                                thisID = fourthIng.itemID;
                                imgPicked4.setImageResource(fourthIng.img);
                                break;
                            case 5:
                                thisID = fifthIng.itemID;
                                imgPicked4.setImageResource(fifthIng.img);
                                break;
                            case 6:
                                thisID = sixthIng.itemID;
                                imgPicked4.setImageResource(sixthIng.img);
                                break;
                        }
                        ingByUser[3] = thisID;
                        items+=1;

                        if (RAlength == 4) {
                            finalIngByUser = new int[4];
                            finalIngByUser[0] = ingByUser[0];
                            finalIngByUser[1] = ingByUser[1];
                            finalIngByUser[2] = ingByUser[2];
                            finalIngByUser[3] = ingByUser[3];
                            showRes();
                        }

                    }
                    String arrayF = Arrays.toString(ingByUser);
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
        btn = view.getId();
        showDialog(DIALOG);
    }

    public void click_check4(View view) {
        btn = view.getId();
        showDialog(DIALOG);
    }

    public void click_check5(View view) {
        btn = view.getId();
        showDialog(DIALOG);
    }

    public void click_check6(View view) {
        btn = view.getId();
        showDialog(DIALOG);
    }

    //метод который начинает очередной вопрос (очищает выбранные предметы, генерирует случайный предмет
    //для угадывания, и заполняет варианты возможных ингредиентов перемешанные с правильными ингредиентами
    public void getNextDish() {
        /*try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //очищаем счетчик выбраных предметов
        items = 0;
        //сначала очищаем все выбранные предметы, ставим вопросики вместо них
        imgPicked1.setImageResource(R.drawable.quest_mark);
        imgPicked2.setImageResource(R.drawable.quest_mark);
        imgPicked3.setImageResource(R.drawable.quest_mark);
        imgPicked4.setImageResource(R.drawable.quest_mark);
        db.open();
        //take first random Item
        item = db.getRandomItem();

        //ivQuizImg.setImageResource(itemImg.getResourceId(item.img, 1));
        ivQuizImg.setImageResource(item.img);

        //play sound
        ratingBar.setRating(score);
        String itemSound = item.sound;
        switch (itemSound){
            case "aghanim":
                sp.play(aganim_venom, 1, 1, 0, 0, 1);
                break;
            case "armlet":
                sp.play(armlet_lifestealer, 1, 1, 0, 0, 1);
                break;
            case "assault":
                sp.play(assault_lifestealer, 1, 1, 0, 0, 1);
                break;
            case "batterfury":
                sp.play(battlefury_jugger, 1, 1, 0, 0, 1);
                break;
            case "blackking":
                sp.play(blackking_spiritbreaker, 1, 1, 0, 0, 1);
                break;
            case "blademail":
                sp.play(blademail, 1, 1, 0, 0, 1);
                break;
            case "blink":
                sp.play(blink_nightstalker, 1, 1, 0, 0, 1);
                break;
            case "bloodstone":
                sp.play(bloodstone_dp, 1, 1, 0, 0, 1);
                break;
            case "bootstravel":
                sp.play(bootstravel_tinker, 1, 1, 0, 0, 1);
                break;
            case "butterfly":
                sp.play(butterfly_slark, 1, 1, 0, 0, 1);
                break;
            case "dagon":
                sp.play(dagon_nyx, 1, 1, 0, 0, 1);
                break;
            case "deadalus":
                sp.play(deadalus_draw, 1, 1, 0, 0, 1);
                break;
            case "desolator":
                sp.play(desolator_bh, 1, 1, 0, 0, 1);
                break;
            case "divine":
                sp.play(divine_gyro, 1, 1, 0, 0, 1);
                break;
            case "eye":
                sp.play(eye_medusa, 1, 1, 0, 0, 1);
                break;
            default:
                break;
        }

        //get ingr pack for this item
        int num = item.itemID;
        //take correct ingredients for item
        /*
        int[] ingrpack = db.getCurrentIngr(num);
        //создаем массив длинна которого равна количеству правильных ингредиентов
        correctIngr = new int[ingrpack.length];
         */
        ingrpack = db.getCurrentIngr(num);
        //создаем массив длинна которого равна количеству правильных ингредиентов
        //correctIngr = new int[ingrpack.length];
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
                RAlength = 4;
            }
            if (ingrToQuiz.length == 3) {
                allIngrToQuiz[2] = ingrpack[2];
                allIngrToQuiz[3] = ingrToQuiz[0];
                allIngrToQuiz[4] = ingrToQuiz[1];
                allIngrToQuiz[5] = ingrToQuiz[2];
                RAlength = 3;
            }
            if (ingrToQuiz.length == 4) {
                allIngrToQuiz[2] = ingrToQuiz[0];
                allIngrToQuiz[3] = ingrToQuiz[1];
                allIngrToQuiz[4] = ingrToQuiz[2];
                allIngrToQuiz[5] = ingrToQuiz[3];
                RAlength = 2;
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
            //раньше тут было setImageResource но из за этого торчали края кнопок на изображении
            //check1but.setBackgroundResource(itemImg.getResourceId(firstIng.img, 1));
            check1but.setBackgroundResource(firstIng.img);
            //вторая кнопка и второй ингредиент
            secondIng = db.getParticularItem(allIngrToQuiz[1]);
            check2but.setBackgroundResource(secondIng.img);
            //3
            thirdIng = db.getParticularItem(allIngrToQuiz[2]);
            check3but.setBackgroundResource(thirdIng.img);
            //4
            fourthIng = db.getParticularItem(allIngrToQuiz[3]);
            check4but.setBackgroundResource(fourthIng.img);
            //5
            fifthIng = db.getParticularItem(allIngrToQuiz[4]);
            check5but.setBackgroundResource(fifthIng.img);
            //6
            sixthIng = db.getParticularItem(allIngrToQuiz[5]);
            check6but.setBackgroundResource(sixthIng.img);

        }


        //get items with quiz level
        int lvl = item.level - 1;

        db.close();

        //проверяем сколько правильных вещей нужно и делаем массив такой длины, куда будут записываться верные
        //ингредиенты

        if (ingrpack.length == 2) {
            ingByUser = new int[2];
        } else if (ingrpack.length == 3) {
            ingByUser = new int[3];
        } else if (ingrpack.length == 4) {
            ingByUser = new int[4];
        }
    }

    public void showRes() {
        String ibu = Arrays.toString(finalIngByUser);
        String coi = Arrays.toString(ingrpack);
        if (finalIngByUser.length != ingrpack.length) {
        } else {
        }
        int on = 0;
        int i;
        int j;
        for (i = 0; i < finalIngByUser.length; i++) {
            for (j = 0; j < ingrpack.length; j++) {
                if (finalIngByUser[i] == ingrpack[j]){
                    ingrpack[j]=0;
                    on++;
                }
            }
        }
        if (on==finalIngByUser.length) {
            score += 1;
            if (score > 5) {
                sp.play(holy_shit, 1, 1, 0, 0, 1);
            }

            Toast.makeText(this, R.string.correct, Toast.LENGTH_LONG).show();

            getNextDish();
        } else {
            score = 0;
            ratingBar.setRating(score);
            Toast.makeText(this, R.string.wrong, Toast.LENGTH_SHORT).show();
            getNextDish();
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }
}
