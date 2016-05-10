package com.porodem.doatquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by porod on 25.04.2016.
 */
public class ItemList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    public static final String LOG_TAG = "myLogs";

    DB db;
    SimpleCursorAdapter scAdapter;
    ListView listView;

    Item item;

    TypedArray itemImg;

    //for dialog
    int btn;
    final int DIALOG = 1;
    LinearLayout view;
    TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //open DB connection
        db = new DB(this);
        db.open();

        //get resurses for all images
        Resources res = getResources();
        itemImg = res.obtainTypedArray(R.array.item_images);

        //ivQuizImg.setImageResource(itemImg.getResourceId(DB.COLUMN_IMAGE, 1));
        //item = db.getRandomItem();
        //Integer.toString(itemImg.getResourceId(Integer.parseInt(DB.COLUMN_IMAGE), 1));

        //itemImg.getResourceId(item.img, 1);

        //columns
        String[] from = new String[] {DB.COLUMN_IMAGE, DB.COLUMN_TITLE};
        int[] to = new int[] {R.id.itemIconn, R.id.tvItemName};

        //create adapter and config list

        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        listView = (ListView)findViewById(R.id.itemList);
        listView.setAdapter(scAdapter);

        //add context menu to list
        registerForContextMenu(listView);

        //create loader for data reading
        getLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = db.getParticularItem(position+1);
                showDialog(DIALOG);

                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = "
                        + id);
            }


        });



    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.check);
        //create view from dialog.xml
        view = (LinearLayout)getLayoutInflater()
                .inflate(R.layout.dialog_info, null);
        //install it as dialog body containing
        //устанавливаем ее, как содержимое тела диалога
        adb.setView(view);
        adb.setPositiveButton(R.string.add, myClickListener);
        //find textView
        tvCount = (TextView)view.findViewById(R.id.tvCount);

        return adb.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog){
        super.onPrepareDialog(id, dialog);

        if (id == DIALOG) {
            // Находим TextView для отображения названия предмета
            TextView dItemName = (TextView) dialog.getWindow().findViewById(R.id.dInfItemName);
            // Находим TextView для отображения параметров предмета
            TextView dParam = (TextView) dialog.getWindow().findViewById(
                    R.id.dInfParam);
            TextView dUse = (TextView) dialog.getWindow().findViewById(
                    R.id.dInfUse);
            TextView dInfo = (TextView) dialog.getWindow().findViewById(
                    R.id.dInfInfo);
            //Button btnAdd = (Button)dialog.getWindow().findViewById(R.id.btnApply);

            ImageView ivItem = (ImageView)dialog.getWindow().findViewById(R.id.ivInfItem);
            ImageView ivIngr1 = (ImageView)dialog.getWindow().findViewById(R.id.ivInfIngr1);
            ImageView ivIngr2 = (ImageView)dialog.getWindow().findViewById(R.id.ivInfIngr2);
            ImageView ivIngr3 = (ImageView)dialog.getWindow().findViewById(R.id.ivInfIngr3);
            ImageView ivIngr4 = (ImageView)dialog.getWindow().findViewById(R.id.ivInfIngr4);
            Log.d(LOG_TAG, "itemClick: dialog ready for: = " + item.title + ", id = "
                    + id);
            dItemName.setText(item.title);
            Log.d(LOG_TAG, "set title" + item.title);
            ivItem.setImageResource(item.img);
            Log.d(LOG_TAG, "set image" + item.img);
            dParam.setText(item.param);
            Log.d(LOG_TAG, "set param" + item.param);
            dUse.setText(item.use);
            Log.d(LOG_TAG, "set use" + item.use);
            int itemIngr[] = db.getCurrentIngr(item.itemID);
            Log.d(LOG_TAG, "getCurrentIngr OK");
            Log.d(LOG_TAG, " itemIngr.length = " + itemIngr.length);
            ivIngr1.setImageDrawable(null);
            ivIngr2.setImageDrawable(null);
            ivIngr3.setImageDrawable(null);
            ivIngr4.setImageDrawable(null);
            if (itemIngr[0] != 0) {
                if (itemIngr.length == 2) {
                    Log.d(LOG_TAG, " itemIngr contains = " + Arrays.toString(itemIngr));
                    Item firstIng = db.getParticularItem(itemIngr[0]);
                    ivIngr1.setImageResource(firstIng.img);
                    Item secondIng = db.getParticularItem(itemIngr[1]);
                    ivIngr2.setImageResource(secondIng.img);
                }
                if (itemIngr.length == 3) {
                    Log.d(LOG_TAG, " itemIngr.length = " + itemIngr.length);
                    Item firstIng = db.getParticularItem(itemIngr[0]);
                    ivIngr1.setImageResource(firstIng.img);
                    Item secondIng = db.getParticularItem(itemIngr[1]);
                    ivIngr2.setImageResource(secondIng.img);
                    Item thirdIng = db.getParticularItem(itemIngr[2]);
                    ivIngr3.setImageResource(thirdIng.img);
                }
                if (itemIngr.length == 4) {
                    Log.d(LOG_TAG, " itemIngr.length = " + itemIngr.length);
                    Item firstIng = db.getParticularItem(itemIngr[0]);
                    ivIngr1.setImageResource(firstIng.img);
                    Item secondIng = db.getParticularItem(itemIngr[1]);
                    ivIngr2.setImageResource(secondIng.img);
                    Item thirdIng = db.getParticularItem(itemIngr[2]);
                    ivIngr3.setImageResource(thirdIng.img);
                    Item fourthIng = db.getParticularItem(itemIngr[3]);
                    ivIngr4.setImageResource(fourthIng.img);
                }
            } else {
                Log.d(LOG_TAG, " itemIngr NO");
            }
        }
    }

    // Click Listener for dialog buttons
    //******************************
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

        }
    };



    protected void onDestroy() {
        super.onDestroy();
        //close connect when exit
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void testListBtn(View view) {
        item = db.getRandomItem();
        int t = item.img;
        Log.d(LOG_TAG, "TEST BUTTON for img res: " + t);
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        //String cc = MainActivity.this.concreteCompany;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
            //MainActivity.this.cc = ccom;
        }
        @Override
        public Cursor loadInBackground() {

            Cursor cursor = db.getAllData();
            try {
                TimeUnit.SECONDS.sleep(1);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;


        }
    }

}
