package com.porodem.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by porod on 09.11.2015.
 */
public class DB {
    //array with all Items with ingredients
    int[]iItems = new int[] {1,2,3,4,5,7,9,10,14,16,19,20,25,26,27,28,30,31,
            32,33,36,37,38,39,41,42,45,46,47,48,50,51,55,56,57,59,60,62,63,
            64,66,67,68,71,72,73,74,77,78,79,80,83,84,87,89,90,96,99,100,101,102};
    public static final String LOG = "myLogs";

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_ITEM = "itemtab";
    private static final String DB_TABLE_INGR = "ingrtab";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_IMAGE = "img";
    public static final String COLUMN_SOUND = "sound";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_GOLD = "gold";
    public static final String COLUMN_PARAM = "param";
    public static final String COLUMN_USE = "use";
    public static final String COLUMN_INFO = "info";

    public static final String COLUMN_INGR_1 = "ingr_1";
    public static final String COLUMN_INGR_2 = "ingr_2";
    public static final String COLUMN_INGR_3 = "ingr_3";
    public static final String COLUMN_INGR_4 = "ingr_4";

    public static final String DB_CREATE_ITEM =
            "create table " + DB_TABLE_ITEM + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " text, " +
                    COLUMN_IMAGE + " integer, " +
                    COLUMN_SOUND + " text, " +
                    COLUMN_LEVEL + " integer, " +
                    COLUMN_GOLD + " integer, " +
                    COLUMN_PARAM + " text, " +
                    COLUMN_USE + " text, " +
                    COLUMN_INFO + " text" +
                    ");";

    public static final String DB_CREATE_INGR =
            "create table " + DB_TABLE_INGR + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_INGR_1 + " integer, " +
                    COLUMN_INGR_2 + " integer, " +
                    COLUMN_INGR_3 + " integer, " +
                    COLUMN_INGR_4 + " integer" +
                    ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB (Context ctx) {
        mCtx = ctx;
    }
    Cursor c;

    //open connection
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    //close conection
    public void close(){
        if (mDBHelper != null) mDBHelper.close();
    }

    public Cursor getAllData() {
        return mDB.query(DB_TABLE_ITEM, null, null, null, null, null, null);
    }

    public Cursor getAllIngr() {
        return mDB.query(DB_TABLE_INGR, null, null, null, null, null, null);
    }

