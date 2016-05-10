package com.porodem.doatquiz;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by porod on 25.04.2016.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;


    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid ) {
        super(context, R.layout.activity_list, itemname);

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item, null, true);

        ImageView imageView = (ImageView)rowView.findViewById(R.id.itemIconn);
        TextView textView = (TextView)rowView.findViewById(R.id.tvItemName);

        imageView.setImageResource(imgid[position]);
        textView.setText(itemname[position]);
        return rowView;

    }
}
