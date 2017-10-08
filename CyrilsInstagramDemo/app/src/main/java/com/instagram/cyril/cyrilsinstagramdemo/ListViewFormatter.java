package com.instagram.cyril.cyrilsinstagramdemo;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by cyril on 07/10/2017.
 */

public class ListViewFormatter {
    int dpToPx;
    int screenWidth;

    public ListViewFormatter(int value, int screenWidth) {
        this.dpToPx = value;
        this.screenWidth = screenWidth;
    }

    public void setListViewSize(ListView myListView, int topHeight) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View listItem = null;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            listItem = myListAdapter.getView(size, null, myListView);
            if (listItem instanceof ViewGroup) listItem.setLayoutParams(lp);
            listItem.measure(widthMeasureSpec, heightMeasureSpec);
            totalHeight += listItem.getMeasuredHeight();
        }
        totalHeight += myListView.getPaddingTop() + myListView.getPaddingBottom();
        totalHeight += (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight;
        myListView.setLayoutParams(params);
        myListView.requestLayout();

        Log.e("height of listView:", String.valueOf(totalHeight));
    }

    public void setGridViewSize(GridView myGridView, String width) {
        ListAdapter myListAdapter = myGridView.getAdapter();
        if (myListAdapter == null) {
            return;
        }

        View myview = myListAdapter.getView(0, null, myGridView);
        myview.measure(0, 0);
        int columnCount = Math.round(Integer.parseInt(width) / myview.getMeasuredWidth());
        int totalHeight = 0;
        Log.e("Colum", "Column count is  ********           " + columnCount + " screenwidth is    " + width);
        for (int size = 0; size < Math.round(myListAdapter.getCount() / 3) + 1; size++) {
            View listItem = myListAdapter.getView(size, null, myGridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myGridView.getLayoutParams();
        params.height = totalHeight;
        //  params.width=Integer.getInteger(width)-30;
        params.leftMargin = 15;
        params.rightMargin = 15;
        myGridView.setLayoutParams(params);
        // print height of adapter on log
        Log.e("height of gridview:", String.valueOf(totalHeight));
    }


}