    public boolean checkOnEmpty() {
        Cursor c = mDB.query(DB_TABLE_ITEM, null, null, null, null, null, null);
        boolean clerrBase = c.moveToFirst();
        return clerrBase;
    }

//adding one Item. On start repaeting to full base (all items).
    public void addItem(String title, int img, String sound, int level, int gold, String param, String use, String info) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_IMAGE, img);
        cv.put(COLUMN_SOUND, sound);
        cv.put(COLUMN_LEVEL, level);
        cv.put(COLUMN_GOLD, gold);
        cv.put(COLUMN_PARAM, param);
        cv.put(COLUMN_USE, use);
        cv.put(COLUMN_INFO, info);
        mDB.insert(DB_TABLE_ITEM, null, cv);
    }

    //adding ingredients for current item to DB table "ingrtab"
    public void addIngr(int ingr1, int ingr2, int ingr3, int ingr4){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INGR_1, ingr1);
        cv.put(COLUMN_INGR_2, ingr2);
        cv.put(COLUMN_INGR_3, ingr3);
        cv.put(COLUMN_INGR_4, ingr4);
        Long rowID = mDB.insert(DB_TABLE_INGR, null, cv);
        Log.d(LOG, "Added record ingredients_id: " + rowID);
    }

    //get ingr to quiz (6 variants)
    public int [] getIngToQuiz(int numIng, int lvl) {
        int[] ingrToQuiz;
        ingrToQuiz = new int[numIng];
        Log.d(LOG, "-- getIngToQuiz --");
        String level = Integer.toString(lvl);
        String sqlQuery = "SELECT * FROM " + DB_TABLE_ITEM + " WHERE " + COLUMN_LEVEL + " = @level";
        c = mDB.rawQuery(sqlQuery, new String[] {level});
        int allIngrWithLvl = c.getCount();
        Log.d(LOG, "-- allIngrWithLvl : " + allIngrWithLvl);
        Log.d(LOG, "-- DB getIngToQuiz() numIng : " + numIng);

        for (int i = 0; i < numIng; i++) {
            //wright here
            //change original formula: 1 + (int)(Math.random() * ((allIngrWithLvl - 1) + 1));
            int random = 1 + (int)(Math.random() * ((allIngrWithLvl - 2) + 1));
            if (c != null) {
                 c.moveToPosition(random);
                    //String str;
                    /*
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG, str);
                    */
                Log.d(LOG, "- moveToPosition - OK ");
                int itemId = c.getInt(c.getColumnIndex(COLUMN_ID));
                Log.d(LOG, "- getId - OK ");
                        String  itemWithLvl = c.getString(c.getColumnIndex(COLUMN_TITLE));
                Log.d(LOG, "- getString - OK ");

                        ingrToQuiz[i] = itemId;
                        Log.d(LOG, "item for this level - " + itemWithLvl);
                        Log.d(LOG, "item for this level  ID - " + itemId);

            } else
                Log.d(LOG, "Cursor is null");
        }
        Log.d(LOG, "-- DB getIngToQuiz() numIng :  completed --");
        return  ingrToQuiz;
    }

    //get ingredients for current item
    public int [] getCurrentIngr(int num) {
        int[] ingrpack;
        String item = Integer.toString(num);
        /*
        String selection = "_id = ?";
        String randomS = Integer.toString(num);
        String [] selectionArgs = new String[]{randomS};
        c = mDB.query(DB_TABLE_ITEM, null, selection, selectionArgs, null, null, null );
        */
        String sqlQuery = "SELECT * FROM " + DB_TABLE_INGR + " WHERE " + COLUMN_ID + " = @item";
        c = mDB.rawQuery(sqlQuery, new String[] {item});
        c.moveToFirst();
        int ingr1Int = c.getColumnIndex(COLUMN_INGR_1);
        int ingr2Int = c.getColumnIndex(COLUMN_INGR_2);
        int ingr3Int = c.getColumnIndex(COLUMN_INGR_3);
        int ingr4Int = c.getColumnIndex(COLUMN_INGR_4);

        int ingr1 = c.getInt(ingr1Int);
        int ingr2 = c.getInt(ingr2Int);
        int ingr3 = c.getInt(ingr3Int);
        int ingr4 = c.getInt(ingr4Int);
        Log.d(LOG, "first ingr: " + ingr1);
        Log.d(LOG, "sevond ingr: " + ingr2);
        Log.d(LOG, "third ingr: " + ingr3);
        Log.d(LOG, "fourth ingr: " + ingr4);

        if (ingr3 == 0) {
            ingrpack = new int[2];
            ingrpack[0] = ingr1;
            ingrpack[1] = ingr2;
        } else if (ingr4 == 0) {
            ingrpack = new int[3];
            ingrpack[0] = ingr1;
            ingrpack[1] = ingr2;
            ingrpack[2] = ingr3;
        } else {
            ingrpack = new int[4];
            ingrpack[0] = ingr1;
            ingrpack[1] = ingr2;
            ingrpack[2] = ingr3;
            ingrpack[3] = ingr4;
        }
        return ingrpack;
    }

    public void getLeftIngr(int num) {
        Log.d(LOG, "-- getLeftIngr --");
        String level = Integer.toString(num);
        String sqlQuery = "SELECT * FROM " + DB_TABLE_ITEM + " WHERE " + COLUMN_LEVEL + " = @level";
        c = mDB.rawQuery(sqlQuery, new String[] {level});


    }

    public Item getRandomItem() {

        int randomArrayElement = 1 + (int)(Math.random() * ((iItems.length - 1) + 1));
        int random = iItems[randomArrayElement];
        //int random = 1;
        String selection = "_id = ?";
        String randomS = Integer.toString(random);
        String [] selectionArgs = new String[]{randomS};
        c = mDB.query(DB_TABLE_ITEM, null, selection, selectionArgs, null, null, null );
        c.moveToFirst();
        Log.d(LOG, " -----> move to first ----" + random);
        int titleInt = c.getColumnIndex(COLUMN_TITLE);
        int imgInt = c.getColumnIndex(COLUMN_IMAGE);
        int soundInt = c.getColumnIndex(COLUMN_SOUND);
        int levelInt = c.getColumnIndex(COLUMN_LEVEL);
        int goldInt = c.getColumnIndex(COLUMN_GOLD);
        int paramInt = c.getColumnIndex(COLUMN_PARAM);
        int useInt = c.getColumnIndex(COLUMN_USE);
        int infoInt = c.getColumnIndex(COLUMN_INFO);

        String title = c.getString(titleInt);
        Log.d(LOG, "Random title: " + title);
        int img = c.getInt(imgInt);
        Log.d(LOG, "Random img: " + img);
        String sound = c.getString(soundInt);
        Log.d(LOG, "Random sound: " + sound);
        int level = c.getInt(levelInt);
        Log.d(LOG, "Random level: " + level);
        int gold = c.getInt(goldInt);
        Log.d(LOG, "Random gold: " + gold);
        String param = c.getString(paramInt);
        Log.d(LOG, "Random param: " + param);
        String use = c.getString(useInt);
        Log.d(LOG, "Random use: " + use);
        String info = c.getString(infoInt);
        Log.d(LOG, "Random info: " + info);

        Item item = new Item();
        item.itemID = random;
        item.title = title;
        item.img = img;
        item.sound = sound;
        item.level = level;
        item.gold = gold;
        item.param = param;
        item.use = use;
        item.info = info;

        return item;
    }

    //get particular item
    public Item getParticularItem(int num) {
        //int random = 1;
        String selection = "_id = ?";
        String randomS = Integer.toString(num);
        String [] selectionArgs = new String[]{randomS};
        c = mDB.query(DB_TABLE_ITEM, null, selection, selectionArgs, null, null, null );
        c.moveToFirst();
        Log.d(LOG, " -----> move to first ----" + num);
        int titleInt = c.getColumnIndex(COLUMN_TITLE);
        int imgInt = c.getColumnIndex(COLUMN_IMAGE);
        int soundInt = c.getColumnIndex(COLUMN_SOUND);
        int levelInt = c.getColumnIndex(COLUMN_LEVEL);
        int goldInt = c.getColumnIndex(COLUMN_GOLD);
        int paramInt = c.getColumnIndex(COLUMN_PARAM);
        int useInt = c.getColumnIndex(COLUMN_USE);
        int infoInt = c.getColumnIndex(COLUMN_INFO);

        String title = c.getString(titleInt);
        Log.d(LOG, "Random title: " + title);
        int img = c.getInt(imgInt);
        Log.d(LOG, "Random img: " + img);
        String sound = c.getString(soundInt);
        Log.d(LOG, "Random sound: " + sound);
        int level = c.getInt(levelInt);
        Log.d(LOG, "Random level: " + level);
        int gold = c.getInt(goldInt);
        Log.d(LOG, "Random gold: " + gold);
        String param = c.getString(paramInt);
        Log.d(LOG, "Random param: " + param);
        String use = c.getString(useInt);
        Log.d(LOG, "Random use: " + use);
        String info = c.getString(infoInt);
        Log.d(LOG, "Random info: " + info);

        Item item = new Item();
        item.itemID = num;
        item.title = title;
        item.img = img;
        item.sound = sound;
        item.level = level;
        item.gold = gold;
        item.param = param;
        item.use = use;
        item.info = info;

        return item;
    }


    // ----- Class for create and handle DB -----
    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_ITEM);
            Log.d(LOG, "--- TABLE ITEM WRITING---");

            db.execSQL(DB_CREATE_INGR);
            Log.d(LOG, "--- TABLE ITEM WRITING---");
