package com.porodem.doatquiz;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by porod on 27.04.2016.
 */
public class Test extends AppCompatActivity {

    private ListView listView;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private DB daOdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
