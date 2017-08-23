package com.example.lee.noqngo;

/**
 * Created by jang on 2017-06-04.
 */

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GridAdapter extends ArrayAdapter<Griditem> {

    private List<Griditem> items;
    private LayoutInflater inflater;

    public GridAdapter(Context context, int textViewResourceId,
                           List<Griditem> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
        }

        // 현재 position 의 내용을 View 로 작성하여 리턴
        Griditem item = (Griditem) items.get(position);

        if (item != null) {

            TextView bookmarkName = (TextView) view.findViewById(R.id.bookmark_name);
            bookmarkName.setTypeface(Typeface.DEFAULT_BOLD);
            String A = Integer.toString(item.getNum());
            bookmarkName.setText(A);

            //    TextView bookmarkUrl = (TextView) view.findViewById(R.id.bookmark_url);
            //    bookmarkUrl.setText(item.getUrl());

        }
        return view;
    }

}