/*
            ContentValues cv = new ContentValues();
                cv.put(COLUMN_TITLE, "Poor Man's Shield");
                cv.put(COLUMN_IMAGE, 83);
                cv.put(COLUMN_SOUND, "default");
                cv.put(COLUMN_LEVEL, 2);
                cv.put(COLUMN_PARAM, "+ 6 к ловкости");
                cv.put(COLUMN_INFO, "Потрескавшийся старый щит");
                db.insert(DB_TABLE_ITEM, null, cv);
                Log.d(LOG, "--- TABLE ITEM CREATED  1---");

            ContentValues cv2 = new ContentValues();
            cv2.put(COLUMN_TITLE, "Slippers of Agility");
            cv2.put(COLUMN_IMAGE, 107);
            cv2.put(COLUMN_SOUND, "default");
            cv2.put(COLUMN_LEVEL, 1);
            cv2.put(COLUMN_PARAM, "+ 3 к ловкости");
            cv2.put(COLUMN_INFO, "Легкие ботинки, сделанные из кожи паука");
            db.insert(DB_TABLE_ITEM, null, cv2);
            Log.d(LOG, "--- TABLE ITEM CREATED  2---");

            ContentValues cv3 = new ContentValues();
            cv3.put(COLUMN_TITLE, "Stout Shield");
            cv3.put(COLUMN_IMAGE, 112);
            cv3.put(COLUMN_SOUND, "default");
            cv3.put(COLUMN_LEVEL, 1);
            cv3.put(COLUMN_PARAM, "БЛОК УРОНА");
            cv3.put(COLUMN_INFO, "Для одного это дно бочки, для другого — щит.");
            db.insert(DB_TABLE_ITEM, null, cv3);
            Log.d(LOG, "--- TABLE ITEM CREATED  3---");
*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
